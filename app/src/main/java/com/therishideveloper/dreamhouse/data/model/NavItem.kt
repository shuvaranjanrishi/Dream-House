package com.therishideveloper.dreamhouse.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val title: String,
    val section: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)