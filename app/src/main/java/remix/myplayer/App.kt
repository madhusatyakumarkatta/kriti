package com.krithi

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.HiltAndroidApp
import com.krithi.helper.AppMigration
import com.krithi.helper.LanguageHelper.onConfigurationChanged
import com.krithi.helper.LanguageHelper.saveSystemCurrentLanguage
import com.krithi.helper.LanguageHelper.setApplicationLanguage
import com.krithi.helper.LanguageHelper.setLocal
import com.krithi.helper.ThirdPartyInitializer
import com.krithi.misc.manager.APlayerActivityManager
import com.krithi.ui.appshortcuts.DynamicShortcutManager
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Remix on 16-3-16.
 */
@HiltAndroidApp
class App : Application() {

  @Inject
  lateinit var appMigration: AppMigration

  override fun attachBaseContext(base: Context) {
    saveSystemCurrentLanguage()
    super.attachBaseContext(setLocal(base))
  }

  override fun onCreate() {
    super.onCreate()
    context = this

    appMigration.check()
    setUp()

    // AppShortcut
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
      DynamicShortcutManager(this).setUpShortcut()
    }

    // 加载第三方库
    ThirdPartyInitializer.init(this@App)

    registerActivityLifecycleCallbacks(APlayerActivityManager())
  }

  private fun setUp() {
    XXPermissions.setCheckMode(false)
    setApplicationLanguage(this)
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    onConfigurationChanged(applicationContext)
  }

  override fun onLowMemory() {
    super.onLowMemory()
    Timber.v("onLowMemory")
  }

  override fun onTrimMemory(level: Int) {
    super.onTrimMemory(level)
    Timber.v("onTrimMemory, %s", level)
  }

  companion object {

    @JvmStatic
    lateinit var context: App
      private set
  }
}