package com.therishideveloper.dreamhouse.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.util.NumberUtils
import com.therishideveloper.dreamhouse.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditForm(
    title: String,
    buttonText: String,
    transactionType: TransactionType,
    transactionId: Int? = null,
    initialCategory: String = "",
    initialDescription: String = "",
    initialAmount: String = "",
    initialDate: Long = System.currentTimeMillis(),
    onSave: (category: String, description: String, amount: String, date: Long) -> Unit,
    onDelete: (() -> Unit)? = null,
    onBack: () -> Unit,
    currentBalance: String
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var description by remember { mutableStateOf(initialDescription) }
    var amount by remember { mutableStateOf(initialAmount) }
    var date by remember { mutableLongStateOf(initialDate) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCalculator by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val categories = if (transactionType == TransactionType.INCOME) {
        Category.getIncomeCategories()
    } else {
        Category.getExpenseCategories()
    }

    // --- Validation Logic ---
    val msgCategory = stringResource(R.string.msg_select_category)
    val msgDescription = stringResource(R.string.msg_enter_description)
    val msgAmount = stringResource(R.string.msg_enter_amount)

    fun validateInputs(): Boolean {
        val amountValue = amount.toDoubleOrNull() ?: 0.0
        return when {
            selectedCategory.isEmpty() -> { showToast(context, msgCategory); false }
            description.trim().isEmpty() -> { showToast(context, msgDescription); false }
            amount.isEmpty() || amountValue <= 0.0 -> { showToast(context, msgAmount); false }
            else -> true
        }
    }

    // Calculator & Delete Dialog Logic (আগের মতোই থাকবে)
    if (showCalculator) {
        CalculatorDialog(
            initialExpression = "", initialResult = "0",
            onMinimize = { _, res -> amount = res; showCalculator = false },
            onClose = { showCalculator = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_msg)) },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete?.invoke() }) {
                    Text(stringResource(R.string.btn_delete), color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor),
                actions = { CurrentBalance(currentBalance) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCalculator = true },
                containerColor = tealColor,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Calculate, null)
            }
        }
    ) { padding ->
        // মেইন বক্স যা কিবোর্ড প্যাডিং হ্যান্ডেল করবে
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.ime) // কিবোর্ডের জন্য ডাইনামিক প্যাডিং
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // স্ক্রল স্টেট ব্যবহার
            ) {

                // ১. ক্যাটাগরি সিলেকশন হেডার
                Text(
                    stringResource(R.string.select_category),
                    fontWeight = FontWeight.SemiBold,
                    color = tealColor,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                if (transactionType == TransactionType.INCOME) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories) { categoryEnum ->
                            CategoryCard(
                                category = stringResource(id = categoryEnum.titleRes),
                                isSelected = selectedCategory == categoryEnum.dbKey,
                                icon = categoryEnum.icon,
                                onSelect = { selectedCategory = categoryEnum.dbKey }
                            )
                        }
                    }
                } else {
                    // Expense এর জন্য ৩টি লেয়ারে ভাগ করা (Chunking)
                    // ১৯টি ক্যাটাগরিকে ৩ ভাগে ভাগ করলে প্রতি লাইনে ৬-৭টি করে পড়বে
                    val chunkSize = if (categories.size > 12) (categories.size / 3) + 1 else 6
                    val layeredCategories = categories.chunked(chunkSize)

                    // এটি একটি হরিজন্টাল স্ক্রলযোগ্য কলাম যা ৩টি রো হোল্ড করবে
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            layeredCategories.forEach { rowItems ->
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    rowItems.forEach { categoryEnum ->
                                        CategoryCard(
                                            category = stringResource(id = categoryEnum.titleRes),
                                            isSelected = selectedCategory == categoryEnum.dbKey,
                                            icon = categoryEnum.icon,
                                            onSelect = { selectedCategory = categoryEnum.dbKey }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.description)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(stringResource(R.string.amount)) },
                        leadingIcon = {
                            Text(stringResource(R.string.currency_symbol), color = tealColor, fontWeight = FontWeight.Bold)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Constants.amountShortcuts) { shortcut ->
                            SuggestionChip(
                                onClick = { amount = shortcut },
                                label = {
                                    Text(stringResource(R.string.currency_symbol) + NumberUtils.formatByLocale(context, shortcut))
                                }
                            )
                        }
                    }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = DateUtils.formatToDisplay(context, date),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.date)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Surface(
                        onClick = {
                            DateUtils.showDatePicker(context, date) { date = it }
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = tealColor.copy(0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.DateRange, null, tint = tealColor)
                        }
                    }
                }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { if (validateInputs()) onSave(selectedCategory, description, amount, date) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = tealColor)
                    ) {
                        Text(text = buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    if (transactionId != null && onDelete != null) {
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            border = BorderStroke(1.dp, Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(text = stringResource(R.string.delete_title), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

