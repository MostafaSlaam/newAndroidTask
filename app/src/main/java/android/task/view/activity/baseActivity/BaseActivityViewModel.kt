package android.task.view.activity.baseActivity

import android.task.MyApplication
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


open class BaseActivityViewModel (
    var application: MyApplication
) : AndroidViewModel(application) {
    lateinit var baseViewModelObserver: BaseViewModelObserver
    var baseCompositeDisposable = CompositeDisposable()
    var parentJob = Job()
    var compositeDisposable = CompositeDisposable()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    val scope = CoroutineScope(coroutineContext)
    init {
    }

    fun onButtonBackClicked() {
        baseViewModelObserver.onBackButtonClicked()
    }

    fun onButtonMenuClicked() {
        baseViewModelObserver.onMenuButtonClicked()
    }

    fun onButtonAnyClicked() {
        baseViewModelObserver.onAnyButtonClicked()
    }

    fun onLoginAgain() {
        baseViewModelObserver.onLoginAgain()
    }


    interface BaseViewModelObserver {
        fun onBackButtonClicked()
        fun onMenuButtonClicked()
        fun onAnyButtonClicked()
        fun onLoginAgain()
        fun onRestartApp(message: String)
    }
}