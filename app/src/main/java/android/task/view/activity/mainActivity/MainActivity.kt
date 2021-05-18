package android.task.view.activity.mainActivity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.task.R
import android.task.databinding.ActivityMainBinding
import android.task.observer.showHtml
import android.task.util.WebAppInterface
import android.task.view.activity.baseActivity.BaseActivity
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.jsoup.Jsoup


class MainActivity : BaseActivity(
    R.string.main,
    true, false, false, false, true
), MainActivityViewModel.Observer {
    lateinit var binding: ActivityMainBinding

    override fun doOnCreate(arg0: Bundle?) {
        binding =
            putContentView(R.layout.activity_main) as ActivityMainBinding
        binding.viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application)
        )
            .get(MainActivityViewModel::class.java)
        binding.viewModel!!.observer = this

        binding.lifecycleOwner = this
        initializeViews()
        setListener()
    }

    override fun initializeViews() {
        binding.viewModel!!.startFunc()
    }

    override fun setListener() {
    }



}