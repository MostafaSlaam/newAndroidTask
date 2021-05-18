package android.task.network_connection

import android.os.AsyncTask
import android.task.BuildConfig
import android.task.util.Preferences
import android.task.util.appId
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.observers.DisposableObserver
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ConnectionHandler {
    companion object {
        fun getInstance(): ConnectionHandler = ConnectionHandler()
    }

    private var retrofit: Retrofit? = null

    internal fun getClient(): Retrofit? {
        retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(
                getUnsafeOkHttpClient().connectTimeout(40, TimeUnit.HOURS)
                    .readTimeout(60, TimeUnit.MINUTES)
                    .writeTimeout(60, TimeUnit.MINUTES).build()
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        return retrofit
    }

    internal fun getClientWithCustomUrl(baseUrl: String): Retrofit? {
        retrofit = Retrofit.Builder().baseUrl(baseUrl).client(
                getUnsafeOkHttpClient().connectTimeout(40, TimeUnit.HOURS)
                    .readTimeout(60, TimeUnit.MINUTES)
                    .writeTimeout(60, TimeUnit.MINUTES).build()
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    internal fun getClientWithoutBaseUrl(): Retrofit? {
        retrofit = Retrofit.Builder().client(
                getUnsafeOkHttpClient().connectTimeout(40, TimeUnit.HOURS)
                    .readTimeout(60, TimeUnit.MINUTES)
                    .writeTimeout(60, TimeUnit.MINUTES).build()
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }
            builder.addNetworkInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader("Connection", "close").build()
                chain.proceed(request)
            }
            return builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun startGetMethodWithCustomUrl(
        token: String,
        baseUrl: String,
        urlFinction: String,
        params: MutableMap<String, Any?>,
        connectionCallback: ConnectionCallback
    ) {
        val apiInterface = getClientWithCustomUrl(baseUrl)?.create(APIInterface::class.java)
        val call = apiInterface?.doGetConnection("Bearer $token", urlFinction, params)
        connectionCallback.onStartConnection()
        call?.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response != null && response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response?.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(response.message().toString())
                    }
                } else {
                    connectionCallback.onFailureConnection(response.message().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                connectionCallback.onFailureConnection(t.message)
            }
        })
    }

    fun startDownloadFile(
        folderName: String,
        filePath: String,
        urlFunction: String,
        connectionCallback: ConnectionCallback
    ) {
        val apiInterface =
            getClient()?.create(APIInterface::class.java)
//        var urlFunction1 = urlFunction.removePrefix("https://edara.com/Product/")
        val call = apiInterface?.downloadFile(urlFunction)
        connectionCallback.onStartConnection()
        call?.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    if (response.isSuccessful) {
                        class saveDownloadStreamingTask : AsyncTask<Void, Void, String>() {
                            override fun doInBackground(vararg params: Void?): String? {
                                var iStream: BufferedInputStream? = null
                                var output: FileOutputStream? = null
                                try {
                                    val body = response.body()
                                    iStream = BufferedInputStream(body!!.byteStream())
                                    try {
                                        output = FileOutputStream(filePath)
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        val wallpaperDirectory = File(folderName)
                                        wallpaperDirectory.mkdirs()
                                        output = FileOutputStream(filePath)
                                    }
                                    response.body()
                                    val buffer = ByteArray(1024)
                                    var total: Long = 0
                                    var count: Int
                                    do {
                                        count = iStream.read(buffer)
                                        if (count == -1) {
                                            if (total < response.body()!!.contentLength())
                                                connectionCallback.onFailureConnection("")
                                            break
                                        }
                                        total += count.toLong()
                                        output!!.write(buffer, 0, count)
                                    } while (true)
                                    if (total == response.body()!!.contentLength()) {
                                        connectionCallback.onSuccessConnection("")
                                    } else {
                                        connectionCallback.onFailureConnection("")
                                    }
                                } catch (e: IOException) {
                                    connectionCallback.onFailureConnection("")
                                } finally {
                                    iStream?.close()
                                    output?.close()
                                }
                                return null
                            }
                        }
                        saveDownloadStreamingTask().execute()
                    } else {
                        connectionCallback.onFailureConnection("")
                    }
                } catch (e: IOException) {
                    connectionCallback.onFailureConnection("")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                connectionCallback.onFailureConnection(t.message)
                connectionCallback.onFailureConnection("")
            }
        })
    }

    class saveDownloadStreamingTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            // ...

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            // ...
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // ...
        }
    }

    fun startGetTwoApisMethodUsingRX(
        token: String,
        urlFunction1: String,
        params1: MutableMap<String, Any>,
        urlFunction2: String,
        params2: MutableMap<String, Any>,
        connectionCallback: ConnectionCallback
    ): Disposable {
        val apiInterface = getClient()?.create(APIInterface::class.java)
        var result = ArrayList<Response<String>>()
        return apiInterface?.doGetConnectionUsingRX(
                "application/json", "application/json",
                Preferences.getApplicationLocale(),
                appId,
                "Bearer $token",
                urlFunction1,
                params1
            )!!.flatMap { response1: Response<String> ->
                result.add(response1)
                return@flatMap apiInterface.doGetConnectionUsingRX(
                    "application/json", "application/json",
                    Preferences.getApplicationLocale(),
                    appId,
                    "Bearer $token",
                    urlFunction2,
                    params2
                )
            }.map { response2: Response<String> ->
                result.add(response2)
                return@map result
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { dis ->
                connectionCallback.onStartConnection()
            }
            .subscribe(
                { response: ArrayList<Response<String>> ->
                    if (response[0].code() == 200 && response[0].isSuccessful
                        && response[1].code() == 200 && response[1].isSuccessful
                    ) {
                        var allResults = ArrayList<String>()
                        allResults.add(response[0].body().toString())
                        allResults.add(response[1].body().toString())
                        connectionCallback.onSuccessConnection(allResults)
                    } else if (response[0].code() == 401 && response[0].errorBody() != null) {
                        try {
                            //error response 1
                            connectionCallback.onLoginAgain(
                                JSONObject(
                                    response[0].errorBody()!!.string()
                                ).toString()
                            )
                        } catch (e: Exception) {
                            connectionCallback.onLoginAgain(e.message)
                        }
                    } else if (response[1].code() == 401 && response[1].errorBody() != null) {
                        try {
                            connectionCallback.onLoginAgain(
                                JSONObject(
                                    response[1].errorBody()!!.string()
                                ).toString()
                            )
                        } catch (e: Exception) {
                            connectionCallback.onLoginAgain(e.message)
                        }
                    } else if (response[0].errorBody() != null) {
                        try {
                            connectionCallback.onFailureConnection(
                                JSONObject(
                                    response[0].errorBody()!!.string()
                                ).toString()
                            )
                        } catch (e: Exception) {
                            connectionCallback.onFailureConnection(e.message)
                        }
                    } else if (response[1].errorBody() != null) {
                        try {
                            connectionCallback.onFailureConnection(
                                JSONObject(
                                    response[1].errorBody()!!.string()
                                ).toString()
                            )
                        } catch (e: Exception) {
                            connectionCallback.onFailureConnection(e.message)
                        }
                    } else {
                        connectionCallback.onFailureConnection("")
                    }
                }, { t: Throwable ->
                    //                connectionCallback.onFailureConnection(t.message)
                    connectionCallback.onFailureConnection(t.message)
                })
    }




    fun startGetMethodUsingRX(
        token: String,
        urlFunction: String,
        params: MutableMap<String, Any>,
        connectionCallback: ConnectionCallback
    ): Disposable {
        val apiInterface = getClient()?.create(APIInterface::class.java)
        return apiInterface?.doGetConnectionUsingRX(
                "application/json", "application/json",
                Preferences.getApplicationLocale(),
                appId,
                "Bearer $token",
                urlFunction,
                params
            )!!.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { dis ->
                connectionCallback.onStartConnection()
            }
            .subscribe(
                { response: Response<String> ->
                    if (response.code() == 200 && response.isSuccessful) {
                        connectionCallback.onSuccessConnection(response.body().toString())
                    } else if (response.code() == 401 && response.errorBody() != null) {
                        try {
                            connectionCallback.onLoginAgain(
                                JSONObject(
                                    response.errorBody()!!.string()
                                ).toString()
                            )
//                            connectionCallback.onLoginAgain(response.errorBody().toString())
                        } catch (e: Exception) {
//                        connectionCallback.onLoginAgain(response.message().toString())
                            connectionCallback.onLoginAgain(e.message)
                        }
                    } else if (response.errorBody() != null) {
                        try {
                            connectionCallback.onFailureConnection(
                                JSONObject(
                                    response.errorBody()!!.string()
                                ).toString()
                            )
//                            connectionCallback.onFailureConnection(response.errorBody().toString())
                        } catch (e: Exception) {
//                        connectionCallback.onFailureConnection(response.message().toString())
                            connectionCallback.onFailureConnection(e.message)
                        }
                    } else {
//                    connectionCallback.onFailureConnection(response.message().toString())
                        connectionCallback.onFailureConnection("")
                    }
                }, { t: Throwable ->
                    //                connectionCallback.onFailureConnection(t.message)
                    connectionCallback.onFailureConnection(t.message)
                })
    }

    fun startPostMethodUsingRX(
        token: String,
        urlFunction: String,
        params: MultipartBody,
        connectionCallback: ConnectionCallback
    ): Disposable {
        val apiInterface = getClient()?.create(APIInterface::class.java)
        return apiInterface?.doPostConnectionUsingRX(
                Preferences.getApplicationLocale(), appId,
                "Bearer $token",
                urlFunction,
                params
            )!!.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                connectionCallback.onStartConnection()
            }
            .subscribe({ response: Response<String> ->
                if (response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response.code() == 401 && response.errorBody() != null) {
                    try {
                        connectionCallback.onLoginAgain(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
//                            connectionCallback.onLoginAgain(response.errorBody().toString())
                    } catch (e: Exception) {
//                        connectionCallback.onLoginAgain(response.message().toString())
                        connectionCallback.onLoginAgain(e.message)
                    }
                } else if (response.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
//                        connectionCallback.onFailureConnection(response.errorBody().toString())
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(e.message)
                    }
                } else {
                    connectionCallback.onFailureConnection("")
                }
            }, { t: Throwable ->
                connectionCallback.onFailureConnection(t.message)
            })
    }

    fun startPostMethodWithGSONParamsUsingRX(
        token: String,
        urlFunction: String,
        params: MutableMap<String, Any>,
        connectionCallback: ConnectionCallback
    ): Disposable {
        val apiInterface = getClient()?.create(APIInterface::class.java)
        val json = Gson().toJson(params)
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json
        )

        return apiInterface?.doPostConnectionUsingRX(
                "application/json", "application/json", Preferences.getApplicationLocale(), appId,
                "Bearer $token",
                urlFunction,
                body
            )!!.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                connectionCallback.onStartConnection()
            }
            .subscribe({ response: Response<String> ->
                if (response != null && response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response?.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
//                        connectionCallback.onFailureConnection(response.errorBody().toString())
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(e.message)
                    }
                } else {
                    connectionCallback.onFailureConnection("")
                }
            }, { t: Throwable ->
                connectionCallback.onFailureConnection(t.message)
            })
    }

    fun startGetMethod(
        token: String,
        urlFunction: String,
        params: MutableMap<String, Any?>,
        connectionCallback: ConnectionCallback
    ) {
        val apiInterface = getClient()?.create(APIInterface::class.java)
        val call = apiInterface?.doGetConnection("Bearer $token", urlFunction, params)
        connectionCallback.onStartConnection()
        call?.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response != null && response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response?.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(response.message().toString())
                    }
                } else {
                    connectionCallback.onFailureConnection(response.message().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                connectionCallback.onFailureConnection(t.message)
            }
        })
    }

    fun startPostMethod(
        token: String,
        urlFunction: String,
        params: MultipartBody,
        connectionCallback: ConnectionCallback
    ) {
        val apiInterface = getClient()?.create(APIInterface::class.java)
        val call = apiInterface?.doPostConnection("Bearer $token", urlFunction, params)
        connectionCallback.onStartConnection()
        call?.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response != null && response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response?.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(response.message().toString())
                    }
                } else {
                    connectionCallback.onFailureConnection(response.message().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                connectionCallback.onFailureConnection(t.message)
            }
        })
    }

    fun startPostMethodWithGSONParams(
        token: String,
        urlFinction: String,
        params: MutableMap<String, Any?>,
        connectionCallback: ConnectionCallback
    ) {
        val apiInterface = getClient()?.create(APIInterface::class.java)
        val json = Gson().toJson(params)
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json
        )
        val call = apiInterface?.doPostConnection("Bearer $token", urlFinction, body)
        connectionCallback.onStartConnection()
        call?.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response != null && response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response?.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(response.message().toString())
                    }
                } else {
                    connectionCallback.onFailureConnection(response.message().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                connectionCallback.onFailureConnection(t.message)
            }
        })
    }
}