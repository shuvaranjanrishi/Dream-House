package com.therishideveloper.dreamhouse.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import androidx.core.content.edit
import java.util.Locale

object LocaleHelper {
    private const val PREFS_NAME = "Settings"
    private const val KEY_LANG = "app_lang"

    fun applyLocale(context: Context, langCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit(commit = true) { putString(KEY_LANG, langCode) }

        applyLocaleInternal(context, langCode)
    }

    fun applySavedLocale(context: Context) {
        applyLocaleInternal(context, getSavedLocale(context))
    }

    private fun applyLocaleInternal(context: Context, langCode: String) {
        val locale = Locale.forLanguageTag(langCode)
        Locale.setDefault(locale)

        updateResources(context, locale)
        context.applicationContext?.let { appCtx ->
            if (appCtx !== context) {
                updateResources(appCtx, locale)
            }
        }
    }

    fun applyLocaleToConfiguration(config: Configuration, langCode: String) {
        val locale = Locale.forLanguageTag(langCode)
        Locale.setDefault(locale)
        config.setLocale(locale)
        config.setLocales(LocaleList(locale))
        config.setLayoutDirection(locale)
    }

    fun wrapContext(context: Context): Context {
        val langCode = getSavedLocale(context)
        val config = Configuration(context.resources.configuration)
        applyLocaleToConfiguration(config, langCode)
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return context.createConfigurationContext(config)
    }

    fun getLocalizedContext(
        context: Context,
        langCode: String = getSavedLocale(context)
    ): Context {
        val targetLang = Locale.forLanguageTag(langCode).language
        val currentLocale = if (!context.resources.configuration.locales.isEmpty) {
            context.resources.configuration.locales[0]
        } else {
            null
        }
        if (currentLocale?.language == targetLang) {
            return context
        }
        val config = Configuration(context.resources.configuration)
        applyLocaleToConfiguration(config, langCode)
        return context.createConfigurationContext(config)
    }

    fun createLocalizedContextWrapper(
        base: Context,
        langCode: String = getSavedLocale(base),
        baseConfig: Configuration = base.resources.configuration
    ): Context {
        val config = Configuration(baseConfig)
        applyLocaleToConfiguration(config, langCode)
        val localizedResources = base.createConfigurationContext(config).resources
        return LocalizedContextWrapper(base, localizedResources)
    }

    private fun updateResources(context: Context, locale: Locale) {
        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        config.setLocales(LocaleList(locale))
        config.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun getSavedLocale(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANG, "bn") ?: "bn"
    }

    private class LocalizedContextWrapper(
        base: Context,
        private val localizedResources: Resources
    ) : ContextWrapper(base) {
        override fun getResources(): Resources = localizedResources
        override fun getAssets(): AssetManager = localizedResources.assets
    }
}
