package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.CurrentBalance
import com.therishideveloper.dreamhouse.component.CalculatorFab
import com.therishideveloper.dreamhouse.component.TransactionItem
import com.therishideveloper.dreamhouse.component.TransactionSummaryCard
import com.therishideveloper.dreamhouse.data.model.TransactionPeriod
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.util.NumberUtils
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onBack: () -> Unit,
    transactionPeriod: String,
    viewModel: TransactionViewModel
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()

    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()

    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedMonth by remember {
        mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1)
    }
    var selectedYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }

    var showMonthYearPicker by remember { mutableStateOf(false) }

    val transactions by remember(
        transactionPeriod,
        date,
        selectedMonth,
        selectedYear
    ) {
        when (transactionPeriod) {
            TransactionPeriod.MONTHLY.dbKey -> {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth - 1)
                }
                viewModel.getMonthlyData(cal.timeInMillis)
            }

            TransactionPeriod.YEARLY.dbKey -> {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                }
                viewModel.getYearlyData(cal.timeInMillis)
            }

            else -> viewModel.getTodayData(date)
        }
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val totalIncome = remember(transactions) {
        transactions.filter {
            TransactionType.fromDbKey(it.transactionType) == TransactionType.INCOME
        }.sumOf { it.amount }
    }

    val totalExpense = remember(transactions) {
        transactions.filter {
            TransactionType.fromDbKey(it.transactionType) == TransactionType.EXPENSE
        }.sumOf { it.amount }
    }

    val balance = totalIncome - totalExpense

    val incomeProgress = remember(totalIncome, totalExpense) {
        val total = totalIncome + totalExpense
        if (total > 0.0) (totalIncome / total).toFloat() else 0.5f
    }

    val allRecords = stringResource(R.string.label_all_records)

    val displayText = remember(transactionPeriod, date, selectedMonth, selectedYear) {
        when (transactionPeriod) {
            TransactionPeriod.MONTHLY.dbKey -> {
                DateUtils.getLocalizedMonthYear(context, selectedMonth, selectedYear)
            }

            TransactionPeriod.YEARLY.dbKey -> {
                NumberUtils.formatByLocale(context, selectedYear.toString())
            }

            TransactionPeriod.TODAY.dbKey -> {
                DateUtils.formatToDisplay(context, date)
            }

            else -> allRecords
        }
    }

    if (showMonthYearPicker) {
        MonthYearPickerDialog(
            type = transactionPeriod,
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            onDismiss = { showMonthYearPicker = false },
            onSelection = { month, year ->
                selectedMonth = month
                selectedYear = year
                showMonthYearPicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(DateUtils.getScreenTitle(transactionPeriod), color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = { CurrentBalance(currentBalance.toString()) }
            )
        },
        floatingActionButton = {
            CalculatorFab(
                lazyListState = lazyListState,
                isListEmpty = transactions.isEmpty()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            DateFilterHeader(
                text = displayText,
                onClick = {
                    if (transactionPeriod == TransactionPeriod.TODAY.dbKey) {
                        DateUtils.showDatePicker(context, date) {
                            date = it
                        }
                    } else showMonthYearPicker = true
                }
            )

            TransactionSummaryCard(totalIncome, totalExpense, balance, incomeProgress)

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(transactions, key = { it.id }) { item ->
                    TransactionItem(transaction = item)
                }
            }
        }
    }
}

@Composable
fun DateFilterHeader(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = tealColor)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ArrowDropDown, null, tint = tealColor)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthYearPickerDialog(
    type: String,
    selectedMonth: Int,
    selectedYear: Int,
    onDismiss: () -> Unit,
    onSelection: (month: Int, year: Int) -> Unit
) {
    val context = LocalContext.current
    var tempMonth by remember { mutableIntStateOf(selectedMonth) }
    var tempYear by remember { mutableIntStateOf(selectedYear) }
    val isMonthly = type == TransactionPeriod.MONTHLY.dbKey

    // বছরগুলোর লিস্ট
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val startYear = 2020
    val years = remember { (startYear..currentYear + 5).toList() }

    val listState = rememberLazyListState()

    LaunchedEffect(key1 = selectedYear) {
        val index = years.indexOf(selectedYear)
        if (index != -1) {
            listState.scrollToItem(index = index, scrollOffset = -200)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(if (isMonthly) R.string.title_select_month_year else R.string.title_select_year))
        },
        text = {
            Column {
                Text(stringResource(R.string.label_year), fontWeight = FontWeight.Bold)

                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp) // সাইড প্যাডিং যাতে দেখতে ভালো লাগে
                ) {
                    items(years) { year ->
                        FilterChip(
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tealColor,
                                selectedLabelColor = Color.White,
                            ),
                            selected = tempYear == year,
                            onClick = { tempYear = year },
                            label = { Text(NumberUtils.formatByLocale(context, year.toString())) }
                        )
                    }
                }

                if (isMonthly) {
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.label_month), fontWeight = FontWeight.Bold)
                    FlowRow(
                        maxItemsInEachRow = 4,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (0..11).forEach { index ->
                            FilterChip(
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tealColor,
                                    selectedLabelColor = Color.White,
                                ),
                                selected = tempMonth == index + 1,
                                onClick = { tempMonth = index + 1 },
                                label = {
                                    Text(DateUtils.getLocalizedMonthName(context, index))
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSelection(tempMonth, tempYear) }) {
                Text(
                    stringResource(R.string.action_apply),
                    color = tealColor,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}

