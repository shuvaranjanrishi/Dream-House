package com.therishideveloper.dreamhouse.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class DashboardItem(
    @param:StringRes val titleRes: Int,
    @param:DrawableRes val iconRes: Int,
    val route: String
)