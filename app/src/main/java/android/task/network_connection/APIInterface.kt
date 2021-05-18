package android.task.network_connection

import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import okhttp3.ResponseBody
import retrofit2.http.Streaming
import retrofit2.http.GET

interface APIInterface {

    @GET("{function}")
    abstract fun doGetConnection(
        @Header("Authorization") token: String, @Path(
            value = "function",
            encoded = true
        ) function: String, @QueryMap params: MutableMap<String, Any?>
    ): Call<String>

    @POST("{function}")
    abstract fun doPostConnection(
        @Header("Authorization") token: String, @Path(
            value = "function",
            encoded = true
        ) function: String, @Body params: RequestBody
    ): Call<String>

    @Streaming
    @GET("{function}")
    fun downloadFile(
        @Path(
            value = "function",
            encoded = true
        ) function: String
    ): Call<ResponseBody>


    @GET("{function}")
    abstract fun doGetConnectionUsingRX(
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Header("Accept-Language") language: String,
        @Header("app-id") appId: String,
        @Header("Authorization") token: String,
        @Path(
            value = "function",
            encoded = true
        ) function: String, @QueryMap params: MutableMap<String, Any>
    ): Single<Response<String>>

    @POST("{function}")
    abstract fun doPostConnectionUsingRX(
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Header("Accept-Language") language: String,
        @Header("app-id") appId: String,
        @Header("Authorization") token: String,
        @Path(
            value = "function",
            encoded = true
        ) function: String, @Body params: RequestBody
    ): Single<Response<String>>

    @POST("{function}")
    abstract fun doPostConnectionUsingRX(
        @Header("Accept-Language") language: String,
        @Header("app-id") appId: String,
        @Header("Authorization") token: String,
        @Path(
            value = "function",
            encoded = true
        ) function: String, @Body params: RequestBody
    ): Single<Response<String>>

}