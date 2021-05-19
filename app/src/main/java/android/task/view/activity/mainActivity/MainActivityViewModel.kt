package android.task.view.activity.mainActivity

import android.R.id.text2
import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.task.MyApplication
import android.task.R
import android.task.adapter.RecyclerWordsAdapter
import android.task.model.WordModel
import android.task.network_connection.*
import android.task.observer.showHtml
import android.task.util.Preferences
import android.task.util.WebAppInterface
import android.task.view.activity.baseActivity.BaseActivityViewModel
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.shababit.observer.OnRecyclerItemClickListener
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.w3c.dom.Document
import java.lang.Runnable


open class MainActivityViewModel(
    application: MyApplication
) : BaseActivityViewModel(application) {
    lateinit var observer: Observer
    var isShowLoader = MutableLiveData<Boolean>()
    var isShowError = MutableLiveData<Boolean>()
    var connectionErrorMessage = MutableLiveData<String>()

    //    var arrayList = ArrayList<WordModel>()
    var adapter: RecyclerWordsAdapter
    lateinit var webView: WebView
    var cout = 0

    init {

//        connectionErrorMessage.value = ""
        adapter = RecyclerWordsAdapter(ArrayList(), object : OnRecyclerItemClickListener {
            override fun onRecyclerItemClickListener(position: Int) {

            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun startFunc() {
        isShowError.value = false
        isShowLoader.value = true
        webView = WebView(application.context)
        webView.settings.javaScriptEnabled = true
        webView.isHorizontalScrollBarEnabled = true
        webView.isVerticalScrollBarEnabled = true
        webView.clearCache(true)
        webView.addJavascriptInterface(WebAppInterface(object : showHtml {
            override fun showHtml(html: String) {
                scope.launch(Dispatchers.IO) { setData(html) }

            }
        }), "Android")
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:window.Android.showHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                var html = Preferences.getHtmlData()
                if (html.isEmpty()) {
                    connectionErrorMessage.value = description
                    isShowLoader.value = false
                    isShowError.value = true
                } else
                    scope.launch(Dispatchers.IO) { setData(html) }

            }
        })
        webView.loadUrl("https://www.alalmiyalhura.com")
    }

   suspend fun setData(html: String) {
        isShowLoader.postValue(true)
        Preferences.saveHtmlData(html)
        Handler(Looper.getMainLooper()).post(
            Runnable {
                adapter.setList(prepareListWords(html))
            })

        isShowLoader.postValue(false)
//        return true
    }

    fun prepareListWords(html: String): ArrayList<WordModel> {

        var arrayList = ArrayList<WordModel>()
        val doc: org.jsoup.nodes.Document? = Jsoup.parse(html)
        val regex = Regex("[^a-zA-Zء-ي0-9]")
        var text = doc!!.text().replace(regex, " ")
        var wordList = text.split(" ")
        for (word in wordList) {

            var index = getWordIndex(arrayList, word)
            if (index == -1) {
                if (word.isNotEmpty())
                    arrayList.add(WordModel(word, "1"))
            } else
                arrayList.get(index).count =
                    ((arrayList.get(index).count).toInt() + 1).toString()
        }

        return arrayList
    }

    fun getWordIndex(arrayList: ArrayList<WordModel>, word: String): Int {
        for (index in arrayList.indices)
            if (arrayList[index].word.compareTo(word) == 0)
                return index
        return -1
    }

    interface Observer {
    }


}