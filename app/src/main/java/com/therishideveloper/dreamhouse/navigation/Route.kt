package com.therishideveloper.dreamhouse.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Share
import com.therishideveloper.dreamhouse.R
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
    CategoryManualScreen("cat_manual_screen"),
    ProjectSetupScreen("project_setup_screen?projectId={projectId}"),
    ConstructionStageScreen("construction_stage_screen"),
    AddStageScreen("add_stage_screen?stageId={stageId}"),
    ProjectOverviewScreen("project_overview_screen")
}

val listOfNavItems = listOf(
    // Main Section
    NavItem(
        1,
        R.string.menu_home,
        "Main",
        Icons.Filled.Home,
        Icons.Outlined.Home,
        Screens.HomeScreen.route
    ),
    NavItem(
        2,
        R.string.menu_construction_plan,
        "Main",
        Icons.Filled.Architecture,
        Icons.Outlined.Architecture,
        Screens.ProjectOverviewScreen.route
    ),
    // Settings Section
    NavItem(
        3,
        R.string.menu_language,
        "Settings",
        Icons.Filled.Language,
        Icons.Outlined.Language,
        Screens.LanguageScreen.route
    ),
    NavItem(
        4,
        R.string.menu_backup,
        "Settings",
        Icons.Filled.Backup,
        Icons.Outlined.Backup,
        Screens.BackupScreen.route
    ),
    // Help Section
    NavItem(
        5,
        R.string.cat_guide,
        "Help",
        Icons.Filled.Category,
        Icons.Outlined.Category,
        Screens.CategoryManualScreen.route
    ),
    // More Section
    NavItem(
        6,
        R.string.menu_share,
        "More",
        Icons.Filled.Share,
        Icons.Outlined.Share,
        Screens.ShareAppScreen.route
    ),
    NavItem(
        7,
        R.string.menu_about,
        "More",
        Icons.Filled.Info,
        Icons.Outlined.Info,
        Screens.AboutScreen.route
    )
)