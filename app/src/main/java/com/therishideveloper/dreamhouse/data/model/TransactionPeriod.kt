package com.therishideveloper.dreamhouse.data.model

import com.therishideveloper.dreamhouse.R

enum class TransactionPeriod(
    val dbKey: String,    // Key for Database (to keep data consistent)
    val titleRes: Int    // Global color associated with this type
) {
    TODAY("TODAY", R.string.menu_today),
    MONTHLY("MONTHLY", R.string.menu_monthly),
    YEARLY("YEARLY", R.string.label_expense);

    companion object {
        fun fromDbKey(key: String?): TransactionPeriod {
            return entries.find { it.dbKey == key } ?: TODAY
        }
    }
}
