package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.component.TransactionItemWithActions
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel
import androidx.compose.foundation.layout.PaddingValues
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.DownloadLoaderDialog
import com.therishideveloper.dreamhouse.component.TransactionDetailSheet
import com.therishideveloper.dreamhouse.component.TransactionSummaryCard
import com.therishideveloper.dreamhouse.component.showToast
import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.util.ExcelHelper.showDownloadNotification
import com.therishideveloper.dreamhouse.viewmodel.DownloadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionScreen(
    onBack: () -> Unit,
    navController: NavController,
    viewModel: TransactionViewModel,
    downloadViewModel: DownloadViewModel
) {
    val context = LocalContext.current
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    // --- States ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTransactionForDetail by remember { mutableStateOf<Transaction?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // --- Dynamic Categories Logic using Enum ---
    val currentCategories = remember(selectedTab) {
        when (selectedTab) {
            1 -> listOf("All") + Category.getIncomeCategories().map { it.dbKey }
            2 -> listOf("All") + Category.getExpenseCategories().map { it.dbKey }
            else -> listOf("All") + Category.entries.map { it.dbKey }
        }
    }

    // Filter logic updated with TransactionType Enum integration
    val filteredTransactions =
        remember(transactions, selectedTab, selectedCategory, searchQuery, selectedDate) {
            transactions.filter { item ->
                // Convert DB String to Enum
                val itemType = TransactionType.fromDbKey(item.transactionType)

                // Tab filtering using Enum comparison
                val matchesTab = when (selectedTab) {
                    1 -> itemType == TransactionType.INCOME
                    2 -> itemType == TransactionType.EXPENSE
                    else -> true
                }

                // Category matching (using category key)
                val matchesCategory = selectedCategory == "All" || item.category == selectedCategory

                // Search filtering
                val matchesSearch = item.description.contains(searchQuery, ignoreCase = true)

                // Date range filtering
                val matchesDate = if (selectedDate == null) {
                    true
                } else {
                    val range = DateUtils.getDayRange(selectedDate!!)
                    item.date >= range.first && item.date <= range.second
                }

                matchesTab && matchesCategory && matchesSearch && matchesDate
            }
        }

    val totalIncome = filteredTransactions
        .filter { TransactionType.fromDbKey(it.transactionType) == TransactionType.INCOME }
        .sumOf { it.amount }

    val totalExpense = filteredTransactions
        .filter { TransactionType.fromDbKey(it.transactionType) == TransactionType.EXPENSE }
        .sumOf { it.amount }

    val balance = totalIncome - totalExpense

    val incomeProgress = if (totalIncome + totalExpense > 0) {
        (totalIncome / (totalIncome + totalExpense)).toFloat()
    } else {
        0.5f
    }

    if (showDetailSheet && selectedTransactionForDetail != null) {
        TransactionDetailSheet(
            transaction = selectedTransactionForDetail!!,
            onDismiss = { showDetailSheet = false }
        )
    }
    val msgDownloadComplete = stringResource(R.string.download_complete)

    var showDownloadDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    if (showDownloadDialog) {
        ReusableConfirmDialog(
            title = stringResource(R.string.download_report_title),
            message = stringResource(R.string.download_report_desc),
            confirmBtnText = stringResource(R.string.btn_download),
            onConfirm = {
                showDownloadDialog = false
                downloadViewModel.downloadExcel(context, filteredTransactions) { file ->
                    showToast(context, msgDownloadComplete)
                    showDownloadNotification(context, file)
                }
            },
            onDismiss = { showDownloadDialog = false }
        )
    }

    if (showShareDialog) {
        ReusableConfirmDialog(
            title = stringResource(R.string.share_report_title),
            message = stringResource(R.string.share_report_desc),
            confirmBtnText = stringResource(R.string.btn_share),
            onConfirm = {
                showShareDialog = false
                downloadViewModel.shareExcel(filteredTransactions, context)
            },
            onDismiss = { showShareDialog = false }
        )
    }

    if (downloadViewModel.isDownloading) {
        DownloadLoaderDialog()
    }

    Scaffold(
        topBar = {
            AllTransactionTopBar(
                onBack = onBack,
                onDownload = { showDownloadDialog = true },
                onShare = { showShareDialog = true }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // 1. Search & Date Selector Row
            SearchAndDateRow(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedDate = selectedDate?.let { DateUtils.formatToDisplay(context, it) } ?: "",
                onDateClear = { selectedDate = null },
                onDatePickerClick = {
                    DateUtils.showDatePicker(context, selectedDate ?: System.currentTimeMillis()) {
                        selectedDate = it
                    }
                }
            )

            // 2. Tab Filter (All, Income, Expense)
            TransactionTabRow(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    selectedCategory = "All"
                }
            )

            // 3. Category Horizontal List using Enum
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentCategories) { categoryKey ->
                    // Get localized name for the UI chip
                    val labelText = if (categoryKey == "All") {
                        stringResource(R.string.filter_all)
                    } else {
                        stringResource(id = Category.fromDbKey(categoryKey).titleRes)
                    }

                    FilterChip(
                        selected = selectedCategory == categoryKey,
                        onClick = { selectedCategory = categoryKey },
                        label = { Text(labelText) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tealColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // 4. Summary Visualization Card
            TransactionSummaryCard(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                balance = balance,
                incomeProgress = incomeProgress
            )

            // 5. Transactions List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    top = 4.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredTransactions, key = { it.id }) { item ->
                    TransactionItemWithActions(
                        transaction = item,
                        onClick = {
                            selectedTransactionForDetail = item
                            showDetailSheet = true
                        },
                        onEditClick = {
                            val type = TransactionType.fromDbKey(item.transactionType)
                            val route = if (type == TransactionType.INCOME) {
                                "add_income_screen"
                            } else {
                                "add_expense_screen"
                            }

                            navController.navigate("$route?transactionId=${item.id}")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionTopBar(onBack: () -> Unit, onDownload: () -> Unit, onShare: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.title_all_transactions), color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = onDownload) {
                Icon(
                    Icons.Default.Download,
                    null,
                    tint = Color.White
                )
            }
            IconButton(onClick = onShare) { Icon(Icons.Default.Share, null, tint = Color.White) }
        }
    )
}

@Composable
fun SearchAndDateRow(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedDate: String,
    onDateClear: () -> Unit,
    onDatePickerClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = selectedDate.ifEmpty { searchQuery },
            onValueChange = { if (selectedDate.isEmpty()) onSearchChange(it) },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    if (selectedDate.isEmpty())
                        stringResource(R.string.placeholder_search_description)
                    else stringResource(
                        R.string.placeholder_filtered_by_date
                    )
                )
            },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            trailingIcon = {
                if (selectedDate.isNotEmpty()) {
                    IconButton(onClick = onDateClear) { Icon(Icons.Default.Close, null) }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = tealColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            onClick = onDatePickerClick,
            modifier = Modifier.size(54.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (selectedDate.isEmpty()) tealColor.copy(0.1f) else tealColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.DateRange,
                    null,
                    tint = if (selectedDate.isEmpty()) tealColor else Color.White
                )
            }
        }
    }
}

@Composable
fun TransactionTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = tealColor,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = tealColor
            )
        },
        divider = {}
    ) {
        // Localized Tab Names could be added here later via stringResource
        val tabs = listOf(R.string.filter_all, R.string.income, R.string.expense)

        tabs.forEachIndexed { index, title ->
            Tab(
                selectedContentColor = tealColor,
                unselectedContentColor = Color.Gray,
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        stringResource(title),
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
fun ReusableConfirmDialog(
    title: String,
    message: String,
    confirmBtnText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) { Text(confirmBtnText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) }
        }
    )
}