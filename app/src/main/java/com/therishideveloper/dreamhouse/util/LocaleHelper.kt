package com.therishideveloper.dreamhouse.util

import android.content.Context
import androidx.core.content.edit
import java.util.Locale

object LocaleHelper {
    fun applyLocale(context: Context, langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .edit { putString("app_lang", langCode) }
    }

    fun getSavedLocale(context: Context): String {
        return context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .getString("app_lang", "bn") ?: "bn"
    }
}
