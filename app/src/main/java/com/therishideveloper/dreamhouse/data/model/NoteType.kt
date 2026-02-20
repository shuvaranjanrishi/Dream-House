package com.therishideveloper.dreamhouse.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.ui.theme.softRedColor
import com.therishideveloper.dreamhouse.ui.theme.tealColor

enum class NoteType(
    val dbKey: String,
    val titleRes: Int,
    val color: Color,
    val icon: ImageVector
) {
    DEBT(
        dbKey = "DEBT",
        titleRes = R.string.filter_debt,
        color = softRedColor,
        icon = Icons.AutoMirrored.Filled.CallMade
    ),

    RECEIVABLE(
        dbKey = "RECEIVABLE",
        titleRes = R.string.filter_receivable,
        color = tealColor,
        icon = Icons.AutoMirrored.Filled.CallReceived // Arrow pointing in (Money coming in)
    );

    companion object {

        fun fromDbKey(key: String?): NoteType {
            return entries.find { it.dbKey == key } ?: DEBT
        }

        fun getFilterList() = entries.toList()
    }
}