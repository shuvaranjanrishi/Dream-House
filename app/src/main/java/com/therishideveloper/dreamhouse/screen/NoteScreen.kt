package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import com.therishideveloper.dreamhouse.component.CalculatorFab
import com.therishideveloper.dreamhouse.component.CurrentBalance
import com.therishideveloper.dreamhouse.component.NoteDisclaimerCard
import com.therishideveloper.dreamhouse.component.NoteItem
import com.therishideveloper.dreamhouse.component.SolidPieChart
import com.therishideveloper.dreamhouse.component.TransactionSummaryRow
import com.therishideveloper.dreamhouse.data.entity.Note
import com.therishideveloper.dreamhouse.data.model.NoteType
import com.therishideveloper.dreamhouse.ui.theme.softRedColor
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

    val filteredList = remember(notes, selectedFilter) {
        when (selectedFilter) {
            NoteType.DEBT.dbKey -> notes.filter { it.type == NoteType.DEBT.dbKey }
            NoteType.RECEIVABLE.dbKey -> notes.filter { it.type == NoteType.RECEIVABLE.dbKey }
            else -> notes
        }
    }

    val totalDebt = remember(filteredList) {
        filteredList.filter { it.type == NoteType.DEBT.dbKey }.sumOf { it.amount }
    }
    val totalReceivable = remember(filteredList) {
        filteredList.filter { it.type == NoteType.RECEIVABLE.dbKey }.sumOf { it.amount }
    }
    val balanceAmount = totalReceivable - totalDebt
    val incomeProgress = remember(totalDebt, totalReceivable) {
        val totalSum = totalDebt + totalReceivable
        if (totalSum > 0) (totalReceivable / totalSum).toFloat() else 0.5f
    }

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
            CalculatorFab(
                lazyListState = lazyListState,
                isListEmpty = notes.isEmpty()
            )
        }

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
                    .widthIn(max = 640.dp)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                NoteDisclaimerCard()

                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val isCompact = maxWidth < 340.dp
                        val pieSize = if (isCompact) 76.dp else 90.dp
                        val cardPadding = if (isCompact) 12.dp else 16.dp
                        val gap = if (isCompact) 12.dp else 16.dp

                        Row(
                            modifier = Modifier.padding(cardPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(pieSize)) {
                                SolidPieChart(incomeProgress, tealColor, softRedColor)
                            }
                            Spacer(modifier = Modifier.width(gap))
                            Column(modifier = Modifier.weight(1f)) {
                                TransactionSummaryRow(
                                    stringResource(R.string.label_total_debt),
                                    "$totalDebt",
                                    softRedColor
                                )
                                TransactionSummaryRow(
                                    stringResource(R.string.label_total_receivable),
                                    "$totalReceivable",
                                    MaterialTheme.colorScheme.primary
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                )
                                TransactionSummaryRow(
                                    if (balanceAmount >= 0) stringResource(R.string.label_net_receivable) else stringResource(
                                        R.string.label_net_debt
                                    ),
                                    balanceAmount.toString(),
                                    if (balanceAmount >= 0) MaterialTheme.colorScheme.primary else softRedColor
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filterOptions = listOf("All") + NoteType.entries.map { it.dbKey }
                        filterOptions.forEach { filterKey ->
                            val label = if (filterKey == "All") stringResource(R.string.filter_all)
                            else stringResource(NoteType.fromDbKey(filterKey).titleRes)

                            FilterChip(
                                selected = selectedFilter == filterKey,
                                onClick = { selectedFilter = filterKey },
                                label = { Text(label, fontSize = 12.sp, maxLines = 1) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tealColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showSheet = true },
                        modifier = Modifier
                            .background(tealColor, CircleShape)
                            .size(40.dp)
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
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 84.dp
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
                        val parsedAmount = noteAmount.trim().toDoubleOrNull()
                        if (parsedAmount != null && parsedAmount > 0 && noteDesc.isNotBlank()) {
                            noteViewModel.saveNote(
                                Note(
                                    amount = parsedAmount,
                                    description = noteDesc.trim(),
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