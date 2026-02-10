package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.therishideveloper.dreamhouse.component.CalculatorFab
import com.therishideveloper.dreamhouse.component.DateSelectionRow
import com.therishideveloper.dreamhouse.component.showToast
import com.therishideveloper.dreamhouse.data.model.ConstructionStage
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStageScreen(
    onBack: () -> Unit,
    viewModel: ProjectViewModel = hiltViewModel()
) {
    var selectedStage by remember { mutableStateOf<ConstructionStage?>(null) }
    var stageName by remember { mutableStateOf("") }
    var estimatedCost by remember { mutableStateOf("") }

    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis() + 86400000 * 10) }

    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val diffInMs = endDate - startDate
    val totalDays = (diffInMs / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.label_add_stage), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val cost = estimatedCost.toDoubleOrNull() ?: 0.0
                        if (selectedStage != null && cost > 0) {
                            viewModel.addStage(
                                projectId = 1,
                                name = selectedStage!!.dbKey,
                                cost = cost,
                                start = startDate,
                                end = endDate
                            )
                            onBack()
                        } else {
                            showToast(
                                context,
                                context.getString(R.string.err_fill_all)
                            )
                        }
                    }) {
                        Icon(
                            Icons.Default.Save,
                            null,
                            tint = Color.White
                        )
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                stringResource(R.string.select_work_stage),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box {
                OutlinedTextField(
                    value = stageName,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    label = { Text(stringResource(R.string.stage_name)) },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, null)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.White)
                ) {
                    ConstructionStage.getAllStages().forEach { stage ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        stringResource(stage.titleRes),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        stringResource(stage.descriptionRes),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            },
                            onClick = {
                                selectedStage = stage
                                stageName = context.getString(stage.titleRes)
                                expanded = false
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        )
                        if (stage != ConstructionStage.entries.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.estimated_budget),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
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

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.select_timeframe), fontWeight = FontWeight.Bold)

            DateSelectionRow(
                label = stringResource(R.string.start_date),
                date = startDate,
                onDateSelected = {
                    startDate = it
                    if (startDate > endDate) endDate = startDate
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            DateSelectionRow(
                label = stringResource(R.string.end_date),
                date = endDate,
                minDate = startDate,
                onDateSelected = { endDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = tealColor.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, tealColor.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        null,
                        tint = tealColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.label_total_days_needed, totalDays.toInt()),
                        fontWeight = FontWeight.Bold,
                        color = tealColor,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
