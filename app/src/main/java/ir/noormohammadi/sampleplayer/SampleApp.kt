package ir.noormohammadi.sampleplayer

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.permissionx.guolindev.BuildConfig
import ir.noormohammadi.sampleplayer.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.Locale

class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@SampleApp)
            modules(appModule)
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun attachBaseContext(base: Context?) {
        var context = base ?: this
        val config = context.resources.configuration
        val language = "fa"
        val sysLocale: Locale = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            LocalContextWrapper.getSystemLocale(config)
        } else {
            LocalContextWrapper.getSystemLocaleLegacy(config)
        }
        if (sysLocale.language != language) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            LocalContextWrapper.setSystemLocale(config, locale)
        }
        context = context.createConfigurationContext(config)
        super.attachBaseContext(context)
    }
}