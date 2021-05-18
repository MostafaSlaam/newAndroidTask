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
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.shababit.observer.OnRecyclerItemClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.w3c.dom.Document


open class MainActivityViewModel(
    application: MyApplication
) : BaseActivityViewModel(application) {
    lateinit var observer: Observer
    var isShowLoader = MutableLiveData<Boolean>()
    var isShowError = MutableLiveData<Boolean>()
    var connectionErrorMessage = MutableLiveData<String>()
    var arrayList = ArrayList<WordModel>()
    var adapter: RecyclerWordsAdapter
    lateinit var webView: WebView

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
                Handler(Looper.getMainLooper()).postDelayed({
                    setData(html)
                }, 10000)

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
                var html =Preferences.getHtmlData()
                if (html.isEmpty()) {
                    connectionErrorMessage.value = description
                    isShowLoader.value = false
                    isShowError.value = true
                }
                else
                    setData(html)
            }
        })
        webView.loadUrl("https://www.alalmiyalhura.com")
//        webView.loadUrl("https://www.filgoal.com")
    }

    fun setData(html:String){
        isShowLoader.value = true
        Preferences.saveHtmlData(html)
        prepareListWords(html)
        adapter.setList(arrayList)
        isShowLoader.value = false
    }

    fun prepareListWords(html: String) {

        arrayList.clear()
        var doc: org.jsoup.nodes.Document? = Jsoup.parse(html)
        var regex=Regex("[^a-zA-Zء-ي0-9]")
//
        var text = doc!!.text().replace(regex," ")
//        {
//            it.isLetterOrDigit() || it.isWhitespace()
//        }
        var wordList = text.split(" ")
        for (word in wordList) {
//            var oldCount = words.get(word)
//            if (oldCount == null)
//                oldCount = 0
//            words.put(word, oldCount + 1)
            var index = getWordIndex(word)
            if (index == -1) {
                if (word.isNotEmpty())
                    arrayList.add(WordModel(word, "1"))
            } else
                arrayList.get(index).count =
                    ((arrayList.get(index).count).toInt() + 1).toString()
        }

    }

    fun getWordIndex(word: String): Int {
        for (index in arrayList.indices)
            if (arrayList[index].word.compareTo(word) == 0)
                return index
        return -1
    }

    interface Observer {
    }


}