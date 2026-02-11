package com.therishideveloper.dreamhouse.data.model

import androidx.compose.ui.graphics.Color
import com.therishideveloper.dreamhouse.R

enum class StageStatus(
    val dbKey: String,
    val titleRes: Int,
    val color: Color
) {
    PENDING("PENDING", R.string.status_pending, Color.Gray),
    ONGOING("ONGOING", R.string.status_ongoing, Color(0xFFFFA500)),
    COMPLETED("COMPLETED", R.string.status_completed, Color(0xFF008080));

    companion object {
        fun fromDbKey(key: String?): StageStatus =
            entries.find { it.dbKey == key } ?: PENDING

        fun getAllStatuses() = entries.toList()
    }
}