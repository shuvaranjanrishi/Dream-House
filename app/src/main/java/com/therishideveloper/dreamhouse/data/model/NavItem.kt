package com.therishideveloper.dreamhouse.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val id: Int,
    val titleRes: Int,
    val section: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)