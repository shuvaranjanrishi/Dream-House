package com.therishideveloper.dreamhouse.util

import android.content.Context
import androidx.core.content.edit

object ThemeHelper {
    private const val PREFS_NAME = "Settings"
    private const val KEY_DARK_THEME = "dark_theme"

    fun isDarkTheme(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_THEME, false)
    }

    fun setDarkTheme(context: Context, isDark: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit(commit = true) {
                putBoolean(KEY_DARK_THEME, isDark)
            }
    }
}
