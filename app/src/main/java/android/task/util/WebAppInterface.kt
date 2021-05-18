package android.task.util

import android.task.observer.showHtml
import android.webkit.JavascriptInterface

class WebAppInterface(var showHtmlObserver: showHtml) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showHtml(toast: String) {
        showHtmlObserver.showHtml(toast)
//                Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
//            binding.tvTextView.post {
//                val doc: org.jsoup.nodes.Document? = Jsoup.parse(toast)
//
//                binding.tvTextView.text=doc!!.text()
//            }
    }
}