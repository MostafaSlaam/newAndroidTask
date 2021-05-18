package android.task.view.activity.baseActivity

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.task.MyApplication
import android.task.R
import android.task.databinding.ActivityBaseBinding
import android.task.util.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.shababi.view.activity.baseActivity.BaseViewModelFactory
import com.shababit.observer.OnAskUserAction
import java.util.*
import kotlin.properties.Delegates

abstract class BaseActivity : AppCompatActivity {
    var drawHeader: Boolean by Delegates.notNull<Boolean>()
    internal var showBack: Boolean by Delegates.notNull<Boolean>()
    internal var showMenu: Boolean by Delegates.notNull<Boolean>()
    internal var showAny: Boolean by Delegates.notNull<Boolean>()
    internal var appBarWhite: Boolean by Delegates.notNull<Boolean>()

    private var activityTitleId: Int = 0


    constructor(
        activityTitleId: Int,
        drawHeader: Boolean,
        showBack: Boolean,
        showMenu: Boolean,
        showAny: Boolean,
        appBarWhite: Boolean
    ) : super() {
        this.drawHeader = drawHeader
        this.showBack = showBack
        this.showMenu = showMenu
        this.showAny = showAny
        this.appBarWhite = appBarWhite
        this.activityTitleId = activityTitleId
    }

    protected abstract fun doOnCreate(arg0: Bundle?)
    abstract fun initializeViews()
    abstract fun setListener()

    fun setTitleGravity(gravity: Int) {
        baseBinding.tvTitleCustomActionBar.gravity = gravity
    }

    fun updateLocale() {
        //update activities locale
        if (Preferences.getApplicationLocale().compareTo("ar") == 0) {
            Preferences.saveApplicationLocale("ar")
            //RTL
            forceRTLIfSupported()
        } else if(Preferences.getApplicationLocale().compareTo("fr") == 0) {
            Preferences.saveApplicationLocale("fr")
            //LTR
            forceLTRIfSupported()
        } else {
            Preferences.saveApplicationLocale("en")
            //LTR
            forceLTRIfSupported()
        }
        //Update the locale here before loading the layout to get localized strings.
        LocaleHelper.updateLocale(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateAndroidSecurityProvider()
        baseBinding = DataBindingUtil.setContentView(this, R.layout.activity_base)
        //set default color for appbar
        setTranslucentAppBar()
        application = getApplication() as MyApplication
        application.context = this

        baseBinding.viewModel =
            ViewModelProvider(this, BaseViewModelFactory(application))
                .get(BaseActivityViewModel::class.java)
        baseBinding.viewModel!!.baseViewModelObserver = baseViewModelObserver
        baseBinding.lifecycleOwner = this
//        baseBinding.viewModel!!.dataBaseSource=dataBaseSource
        liveDataObservers()
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        updateLocale()

        //set actionbar
        setDrawHeader(
            drawHeader,
            getString(activityTitleId),
            showBack,
            showMenu,
            showAny,
            appBarWhite
        )
        doOnCreate(savedInstanceState)
        setListener()
    }

    var baseViewModelObserver = object : BaseActivityViewModel.BaseViewModelObserver {

        override fun onBackButtonClicked() {
            onBackPressed()
        }

        override fun onMenuButtonClicked() {
            if (baseBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                closeMenu()
            } else {
                openMenu()
            }
        }

        override fun onAnyButtonClicked() {
        }

        override fun onLoginAgain() {
            showMessage(
                this@BaseActivity,
                "error",
                "error",
                object : OnAskUserAction {
                    override fun onPositiveAction() {
//                        baseBinding.viewModel!!.clearAppPreferencesAndDB()
                    }

                    override fun onNegativeAction() {
                    }

                },
                false,
                getString(R.string.cancel),
                getString(R.string.ok),
                false
            )
        }

        override fun onRestartApp(message: String) {
            restartApp(message)
        }
    }

    fun restartApp(message: String) {
        if (message.isNotEmpty())
            Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            )
                .show()
        //restart app
//        var intent = Intent(this, SplashActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        overridePendingTransition(
//            R.anim.slide_from_right_to_left,
//            R.anim.slide_in_left
//        )
        finishAffinity()
    }

    private fun liveDataObservers() {
    }

    override fun onResume() {
        super.onResume()
    }

    var application: MyApplication by Delegates.notNull()
    var imm: InputMethodManager by Delegates.notNull()

    lateinit var baseBinding: ActivityBaseBinding

    fun putContentView(activityLayout: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            layoutInflater,
            activityLayout,
            baseBinding.baseFragment,
            true
        )
    }

    private fun updateAndroidSecurityProvider() {
        try {
            //enable provider in some devices that disabled by default to avoid ssl error in connection
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
//            val apiAvailability = GoogleApiAvailability.getInstance()
//            val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
//            if (resultCode != ConnectionResult.SUCCESS) apiAvailability.getErrorDialog(
//                this,
//                resultCode,
//                2404
//            ).show()
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e("SecurityException", "Google Play Services not available.")
//            Toast.makeText(this, "Google Play Services not available.", Toast.LENGTH_SHORT).show()
//            val apiAvailability = GoogleApiAvailability.getInstance()
//            val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
//            if (resultCode != ConnectionResult.SUCCESS) apiAvailability.getErrorDialog(
//                this,
//                resultCode,
//                2404
//            ).show()
        }
    }

    fun setMenuIconVisibility(isVisible: Boolean) {
        baseBinding.ivMenuCustomActionBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE

        initializeSlideMenu()
        //enable or disable slide menu
        setDrawerState(isVisible)
    }

    fun setAnyIconVisibility(isVisible: Boolean) {
        baseBinding.ivAnyIconCustomActionBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    fun setActionBarVisibilty(isVisible: Boolean) {
        baseBinding.layoutContainerActionBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    fun setHeaderTitle(title: String) {
        baseBinding.tvTitleCustomActionBar.visibility = View.VISIBLE
        baseBinding.tvTitleCustomActionBar.text = title
    }

    internal fun setTranslucentAppBar() {
        val fullScreen = window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (fullScreen) {
                baseBinding.layoutContainerActionBar.visibility = View.GONE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
                }
            } else {
                setAppBarGradient()
            }
        } else {
            if (!fullScreen) {
                setAppBarGradient()
            }
        }
    }

    fun setDrawHeader(
        isShowHeader: Boolean,
        title: String,
        isShowBackIcon: Boolean,
        showMenu: Boolean,
        showAny: Boolean,
        appBarWhite: Boolean
    ) {
        setActionBarVisibilty(isShowHeader)
        setBackIconVisibility(isShowBackIcon, appBarWhite)
        setMenuIconVisibility(showMenu)
        setAnyIconVisibility(showAny)
        setHeaderTitle(title)

        if (appBarWhite) {
            setAppBarlightAndStatusBarDark(R.color.white)
            baseBinding.tvTitleCustomActionBar.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color._00c0ed
                )
            )
        } else {
            setAppBarGradient()
            baseBinding.layoutContainerActionBar.setBackgroundResource(R.color.colorPrimaryDark)
            baseBinding.tvTitleCustomActionBar.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
        }
    }

    fun setBackIconVisibility(isShowBackIcon: Boolean, appBarWhite: Boolean) {
        baseBinding.ivBackIconCustomActionBar.visibility =
            if (isShowBackIcon) View.VISIBLE else View.GONE
        //set icons
//        if (appBarWhite) {
//            baseBinding.ivBackIconCustomActionBar.setImageResource(R.drawable.back_white_home_icon)
//        } else {
        baseBinding.ivBackIconCustomActionBar.setImageResource(R.drawable.back_white_home_icon)
//        }
    }

    override fun onBackPressed() {
        if (showBack) super.onBackPressed()
        else if (showMenu) {
            if (baseBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                closeMenu()
            } else {
                super.onBackPressed()
            }
        } else
            super.onBackPressed()
    }

    internal fun initializeSlideMenu() {
        // slide menu def
//        if (supportFragmentManager.findFragmentByTag("menu") == null) supportFragmentManager.beginTransaction()
//            .replace(
//                R.id.menu,
//                MenuFragment(),
//                "menu"
//            ).commit()

        baseBinding.drawerLayout.addDrawerListener(object :
            androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerStateChanged(newState: Int) {
                if (newState == androidx.drawerlayout.widget.DrawerLayout.STATE_IDLE) {
                    if (baseBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    } else {
                    }
                }
            }
        })
    }

    fun setDrawerState(isEnabled: Boolean) {
        if (isEnabled) {
            baseBinding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            baseBinding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    fun closeMenu() {
        baseBinding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun openMenu() {
        baseBinding.drawerLayout.openDrawer(GravityCompat.START)
    }

     fun setAppBarlightAndStatusBarDark(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //make statusbar dark text and icons starting from KITKAT
            baseBinding.layoutContainerBaseActivity.systemUiVisibility =
                baseBinding.layoutContainerBaseActivity.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, color)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        }
        baseBinding.layoutContainerActionBar.setBackgroundResource(color)
        baseBinding.tvTitleCustomActionBar.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.taupe
            )
        )
        window.setBackgroundDrawableResource(R.color.black)
    }

    fun setAppBarGradient() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //make statusbar dark text and icons starting from KITKAT
            baseBinding.layoutContainerBaseActivity.systemUiVisibility =
                baseBinding.layoutContainerBaseActivity.systemUiVisibility and
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
            window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        }
        //set gradient color
        baseBinding.layoutContainerActionBar.setBackgroundResource(R.color.colorPrimaryDark)
        baseBinding.tvTitleCustomActionBar.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorPrimaryDark))
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun forceLTRIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(
            LocaleHelper.applyOverrideConfiguration(
                baseContext,
                overrideConfiguration
            )
        )
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleHelper.updateLocale(newBase)
        )
    }

    internal val callPermissionRequest = 4

    var number = ""
    fun CallMobile(Number: String?) {
        try {
            number = Number!!
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), callPermissionRequest)
                return
            }
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$Number")
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callIntent.setPackage("com.android.server.telecom")
            } else {
                callIntent.setPackage("com.android.phone")
            }
            startActivity(callIntent)
        } catch (e: Exception) {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$Number")
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(callIntent)
        }
    }

    internal fun requestReceiveSMSPermission() {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.RECEIVE_SMS),
                RequestCodeSMSPERMISSIONActivity
            )
            return
        }
    }

    fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (context != null && permissions != null) {
            for (p in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        p
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            callPermissionRequest ->
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    CallMobile(number)
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            else -> {
            }
        }
    }

    open fun finish_activity() {
        finish()
//        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_from_left_to_right)
    }

    fun hideKeyPad(view: View) {
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showHideProgressDialog(isShow: Boolean) {
        if (isShow)
            ProgressDialogLoading.show(this@BaseActivity)
        else
            ProgressDialogLoading.dismiss(this@BaseActivity)
    }

    fun showHideMessageDialog(isShow: Boolean, title: String, message: String) {
        if (isShow)
            showMessage(
                this@BaseActivity, title,
                message,
                object : OnAskUserAction {
                    override fun onPositiveAction() {
                    }

                    override fun onNegativeAction() {
                    }

                },
                false,
                getString(R.string.cancel),
                getString(R.string.ok),
                true
            )
        else
            ProgressDialogLoading.dismiss(this@BaseActivity)
    }
}