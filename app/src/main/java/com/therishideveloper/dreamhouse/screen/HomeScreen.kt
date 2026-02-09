package com.therishideveloper.dreamhouse.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.component.ActionButton
import com.therishideveloper.dreamhouse.component.CalculatorDialog
import com.therishideveloper.dreamhouse.component.CurrentBalance
import com.therishideveloper.dreamhouse.component.SolidPieChart
import com.therishideveloper.dreamhouse.component.SummaryClickableRow
import com.therishideveloper.dreamhouse.data.model.TransactionPeriod
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.navigation.Screens
import com.therishideveloper.dreamhouse.ui.theme.expenseRed
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DashboardUtils
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.util.NumberUtils
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    viewModel: TransactionViewModel
) {
    val context = LocalContext.current
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCalculator = true },
                containerColor = tealColor,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Calculate, null, modifier = Modifier.size(30.dp))
            }
        }

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
                        SolidPieChart(incomeProgress, tealColor, expenseRed)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        SummaryClickableRow(
                            stringResource(R.string.income),
                            NumberUtils.formatByLocale(context, totalIncome.toString()),
                            tealColor
                        ) {
                            navController.navigate("income_expense_screen/" + TransactionType.INCOME.dbKey)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        SummaryClickableRow(
                            stringResource(R.string.expense),
                            NumberUtils.formatByLocale(context, totalExpense.toString()),
                            expenseRed
                        ) {
                            navController.navigate("income_expense_screen/" + TransactionType.EXPENSE.dbKey)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        TransactionSummaryRow(
                            stringResource(R.string.balance),
                            NumberUtils.formatByLocale(context, balance.toString()),
                            tealColor
                        )
                    }
                }
            }

            // --- Menu Grid & Actions ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxWidth()) {
                    items(DashboardUtils.getDashboardMenus()) { item ->
                        MenuGridItem(item = item) { route ->
                            handleNavigation(navController, route)
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
                        expenseRed,
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
}

private fun handleNavigation(navController: NavController, route: String) {
    when (route) {
        // Handle period-based routes (today, monthly, yearly)
        TransactionPeriod.TODAY.dbKey,
        TransactionPeriod.MONTHLY.dbKey,
        TransactionPeriod.YEARLY.dbKey -> {
            navController.navigate("transaction_list/$route")
        }

        // Handle other static routes (notes, charts, etc.)
        else -> {
            try {
                navController.navigate(route)
            } catch (e: Exception) {
                // Prevent crash if route is missing
                Log.e("NavError", "Destination not found: $route")
            }
        }
    }
}

@Composable
private fun DisplayTodayDate() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(end = 24.dp, start = 24.dp, top = 8.dp, bottom = 0.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = tealColor.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, tealColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                tint = tealColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = DateUtils.getTodayDateForHomeScreen(context),
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 16.sp
            )
        }
    }
}
