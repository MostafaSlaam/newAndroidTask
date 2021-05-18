package android.task.network_connection

import android.task.MyApplication
import android.task.util.Preferences
import androidx.multidex.BuildConfig

import okhttp3.MultipartBody
import kotlin.collections.HashMap

object DefaultParams {
    fun getDefaultParams(
        application: MyApplication,
        params: HashMap<String, Any>
    ): MutableMap<String, Any> {
        params["notification_Token"] = Preferences.getUserToken()
        params["lang"] = Preferences.getApplicationLocale()
        params["version_code"] = BuildConfig.VERSION_CODE.toString()
        params["os_version"] = application.getOSVersion()
        params["mobile_model"] = application.getDeviceModel()
        params["applicationId"] = "0"
        params["device_type"] = "1"
        params["android"] = true
        return params
    }

    fun getDefaultParams(
        application: MyApplication,
        builder: MultipartBody.Builder
    ): MultipartBody.Builder {
        builder.setType(MultipartBody.FORM)
        var token = Preferences.getUserToken()
        builder.addFormDataPart("notification_Token", token)
        builder.addFormDataPart("lang", Preferences.getApplicationLocale())
        builder.addFormDataPart("version_code", BuildConfig.VERSION_CODE.toString())
        builder.addFormDataPart("os_version", application.getOSVersion())
        builder.addFormDataPart("mobile_model", application.getDeviceModel())
        builder.addFormDataPart("applicationId", "0")
        builder.addFormDataPart("device_type", "1")
        builder.addFormDataPart("android", "true")
        return builder
    }
}