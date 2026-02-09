package com.therishideveloper.dreamhouse.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.outlined.ContactSupport
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ContactSupport
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Share
import com.therishideveloper.dreamhouse.data.model.NavItem

enum class Screens(val route: String) {
    HomeScreen("home"),
    LanguageScreen("language"),
    BackupScreen("backup"),
    TransactionListScreen("transaction_list/{type}"),
    IncomeExpenseScreen("income_expense_screen/{type}"),
    NoteScreen("note"),
    AllTransactionsScreen("all_transactions"),
    CategoryChartScreen("category_chart"),
    AddIncomeScreen("add_income_screen"),
    AddExpenseScreen("add_expense_screen"),
    ShareAppScreen("share_app"),
    AboutScreen("about_screen"),
    CategoryManualScreen("cat_manual_screen")
}

val listOfNavItems = listOf(
    // Main Section
    NavItem(
        "Home",
        "Main",
        Icons.Filled.Home,
        Icons.Outlined.Home,
        Screens.HomeScreen.route
    ),

    // Settings Section
    NavItem(
        "Language",
        "Settings",
        Icons.Filled.Language,
        Icons.Outlined.Language,
        Screens.LanguageScreen.route
    ),
    NavItem(
        "Backup",
        "Settings",
        Icons.Filled.Backup,
        Icons.Outlined.Backup,
        Screens.BackupScreen.route
    ),
    // Help Section
    NavItem(
        "Category Guide",
        "Help",
        Icons.Filled.Category,
        Icons.Outlined.Category,
        Screens.CategoryManualScreen.route
    ),
    // More Section
    NavItem(
        "Share App",
        "More",
        Icons.Filled.Share,
        Icons.Outlined.Share,
        Screens.ShareAppScreen.route
    ),
    NavItem(
        "About",
        "More",
        Icons.Filled.Info,
        Icons.Outlined.Info,
        Screens.AboutScreen.route
    )
)