package com.shababi.view.activity.baseActivity

import android.task.MyApplication
import android.task.view.activity.baseActivity.BaseActivityViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class BaseViewModelFactory(
    var application: MyApplication
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BaseActivityViewModel(application) as T
    }
}