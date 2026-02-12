package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.therishideveloper.dreamhouse.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.component.CalculatorFab
import com.therishideveloper.dreamhouse.component.DateSelectionRow
import com.therishideveloper.dreamhouse.component.StepNumberBadge
import com.therishideveloper.dreamhouse.component.showToast
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import com.therishideveloper.dreamhouse.data.model.ConstructionStage
import com.therishideveloper.dreamhouse.data.model.StageStatus
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStageScreen(
    onBack: () -> Unit,
    stageId: Int? = null,
    viewModel: ProjectViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- State Management ---
    var selectedStage by remember { mutableStateOf<ConstructionStage?>(null) }
    var estimatedCost by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis() + 86400000 * 10) }
    var status by remember { mutableStateOf(StageStatus.PENDING.dbKey) }

    var expanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    // Logic for total days calculation
    val totalDays = remember(startDate, endDate) {
        ((endDate - startDate) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    }

    // Observing existing stages for validation
    val existingStages by viewModel.getStages(1)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // --- Strings & Messages ---
    val successMsg = stringResource(R.string.msg_success) // "সফলভাবে সম্পন্ন হয়েছে!"
    val errorFillAll = stringResource(R.string.err_fill_all)
    val errorDuplicate =
        stringResource(R.string.err_duplicate_stage) // "এই ধাপটি ইতিমধ্যে যোগ করা হয়েছে!"
    val errorEditLock =
        stringResource(R.string.err_edit_lock) // "এডিট করার সময় ধাপ পরিবর্তন সম্ভব নয়।"

    // --- Load Data for Edit Mode ---
    LaunchedEffect(stageId) {
        if (stageId != null) {
            viewModel.getStageById(stageId).collect { stage ->
                stage?.let {
                    estimatedCost = it.estimatedCost.toString()
                    startDate = it.startDate
                    endDate = it.endDate
                    status = it.status
                    selectedStage =
                        ConstructionStage.getAllStages().find { s -> s.dbKey == it.stageName }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (stageId == null) stringResource(R.string.label_add_stage) else stringResource(
                            R.string.label_update_stage
                        ),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val cost = estimatedCost.toDoubleOrNull() ?: 0.0

                        // 1. Basic Validation
                        if (selectedStage == null || cost <= 0) {
                            showToast(context, errorFillAll)
                            return@IconButton
                        }

                        // 2. Duplicate Validation (Only for New Entry)
                        if (stageId == null && existingStages.any { it.stageName == selectedStage!!.dbKey }) {
                            showToast(context, errorDuplicate)
                            return@IconButton
                        }

                        // 3. Save/Update Execution
                        val stageEntity = StageEntity(
                            id = stageId ?: 0,
                            projectId = 1,
                            stageName = selectedStage!!.dbKey,
                            estimatedCost = cost,
                            startDate = startDate,
                            endDate = endDate,
                            status = status
                        )
                        viewModel.insertOrUpdateStage(stageEntity)
                        showToast(context, successMsg)
                        onBack()
                    }) {
                        Icon(Icons.Default.Save, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor)
            )
        },
        floatingActionButton = { CalculatorFab() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // --- Stage Selection Section ---
            Text(stringResource(R.string.select_work_stage), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Box {
                val allStages = ConstructionStage.getAllStages()
                val selectedIndex = selectedStage?.let { allStages.indexOf(it) } ?: -1
                val displayStageName = selectedStage?.let { stringResource(it.titleRes) } ?: ""

                OutlinedTextField(
                    value = displayStageName,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (stageId == null) expanded = true
                            else showToast(context, errorEditLock)
                        },
                    label = { Text(stringResource(R.string.stage_name)) },
                    // এখানে Leading Icon হিসেবে সিরিয়াল নম্বরটি বসানো হয়েছে
                    leadingIcon = if (selectedIndex != -1) {
                        { StepNumberBadge(number = selectedIndex + 1) }
                    } else null,
                    trailingIcon = { if (stageId == null) Icon(Icons.Default.ArrowDropDown, null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = if (stageId != null) Color.LightGray else MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = Color.Unspecified // ব্যাজের কালার ঠিক রাখার জন্য
                    )
                )

                if (stageId == null) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(Color.White)
                    ) {
                        allStages.forEachIndexed { index, stage ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // মেনুর ভেতরে রাউন্ড বক্স সিরিয়াল নম্বর
                                        StepNumberBadge(number = index + 1)

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                stringResource(stage.titleRes),
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                            Text(
                                                stringResource(stage.descriptionRes),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedStage = stage
                                    expanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            )

                            if (index != allStages.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = Color.LightGray.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            // --- Budget Section ---
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.estimated_budget), fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = estimatedCost,
                onValueChange = { estimatedCost = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.budget_taka)) },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Text(
                        stringResource(R.string.currency_symbol),
                        modifier = Modifier.padding(start = 12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            )

            // --- Date Section ---
            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(R.string.select_timeframe), fontWeight = FontWeight.Bold)
            DateSelectionRow(
                label = stringResource(R.string.start_date),
                date = startDate,
                onDateSelected = { startDate = it; if (startDate > endDate) endDate = startDate })
            Spacer(modifier = Modifier.height(12.dp))
            DateSelectionRow(
                label = stringResource(R.string.end_date),
                date = endDate,
                minDate = startDate,
                onDateSelected = { endDate = it })

            // --- Duration Badge ---
            Spacer(modifier = Modifier.height(16.dp))
            DurationBadge(totalDays = totalDays.toInt())

            // --- Status Section (Edit Mode Only) ---
            if (stageId != null) {
                StatusUpdateSection(
                    currentStatusKey = status,
                    onStatusSelected = { status = it }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DurationBadge(totalDays: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = tealColor.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, tealColor.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.CalendarToday,
                null,
                tint = tealColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.label_total_days_needed, totalDays),
                fontWeight = FontWeight.Bold,
                color = tealColor,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun StatusUpdateSection(currentStatusKey: String, onStatusSelected: (String) -> Unit) {
    var statusExpanded by remember { mutableStateOf(false) }
    val currentStatus = StageStatus.fromDbKey(currentStatusKey)

    Spacer(modifier = Modifier.height(16.dp))
    Text(text = stringResource(R.string.label_update_status), fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))

    Box {
        OutlinedButton(
            onClick = { statusExpanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, currentStatus.color.copy(alpha = 0.5f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(currentStatus.color, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(currentStatus.titleRes),
                    modifier = Modifier.weight(1f),
                    color = Color.Black
                )
                Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray)
            }
        }
        DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
            StageStatus.getAllStatuses().forEach { stageStatus ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(stageStatus.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = stringResource(stageStatus.titleRes))
                        }
                    },
                    onClick = { onStatusSelected(stageStatus.dbKey); statusExpanded = false }
                )
            }
        }
    }
}
