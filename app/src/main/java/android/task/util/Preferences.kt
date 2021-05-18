package android.task.util

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.Delegates

object Preferences {

    internal var PREFS_NAME = "ANDROIDTASKAppPref"
    internal var HTML_DATA_PREF = "userID"
    internal var APIToken_PREF = "api_token"
    internal var UserRole_PREF = "UserRole"
    internal var USER_Email_PREF = "userEmail"
    internal var USER_NAME_PREF = "userName"
    internal var USER_COUNTRY_PREF = "userCountry"
    internal var USER_PHONE_PREF = "userPhone"
    internal var HOME_DATA_PREF = "HOMEDATA"
    internal var Registration_Token_PREF="user_token"
    internal var TOGGLE_NOTIFICATIONS="toggole_notifications"
    internal var appPrefence: SharedPreferences by Delegates.notNull<SharedPreferences>()

    // notifications preference
    internal var ApplicationLocale_PREF = "ApplicationLocale"
    internal var preferenceEditor: SharedPreferences.Editor by Delegates.notNull<SharedPreferences.Editor>()

    fun initializePreferences(context: Context) {
        appPrefence = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getApplicationLocale(): String {
        return appPrefence.getString(ApplicationLocale_PREF, "")!!
    }

    fun saveApplicationLocale(local: String) {
        preferenceEditor = appPrefence.edit()
        preferenceEditor.putString(ApplicationLocale_PREF, local)
        preferenceEditor.commit()
    }

    fun getToggleNotifications(): Boolean {
        return appPrefence.getBoolean(TOGGLE_NOTIFICATIONS, true)!!
    }

    fun saveToggleNotifications(local: Boolean) {
        preferenceEditor = appPrefence.edit()
        preferenceEditor.putBoolean(TOGGLE_NOTIFICATIONS, local)
        preferenceEditor.commit()
    }

    fun saveUserToken(Token: String) {
        preferenceEditor = appPrefence.edit()
        preferenceEditor.putString(Registration_Token_PREF, Token)
        preferenceEditor.commit()
    }

    fun getUserToken(): String {
        return appPrefence.getString(Registration_Token_PREF, "")!!
    }



    fun saveUserEmail(userEmail: String) {
        preferenceEditor = appPrefence.edit()
        preferenceEditor.putString(USER_Email_PREF, userEmail)
        preferenceEditor.commit()
    }

    fun getUserEmail(): String {
        return appPrefence.getString(USER_Email_PREF, "")!!
    }




    fun getUserName(): String {
        return appPrefence.getString(USER_NAME_PREF, "")!!
    }

    fun saveUserName(userName: String) {
        preferenceEditor = appPrefence.edit()
        preferenceEditor.putString(USER_NAME_PREF, userName)
        preferenceEditor.commit()
    }

    fun getUserCountry(): String {
        return appPrefence.getString(USER_COUNTRY_PREF, "")!!
    }

    fun saveUserCountry(country: String) {
        preferenceEditor = appPrefence.edit()
        preferenceEditor.putString(USER_COUNTRY_PREF, country)
        preferenceEditor.commit()
    }

    fun getUserPhone(): String {
        return appPrefence.getString(USER_PHONE_PREF, "")!!
    }

    fun saveUserPhone(userPhone: String) {
        preferenceEditor = appPrefence.edit()
        preferenceEditor.putString(USER_PHONE_PREF, userPhone)
        preferenceEditor.commit()
    }

    fun getHtmlData(): String {
        return appPrefence.getString(HTML_DATA_PREF, "")!!
    }

    fun saveHtmlData(html: String) {
        preferenceEditor = appPrefence.edit()
        preferenceEditor.putString(HTML_DATA_PREF, html)
        preferenceEditor.commit()
    }



    fun clearUserData() {
        preferenceEditor = appPrefence.edit()
        preferenceEditor.clear().commit()
    }
}