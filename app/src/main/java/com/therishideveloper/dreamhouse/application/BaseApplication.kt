package com.therishideveloper.dreamhouse.application

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.therishideveloper.dreamhouse.util.LocaleHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleHelper.applyLocale(this, LocaleHelper.getSavedLocale(this))
    }
}