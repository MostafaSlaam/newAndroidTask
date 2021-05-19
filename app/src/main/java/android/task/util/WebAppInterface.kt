package android.task.util

import android.task.observer.showHtml
import android.webkit.JavascriptInterface

class WebAppInterface(var showHtmlObserver: showHtml) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showHtml(toast: String) {
        showHtmlObserver.showHtml(toast)
    }
}