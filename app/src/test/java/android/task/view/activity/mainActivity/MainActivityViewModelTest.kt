package android.task.view.activity.mainActivity

import android.app.Application
import android.task.MyApplication
import android.task.model.WordModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Assert.*
import org.junit.Test

class MainActivityViewModelTest{
    private val viewModel=MainActivityViewModel(MyApplication())

    @Test
    fun testGetIndexWord()
    {
        val items=ArrayList<WordModel>()
        items.add(WordModel("test","5"))
        val result1=viewModel.getWordIndex(items,"test")
        val result2=viewModel.getWordIndex(items,"mostafa")
        assertEquals(0,result1)
        assertEquals(-1,result2)
    }
    @Test
    fun testPrepareListOfWords()
    {
        val html="<!doctype html>\n" +
                "<html lang=\"ar\" data-n-head=\"%7B%22lang%22:%7B%221%22:%22ar%22%7D%7D\">\n" +
                "\n" +
                "<head>\n" +
                "\t<title>العالمية الحرة لتقنية المعلومات</title>\n" +
                "\t<meta data-n-head=\"1\" charset=\"utf-8\">\n" +
                "\t<meta data-n-head=\"1\" name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n" +
                "\t<link data-n-head=\"1\" rel=\"icon\" type=\"image/x-icon\" href=\"/icon.png\">\n" +
                "\t<base href=\"/\">\n" +
                "\t<link rel=\"preload\" href=\"/_nuxt/24d7bb2.js\" as=\"script\">\n" +
                "\t<link rel=\"preload\" href=\"/_nuxt/ee27594.js\" as=\"script\">\n" +
                "\t<link rel=\"preload\" href=\"/_nuxt/1a76bef.js\" as=\"script\">\n" +
                "\t<link rel=\"preload\" href=\"/_nuxt/f2b2914.js\" as=\"script\">\n" +
                "\t<script>\n" +
                "\t\t(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':\n" +
                "        new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],\n" +
                "        j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=\n" +
                "        'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);\n" +
                "        })(window,document,'script','dataLayer','GTM-NQS9BZB');\n" +
                "\t</script>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\t<div id=\"__nuxt\">\n" +
                "\t\t<style>\n" +
                "\t\t\t#nuxt-loading {\n" +
                "\t\t\t\tbackground: #fff;\n" +
                "\t\t\t\tvisibility: hidden;\n" +
                "\t\t\t\topacity: 0;\n" +
                "\t\t\t\tposition: absolute;\n" +
                "\t\t\t\tleft: 0;\n" +
                "\t\t\t\tright: 0;\n" +
                "\t\t\t\ttop: 0;\n" +
                "\t\t\t\tbottom: 0;\n" +
                "\t\t\t\tdisplay: flex;\n" +
                "\t\t\t\tjustify-content: center;\n" +
                "\t\t\t\talign-items: center;\n" +
                "\t\t\t\tflex-direction: column;\n" +
                "\t\t\t\tanimation: nuxtLoadingIn 10s ease;\n" +
                "\t\t\t\t-webkit-animation: nuxtLoadingIn 10s ease;\n" +
                "\t\t\t\tanimation-fill-mode: forwards;\n" +
                "\t\t\t\toverflow: hidden\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t@keyframes nuxtLoadingIn {\n" +
                "\t\t\t\t0% {\n" +
                "\t\t\t\t\tvisibility: hidden;\n" +
                "\t\t\t\t\topacity: 0\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\t20% {\n" +
                "\t\t\t\t\tvisibility: visible;\n" +
                "\t\t\t\t\topacity: 0\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\t100% {\n" +
                "\t\t\t\t\tvisibility: visible;\n" +
                "\t\t\t\t\topacity: 1\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t@-webkit-keyframes nuxtLoadingIn {\n" +
                "\t\t\t\t0% {\n" +
                "\t\t\t\t\tvisibility: hidden;\n" +
                "\t\t\t\t\topacity: 0\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\t20% {\n" +
                "\t\t\t\t\tvisibility: visible;\n" +
                "\t\t\t\t\topacity: 0\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\t100% {\n" +
                "\t\t\t\t\tvisibility: visible;\n" +
                "\t\t\t\t\topacity: 1\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t#nuxt-loading>div,\n" +
                "\t\t\t#nuxt-loading>div:after {\n" +
                "\t\t\t\tborder-radius: 50%;\n" +
                "\t\t\t\twidth: 5rem;\n" +
                "\t\t\t\theight: 5rem\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t#nuxt-loading>div {\n" +
                "\t\t\t\tfont-size: 10px;\n" +
                "\t\t\t\tposition: relative;\n" +
                "\t\t\t\ttext-indent: -9999em;\n" +
                "\t\t\t\tborder: .5rem solid #f5f5f5;\n" +
                "\t\t\t\tborder-left: .5rem solid #d3d3d3;\n" +
                "\t\t\t\t-webkit-transform: translateZ(0);\n" +
                "\t\t\t\t-ms-transform: translateZ(0);\n" +
                "\t\t\t\ttransform: translateZ(0);\n" +
                "\t\t\t\t-webkit-animation: nuxtLoading 1.1s infinite linear;\n" +
                "\t\t\t\tanimation: nuxtLoading 1.1s infinite linear\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t#nuxt-loading.error>div {\n" +
                "\t\t\t\tborder-left: .5rem solid #ff4500;\n" +
                "\t\t\t\tanimation-duration: 5s\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t@-webkit-keyframes nuxtLoading {\n" +
                "\t\t\t\t0% {\n" +
                "\t\t\t\t\t-webkit-transform: rotate(0);\n" +
                "\t\t\t\t\ttransform: rotate(0)\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\t100% {\n" +
                "\t\t\t\t\t-webkit-transform: rotate(360deg);\n" +
                "\t\t\t\t\ttransform: rotate(360deg)\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t@keyframes nuxtLoading {\n" +
                "\t\t\t\t0% {\n" +
                "\t\t\t\t\t-webkit-transform: rotate(0);\n" +
                "\t\t\t\t\ttransform: rotate(0)\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\t100% {\n" +
                "\t\t\t\t\t-webkit-transform: rotate(360deg);\n" +
                "\t\t\t\t\ttransform: rotate(360deg)\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t</style>\n" +
                "\t\t<script>\n" +
                "\t\t\twindow.addEventListener(\"error\",function(){var e=document.getElementById(\"nuxt-loading\");e&&(e.className+=\" error\")})\n" +
                "\t\t</script>\n" +
                "\t\t<div id=\"nuxt-loading\" aria-live=\"polite\" role=\"status\">\n" +
                "\t\t\t<div>Loading...</div>\n" +
                "\t\t</div>\n" +
                "\t</div>\n" +
                "\t<script>\n" +
                "\t\twindow.__NUXT__={config:{_app:{basePath:\"/\",assetsPath:\"/_nuxt/\",cdnURL:null}}}\n" +
                "\t</script>\n" +
                "\t<iframe src=\"https://www.googletagmanager.com/ns.html?id=GTM-NQS9BZB\" height=\"0\" width=\"0\"\n" +
                "\t\tstyle=\"display:none;visibility:hidden\"></iframe>\n" +
                "\t<script src=\"/_nuxt/24d7bb2.js\"></script>\n" +
                "\t<script src=\"/_nuxt/ee27594.js\"></script>\n" +
                "\t<script src=\"/_nuxt/1a76bef.js\"></script>\n" +
                "\t<script src=\"/_nuxt/f2b2914.js\"></script>\n" +
                "</body>\n" +
                "\n" +
                "</html>"


        assertEquals(5, viewModel.prepareListWords(html).size)
    }
}