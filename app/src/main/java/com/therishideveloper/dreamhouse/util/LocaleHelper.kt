package com.therishideveloper.dreamhouse.util

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import java.util.Locale

object LocaleHelper {
    private const val PREFS_NAME = "Settings"
    private const val KEY_LANG = "app_lang"

    fun applyLocale(context: Context, langCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit(commit = true) { putString(KEY_LANG, langCode) }

        val locale = Locale.forLanguageTag(langCode)
        Locale.setDefault(locale)

        updateResources(context, locale)
        context.applicationContext?.let { appCtx ->
            if (appCtx !== context) {
                updateResources(appCtx, locale)
            }
        }
    }

    fun wrapContext(context: Context): Context {
        val langCode = getSavedLocale(context)
        val locale = Locale.forLanguageTag(langCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return context.createConfigurationContext(config)
    }

    private fun updateResources(context: Context, locale: Locale) {
        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun getSavedLocale(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANG, "bn") ?: "bn"
    }
}
