package com.therishideveloper.dreamhouse.data.model

import androidx.compose.ui.graphics.Color
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.ui.theme.softRedColor
import com.therishideveloper.dreamhouse.ui.theme.tealColor

enum class TransactionType(
    val dbKey: String,    // Key for Database (to keep data consistent)
    val titleRes: Int,    // Resource ID for multi-language title
    val color: Color      // Global color associated with this type
) {
    INCOME("INCOME", R.string.label_income, tealColor),
    EXPENSE("EXPENSE", R.string.label_expense, softRedColor);

    companion object {
        fun fromDbKey(key: String?): TransactionType {
            return entries.find { it.dbKey == key } ?: EXPENSE
        }

        fun isIncome(key: String?): Boolean {
            return fromDbKey(key) == INCOME
        }
    }
}