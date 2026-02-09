package com.therishideveloper.dreamhouse.navigation

import com.therishideveloper.dreamhouse.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.therishideveloper.dreamhouse.data.model.TransactionPeriod
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.screen.AboutScreen
import com.therishideveloper.dreamhouse.screen.AddEditExpenseScreen
import com.therishideveloper.dreamhouse.screen.AddEditIncomeScreen
import com.therishideveloper.dreamhouse.screen.AllTransactionScreen
import com.therishideveloper.dreamhouse.screen.LanguageScreen
import com.therishideveloper.dreamhouse.screen.BackupScreen
import com.therishideveloper.dreamhouse.screen.CategoryChartScreen
import com.therishideveloper.dreamhouse.screen.HomeScreen
import com.therishideveloper.dreamhouse.screen.TransactionListScreen
import com.therishideveloper.dreamhouse.screen.NoteScreen
import com.therishideveloper.dreamhouse.screen.IncomeExpenseScreen
import com.therishideveloper.dreamhouse.screen.manual.CategoryManualScreen
import com.therishideveloper.dreamhouse.viewmodel.DownloadViewModel
import com.therishideveloper.dreamhouse.viewmodel.NoteViewModel
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel

@Composable
fun AppNavigation(navController: NavHostController, onOpenDrawer: () -> Unit) {

    val transactionViewModel: TransactionViewModel = hiltViewModel()
    val noteViewModel: NoteViewModel = hiltViewModel()
    val downloadViewModel: DownloadViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Screens.HomeScreen.route) {
        composable(Screens.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                onMenuClick = onOpenDrawer,
                viewModel = transactionViewModel
            )
        }
        composable(Screens.LanguageScreen.route) {
            LanguageScreen()
        }
        composable(Screens.BackupScreen.route) {
            BackupScreen(
                onMenuClick = onOpenDrawer,
                viewModel = transactionViewModel
            )
        }

        composable(
            route = Screens.IncomeExpenseScreen.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: TransactionType.INCOME.dbKey

            val incomeList by transactionViewModel.allIncomeTransactions.collectAsStateWithLifecycle()
            val expenseList by transactionViewModel.allExpenseTransactions.collectAsStateWithLifecycle()
            val displayTitle =
                if (type == TransactionType.INCOME.dbKey) stringResource(R.string.label_income_records) else stringResource(
                    R.string.label_expense_records
                )
            val displayTransactions =
                if (type == TransactionType.INCOME.dbKey) incomeList else expenseList

            if (type == TransactionType.INCOME.dbKey) {
                IncomeExpenseScreen(
                    onBack = { navController.popBackStack() },
                    title = displayTitle,
                    transactions = displayTransactions,
                    viewModel = transactionViewModel
                )
            } else {
                IncomeExpenseScreen(
                    onBack = { navController.popBackStack() },
                    title = displayTitle,
                    transactions = displayTransactions,
                    viewModel = transactionViewModel
                )
            }
        }
        composable(
            route = Screens.TransactionListScreen.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: TransactionPeriod.TODAY.dbKey

            TransactionListScreen(
                onBack = { navController.popBackStack() },
                transactionPeriod = type,
                viewModel = transactionViewModel
            )
        }
        composable(Screens.NoteScreen.route) {
            NoteScreen(
                onBack = { navController.popBackStack() },
                transactionViewModel = transactionViewModel,
                noteViewModel = noteViewModel
            )
        }
        composable(Screens.AllTransactionsScreen.route) {
            AllTransactionScreen(
                onBack = { navController.popBackStack() },
                navController = navController,
                viewModel = transactionViewModel,
                downloadViewModel = downloadViewModel
            )
        }
        composable(
            route = "add_income_screen?transactionId={transactionId}",
            arguments = listOf(navArgument("transactionId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("transactionId") ?: -1
            AddEditIncomeScreen(
                transactionId = if (id == -1) null else id,
                onBack = { navController.popBackStack() },
                viewModel = transactionViewModel
            )
        }
        composable(
            route = "add_expense_screen?transactionId={transactionId}",
            arguments = listOf(navArgument("transactionId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("transactionId") ?: -1
            AddEditExpenseScreen(
                transactionId = if (id == -1) null else id,
                onBack = { navController.popBackStack() },
                viewModel = transactionViewModel
            )
        }
        composable(Screens.CategoryChartScreen.route) {
            CategoryChartScreen(
                onBack = { navController.popBackStack() },
                viewModel = transactionViewModel
            )
        }
        composable(Screens.AboutScreen.route) {
            AboutScreen(
                onMenuClick = onOpenDrawer,
            )
        }
        composable(Screens.CategoryManualScreen.route) {
            CategoryManualScreen(
                onMenuClick = onOpenDrawer,
                viewModel = transactionViewModel
            )
        }
    }
}