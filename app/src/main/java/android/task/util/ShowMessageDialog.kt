package android.task.util

import android.os.Bundle
import android.task.view.sub.PopupDialogAskUserAction
import androidx.appcompat.app.AppCompatActivity
import com.shababit.observer.OnAskUserAction

fun showMessage(
    activity: AppCompatActivity,
    title: String,
    msg: String,
    onAskUserAction: OnAskUserAction?,
    isShowNegativeButton: Boolean,
    negativeText: String,
    positiveText: String,
    isCancelable: Boolean
) {
    if (activity.isFinishing)
        return
    var popupDialogAskUserAction = PopupDialogAskUserAction()
    popupDialogAskUserAction.setOnAskUserActionObserver(object : OnAskUserAction {
        override fun onPositiveAction() {
            onAskUserAction?.onPositiveAction()
        }

        override fun onNegativeAction() {
            onAskUserAction?.onNegativeAction()
        }
    })
    var bundle = Bundle()
    bundle.putString("title", title)
    bundle.putString("body", msg)
    bundle.putString("negativeButtonText", negativeText)
    bundle.putString("positiveButtonText", positiveText)
    bundle.putBoolean("isShowTitle", true)
    bundle.putBoolean("isShowNegativeButton", isShowNegativeButton)
    bundle.putBoolean("isShowPositiveButton", true)
    popupDialogAskUserAction.arguments = bundle
    popupDialogAskUserAction.isCancelable = isCancelable
    popupDialogAskUserAction.show(activity.supportFragmentManager, "PopupDialogAskUserAction")
}