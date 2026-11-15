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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()
    val incomeList by viewModel.allIncomeTransactions.collectAsStateWithLifecycle()
    val expenseList by viewModel.allExpenseTransactions.collectAsStateWithLifecycle()

    // --- Calculation Logic ---
    val totalIncome = remember(incomeList) { incomeList.sumOf { it.amount } }
    val totalExpense = remember(expenseList) { expenseList.sumOf { it.amount } }
    val balance = totalIncome - totalExpense
    val incomeProgress = remember(totalIncome, totalExpense) {
        if (totalIncome + totalExpense > 0) (totalIncome / (totalIncome + totalExpense)).toFloat() else 0.5f
    }

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
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                DisplayTodayDate()
                // --- Summary Card ---
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val isCompact = maxWidth < 340.dp
                        val pieSize = if (isCompact) 78.dp else 96.dp
                        val cardPadding = if (isCompact) 10.dp else 14.dp
                        val gap = if (isCompact) 10.dp else 14.dp

                        Row(
                            modifier = Modifier.padding(cardPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(pieSize)) {
                                SolidPieChart(incomeProgress, tealColor, softRedColor)
                            }
                            Spacer(modifier = Modifier.width(gap))
                            Column(modifier = Modifier.weight(1f)) {
                                SummaryClickableRow(
                                    stringResource(R.string.income),
                                    totalIncome.toString(),
                                    MaterialTheme.colorScheme.primary
                                ) {
                                    navController.navigate("income_expense_screen/" + TransactionType.INCOME.dbKey)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                SummaryClickableRow(
                                    stringResource(R.string.expense),
                                    totalExpense.toString(),
                                    softRedColor
                                ) {
                                    navController.navigate("income_expense_screen/" + TransactionType.EXPENSE.dbKey)
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                )
                                TransactionSummaryRow(
                                    stringResource(R.string.balance),
                                    balance.toString(),
                                    if (balance >= 0) MaterialTheme.colorScheme.primary else softRedColor
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val itemWidth = ((maxWidth - 20.dp) / 2.65f).coerceIn(108.dp, 152.dp)
                        val gridHeight = if (maxWidth < 340.dp) 232.dp else 248.dp

                        LazyHorizontalGrid(
                            rows = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(gridHeight),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(DashboardUtils.getDashboardMenus()) { item ->
                                Box(
                                    modifier = Modifier
                                        .width(itemWidth)
                                        .fillMaxHeight()
                                ) {
                                    MenuGridItem(item = item) { route ->
                                        handleNavigation(navController, route)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
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
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
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
