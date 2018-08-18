package io.scal.ambi.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import com.ambi.work.R
import com.ambi.work.databinding.ActivityLauncherBinding
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.login.LoginActivity
import io.scal.ambi.ui.global.base.activity.BaseActivity
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.home.root.HomeActivity
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

class LauncherActivity : BaseActivity<LauncherViewModel, ActivityLauncherBinding>() {

    override val layoutId: Int = R.layout.activity_launcher
    override val viewModelClass: KClass<LauncherViewModel> = LauncherViewModel::class
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds


    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    override val navigator: Navigator by lazy {

        object : BaseNavigator(this) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.LOGIN -> LoginActivity.createScreen(this@LauncherActivity)
                    NavigateTo.HOME  -> HomeActivity.createScreen(this@LauncherActivity)
                    else             -> super.createActivityIntent(screenKey, data)
                }
        }
    }
}