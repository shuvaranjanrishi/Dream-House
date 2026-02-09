package com.therishideveloper.dreamhouse.util

import android.content.Context

object NumberUtils {

    fun formatByLocale(context: Context, input: String): String {
        val locale = context.resources.configuration.locales[0]
        val language = locale.language

        if (language != "bn" && language != "hi") return input

        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val hiDigits = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')

        val targetDigits = if (language == "bn") bnDigits else hiDigits

        return input.map { char ->
            if (char in '0'..'9') {
                targetDigits[char - '0']
            } else {
                char
            }
        }.joinToString("")
    }
}