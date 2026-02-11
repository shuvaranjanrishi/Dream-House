package com.therishideveloper.dreamhouse.util

import android.content.Context
import com.therishideveloper.dreamhouse.R
import java.text.NumberFormat
import java.util.Locale

object NumberUtils {

    private fun convertDigits(input: String, language: String): String {
        val digits = when (language) {
            "bn" -> charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
            "hi" -> charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
            else -> return input
        }

        return input.map { char ->
            if (char in '0'..'9') digits[char - '0'] else char
        }.joinToString("")
    }

    fun formatByLocale(context: Context, input: String): String {
        val language = context.resources.configuration.locales[0].language
        return convertDigits(input, language)
    }

    fun formatAmountByLocale(context: Context, input: String): String {

        val cleanInput = input.replace(",", "")
        val amount = cleanInput.toDoubleOrNull() ?: return input
        val language = context.resources.configuration.locales[0].language

        val commaFormatter = NumberFormat.getInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = if (amount == 0.0) 1 else 0
        }

        val formattedNumber = commaFormatter.format(amount)
        val finalNumber = convertDigits(formattedNumber, language)
        val symbol = context.getString(R.string.currency_symbol)

        return "$symbol $finalNumber"
    }
}
