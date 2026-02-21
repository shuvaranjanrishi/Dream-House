package com.therishideveloper.dreamhouse.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.MenuGridItem
import com.therishideveloper.dreamhouse.component.TransactionSummaryRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.component.ActionButton
import com.therishideveloper.dreamhouse.component.CalculatorDialog
import com.therishideveloper.dreamhouse.component.CalculatorFab
import com.therishideveloper.dreamhouse.component.CurrentBalance
import com.therishideveloper.dreamhouse.component.DisplayTodayDate
import com.therishideveloper.dreamhouse.component.MagicWelcomeOverlay
import com.therishideveloper.dreamhouse.component.SolidPieChart
import com.therishideveloper.dreamhouse.component.SummaryClickableRow
import com.therishideveloper.dreamhouse.data.model.TransactionPeriod
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.navigation.Screens
import com.therishideveloper.dreamhouse.ui.theme.softRedColor
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DashboardUtils
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    viewModel: TransactionViewModel,
    projectViewModel: ProjectViewModel = hiltViewModel()

) {
    // --- States ---
    var showCalculator by remember { mutableStateOf(false) }
    var calcExpression by remember { mutableStateOf("") }
    var calcResult by remember { mutableStateOf("0") }

    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()
    val incomeList by viewModel.allIncomeTransactions.collectAsStateWithLifecycle()
    val expenseList by viewModel.allExpenseTransactions.collectAsStateWithLifecycle()

    // --- Calculation Logic ---
    val totalIncome = incomeList.sumOf { it.amount }
    val totalExpense = expenseList.sumOf { it.amount }
    val balance = totalIncome - totalExpense
    val incomeProgress =
        if (totalIncome + totalExpense > 0) (totalIncome / (totalIncome + totalExpense)).toFloat() else 0.5f

    val showWelcome = projectViewModel.showWelcomeCelebration
    var startFadeIn by remember { mutableStateOf(false) }

    LaunchedEffect(showWelcome) {
        if (showWelcome) {
            delay(100)
            startFadeIn = true
            delay(5000)
            startFadeIn = false
            delay(1500)
            projectViewModel.welcomeShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title), color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor),
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            "Menu",
                            tint = Color.White
                        )
                    }
                },
                actions = { CurrentBalance(currentBalance.toString()) }
            )
        },
        floatingActionButton = { CalculatorFab() }

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            DisplayTodayDate()
            // --- Summary Card ---
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(100.dp)) {
                        SolidPieChart(incomeProgress, tealColor, softRedColor)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        SummaryClickableRow(
                            stringResource(R.string.income),
                            totalIncome.toString(),
                            tealColor
                        ) {
                            navController.navigate("income_expense_screen/" + TransactionType.INCOME.dbKey)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        SummaryClickableRow(
                            stringResource(R.string.expense),
                            totalExpense.toString(),
                            softRedColor
                        ) {
                            navController.navigate("income_expense_screen/" + TransactionType.EXPENSE.dbKey)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        TransactionSummaryRow(
                            stringResource(R.string.balance),
                            balance.toString(),
                            tealColor
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(DashboardUtils.getDashboardMenus()) { item ->
                        Box(modifier = Modifier.width(125.dp)) {
                            MenuGridItem(item = item) { route ->
                                handleNavigation(navController, route)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionButton(
                        stringResource(R.string.add_income),
                        tealColor,
                        Modifier.weight(1f)
                    ) {
                        navController.navigate(Screens.AddIncomeScreen.route)
                    }
                    ActionButton(
                        stringResource(R.string.add_expense),
                        softRedColor,
                        Modifier.weight(1f)
                    ) {
                        navController.navigate(Screens.AddExpenseScreen.route)
                    }
                }
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        if (showCalculator) {
            CalculatorDialog(
                initialExpression = calcExpression, initialResult = calcResult,
                onMinimize = { expr, res ->
                    calcExpression = expr; calcResult = res; showCalculator = false
                },
                onClose = { calcExpression = ""; calcResult = "0"; showCalculator = false }
            )
        }
    }
    AnimatedVisibility(
        visible = startFadeIn,
        enter = fadeIn(animationSpec = tween(1500)) +
                scaleIn(initialScale = 0f, animationSpec = tween(1500)),
        exit = fadeOut(animationSpec = tween(1500)) +
                scaleOut(targetScale = 0f, animationSpec = tween(1500)),

        ) {
        MagicWelcomeOverlay()
    }
}

private fun handleNavigation(navController: NavController, route: String) {
    when (route) {
        TransactionPeriod.TODAY.dbKey,
        TransactionPeriod.MONTHLY.dbKey,
        TransactionPeriod.YEARLY.dbKey -> {
            navController.navigate("transaction_list/$route")
        }

        else -> {
            try {
                navController.navigate(route)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NavError", "Destination not found: $route")
            }
        }
    }
}
