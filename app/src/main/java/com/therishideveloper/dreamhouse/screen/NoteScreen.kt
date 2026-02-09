package com.therishideveloper.dreamhouse.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.AddNoteForm
import com.therishideveloper.dreamhouse.component.CalculatorDialog
import com.therishideveloper.dreamhouse.component.CurrentBalance
import com.therishideveloper.dreamhouse.component.NoteDisclaimerCard
import com.therishideveloper.dreamhouse.component.NoteItem
import com.therishideveloper.dreamhouse.component.SolidPieChart
import com.therishideveloper.dreamhouse.component.TransactionSummaryRow
import com.therishideveloper.dreamhouse.data.entity.Note
import com.therishideveloper.dreamhouse.data.model.NoteType
import com.therishideveloper.dreamhouse.ui.theme.expenseRed
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.util.NumberUtils
import com.therishideveloper.dreamhouse.viewmodel.NoteViewModel
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    onBack: () -> Unit,
    transactionViewModel: TransactionViewModel,
    noteViewModel: NoteViewModel
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()

    // --- Screen States ---
    var selectedFilter by remember { mutableStateOf("All") }
    var showSheet by remember { mutableStateOf(false) }
    var noteAmount by remember { mutableStateOf("") }
    var noteDesc by remember { mutableStateOf("") }
    var noteType by remember { mutableStateOf(NoteType.DEBT) }
    var noteDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showCalculator by remember { mutableStateOf(false) }
    var calcExpr by remember { mutableStateOf("") }
    var calcRes by remember { mutableStateOf("0") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val notes by noteViewModel.notes.collectAsState()
    val currentBalance = transactionViewModel.currentBalance.collectAsStateWithLifecycle()

    val isFabVisible by remember(notes) {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo

            if (visibleItems.isEmpty()) {
                true
            } else {
                val lastItem = visibleItems.lastOrNull()
                val totalItemsCount = layoutInfo.totalItemsCount

                if (lastItem != null && lastItem.index == totalItemsCount - 1) {
                    val viewportEnd = layoutInfo.viewportEndOffset
                    val fabTopBoundary = viewportEnd - 250 // বাটনের উপরের সীমানা
                    val fabBottomBoundary = viewportEnd - 50 // বাটনের নিচের সীমানা
                    val itemTop = lastItem.offset
                    val itemBottom = lastItem.offset + lastItem.size
                    val isOverlapping = itemBottom > fabTopBoundary && itemTop < fabBottomBoundary

                    !isOverlapping
                } else {
                    true
                }
            }
        }
    }

    val filteredList = when (selectedFilter) {
        NoteType.DEBT.dbKey -> notes.filter { it.type == NoteType.DEBT.dbKey }
        NoteType.RECEIVABLE.dbKey -> notes.filter { it.type == NoteType.RECEIVABLE.dbKey }
        else -> notes
    }

    val totalDebt = filteredList.filter { it.type == NoteType.DEBT.dbKey }.sumOf { it.amount }
    val totalReceivable =
        filteredList.filter { it.type == NoteType.RECEIVABLE.dbKey }.sumOf { it.amount }
    val balanceAmount = totalReceivable - totalDebt
    val totalSum = totalDebt + totalReceivable
    val incomeProgress = if (totalSum > 0) (totalReceivable / totalSum).toFloat() else 0.5f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.title_personal_notes),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor),
                actions = { CurrentBalance(currentBalance.value.toString()) }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = { showCalculator = true },
                    containerColor = tealColor,
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                ) {
                    Icon(Icons.Default.Calculate, null, modifier = Modifier.size(30.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            NoteDisclaimerCard()

            Card(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(90.dp)) {
                        SolidPieChart(incomeProgress, tealColor, expenseRed)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        TransactionSummaryRow(
                            stringResource(R.string.label_total_debt),
                            "$totalDebt",
                            expenseRed
                        )
                        TransactionSummaryRow(
                            stringResource(R.string.label_total_receivable),
                            "$totalReceivable",
                            tealColor
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        TransactionSummaryRow(
                            if (balanceAmount >= 0) stringResource(R.string.label_net_receivable) else stringResource(
                                R.string.label_net_debt
                            ),
                            NumberUtils.formatByLocale(
                                context,
                                kotlin.math.abs(balanceAmount).toString()
                            ),
                            if (balanceAmount >= 0) tealColor else expenseRed
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf("All") + NoteType.entries.map { it.dbKey }
                    filterOptions.forEach { filterKey ->
                        val label = if (filterKey == "All") stringResource(R.string.filter_all)
                        else stringResource(NoteType.fromDbKey(filterKey).titleRes)

                        FilterChip(
                            selected = selectedFilter == filterKey,
                            onClick = { selectedFilter = filterKey },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tealColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                IconButton(
                    onClick = { showSheet = true },
                    modifier = Modifier
                        .background(tealColor, CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    top = 4.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredList, key = { it.id }) { note ->
                    NoteItem(
                        note,
                        onDelete = { noteViewModel.deleteNote(note) },
                    )
                }
            }
        }
    }

    if (showCalculator) {
        CalculatorDialog(
            calcExpr, calcRes,
            onMinimize = { e, r -> calcExpr = e; calcRes = r; showCalculator = false },
            onClose = { calcExpr = ""; calcRes = "0"; showCalculator = false }
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Box(modifier = Modifier.imePadding()) {
                AddNoteForm(
                    noteAmount, { noteAmount = it },
                    noteDesc, { noteDesc = it },
                    noteType, { noteType = it },
                    noteDate, {
                        DateUtils.showDatePicker(context, noteDate) {
                            noteDate = it
                        }
                    }, { showCalculator = true },
                    onSave = {
                        if (noteAmount.isNotEmpty() && noteDesc.isNotEmpty()) {
                            noteViewModel.saveNote(
                                Note(
                                    amount = noteAmount.toDouble(),
                                    description = noteDesc,
                                    date = noteDate,
                                    type = noteType.dbKey
                                )
                            )
                            noteAmount = ""; noteDesc = ""; showSheet = false
                        }
                    },
                    tealColor
                )
            }
        }
    }
}