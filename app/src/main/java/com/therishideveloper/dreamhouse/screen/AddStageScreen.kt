package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.component.BudgetInput
import com.therishideveloper.dreamhouse.component.CalculatorFab
import com.therishideveloper.dreamhouse.component.DateSection
import com.therishideveloper.dreamhouse.component.DurationBadge
import com.therishideveloper.dreamhouse.component.StageSelector
import com.therishideveloper.dreamhouse.component.StatusUpdateSection
import com.therishideveloper.dreamhouse.component.showToast
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import com.therishideveloper.dreamhouse.data.model.ConstructionStage
import com.therishideveloper.dreamhouse.data.model.DreamHouseStrings
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
    val strings = DreamHouseStrings.current
    val scrollState = rememberScrollState()
    val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()

    // --- State Management ---
    val tenDaysInMillis = 10 * 24 * 60 * 60 * 1000L
    var selectedStage by remember { mutableStateOf<ConstructionStage?>(null) }
    var estimatedCost by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis() + tenDaysInMillis) }
    var status by remember { mutableStateOf(StageStatus.PENDING.dbKey) }
    var expanded by remember { mutableStateOf(false) }

    val existingStages by viewModel.getStages(activeProject?.id ?: 0)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val totalDays = remember(startDate, endDate) {
        ((endDate - startDate) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    }

    // --- Reusable Logic: Previous Stage End Date ---
    val previousStageEndDate = remember(selectedStage, existingStages) {
        val allStagesList = ConstructionStage.getAllStages()
        val currentIdx = selectedStage?.let { allStagesList.indexOf(it) } ?: -1
        if (currentIdx > 0) {
            val prevStageKey = allStagesList[currentIdx - 1].dbKey
            existingStages.find { it.stageName == prevStageKey }?.endDate
        } else null
    }

    // --- Side Effects ---
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

    LaunchedEffect(selectedStage) {
        if (stageId == null) endDate = startDate + tenDaysInMillis
    }

    LaunchedEffect(previousStageEndDate) {
        previousStageEndDate?.let {
            startDate = it
            endDate = it + tenDaysInMillis
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (stageId == null) strings.stage.addStageTitle else strings.stage.updateStageTitle,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val cost = estimatedCost.toDoubleOrNull() ?: 0.0
                        val currentProjectId = activeProject?.id
                        val allStages = ConstructionStage.getAllStages()
                        val isPreviousStageMissing = if (selectedStage!!.serial > 1 && stageId == null) {
                            val prevSerial = selectedStage!!.serial - 1
                            val previousStageKey = allStages.find { it.serial == prevSerial }?.dbKey
                            existingStages.none { it.stageName == previousStageKey }
                        } else false

                        // Validation Logic
                        when {
                            currentProjectId == null -> showToast(context, strings.stage.errNoProject)
                            selectedStage == null || cost <= 0 -> showToast(
                                context,
                                strings.stage.errFillAll
                            )

                            isPreviousStageMissing -> showToast(
                                context,
                                strings.setup.errPreviousStageMissing
                            )

                            totalDays <= 0 -> showToast(
                                context,
                                strings.stage.errInvalidDuration
                            )

                            stageId == null && existingStages.any { it.stageName == selectedStage!!.dbKey } ->
                                showToast(context, strings.stage.errDuplicate)

                            else -> {
                                val stageEntity = StageEntity(
                                    id = stageId ?: 0,
                                    serial = selectedStage!!.serial,
                                    projectId = currentProjectId,
                                    stageName = selectedStage!!.dbKey,
                                    estimatedCost = cost,
                                    startDate = startDate,
                                    endDate = endDate,
                                    status = status
                                )
                                viewModel.insertOrUpdateStage(stageEntity)
                                showToast(context, strings.common.success)
                                onBack()
                            }
                        }
                    }) { Icon(Icons.Default.Save, null, tint = Color.White) }
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

            StageSelector(
                selectedStage = selectedStage,
                stageId = stageId,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onStageSelected = { selectedStage = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BudgetInput(value = estimatedCost, onValueChange = { estimatedCost = it })

            Spacer(modifier = Modifier.height(24.dp))

            DateSection(
                startDate = startDate,
                endDate = endDate,
                minStartDate = previousStageEndDate,
                onStartDateSelected = {
                    startDate = it
                    if (startDate >= endDate) endDate = startDate + tenDaysInMillis
                },
                onEndDateSelected = { endDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            DurationBadge(totalDays = totalDays.toInt())

            if (stageId != null) {
                StatusUpdateSection(currentStatusKey = status, onStatusSelected = { status = it })
            }
        }
    }
}
