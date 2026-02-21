package com.therishideveloper.dreamhouse.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.therishideveloper.dreamhouse.data.model.ConstructionStage
import com.therishideveloper.dreamhouse.data.model.DreamHouseStrings
import com.therishideveloper.dreamhouse.data.model.StageStatus
import com.therishideveloper.dreamhouse.ui.theme.tealColor

@Composable
fun StageSelector(
    selectedStage: ConstructionStage?,
    stageId: Int?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onStageSelected: (ConstructionStage) -> Unit
) {
    val context = LocalContext.current
    val strings = DreamHouseStrings.current
    val allStages = ConstructionStage.getAllStages()
    val selectedIndex = selectedStage?.let { allStages.indexOf(it) } ?: -1

    Text(strings.stage.selectWorkStage, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    Box {
        OutlinedTextField(
            value = selectedStage?.let { stringResource(it.titleRes) } ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (stageId == null) onExpandedChange(true) else showToast(
                        context,
                        strings.stage.errEditLock
                    )
                },
            label = { Text(strings.setup.stageName) },
            leadingIcon = if (selectedIndex != -1) {
                { StepNumberBadge(number = selectedStage!!.serial) }
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
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White)
        ) {
            allStages.forEachIndexed { index, stage ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StepNumberBadge(number = stage.serial)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(stringResource(stage.titleRes), fontWeight = FontWeight.Bold)
                                Text(
                                    stringResource(stage.descriptionRes),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    },

                    onClick = { onStageSelected(stage); onExpandedChange(false) },
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

@Composable
fun BudgetInput(value: String, onValueChange: (String) -> Unit) {
    val strings = DreamHouseStrings.current
    Text(strings.setup.estimatedBudget, fontWeight = FontWeight.Bold)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(strings.setup.budgetTaka) },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        leadingIcon = {
            Text(
                strings.common.currency,
                modifier = Modifier.padding(start = 12.dp),
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Composable
fun DateSection(
    startDate: Long,
    endDate: Long,
    minStartDate: Long?,
    onStartDateSelected: (Long) -> Unit,
    onEndDateSelected: (Long) -> Unit
) {
    val strings = DreamHouseStrings.current.setup

    Text(strings.selectTimeFrame, fontWeight = FontWeight.Bold)
    DateSelectionRow(
        label = strings.labelStartDate,
        date = startDate,
        minDate = minStartDate,
        onDateSelected = onStartDateSelected
    )
    Spacer(modifier = Modifier.height(12.dp))
    DateSelectionRow(
        label = strings.labelEndDate,
        date = endDate,
        minDate = startDate + 86400000L, // Minimum 1 day from start date
        onDateSelected = onEndDateSelected
    )
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
    val strings = DreamHouseStrings.current.stage
    var statusExpanded by remember { mutableStateOf(false) }
    val currentStatus = StageStatus.fromDbKey(currentStatusKey)
    val allStatus = StageStatus.getAllStatuses()

    Spacer(modifier = Modifier.height(16.dp))
    Text(text = strings.labelUpdateStatus, fontWeight = FontWeight.Bold)
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
        DropdownMenu(
            expanded = statusExpanded,
            onDismissRequest = { statusExpanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White)
        ) {
            allStatus.forEachIndexed { index, stageStatus ->
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
                    onClick = { onStatusSelected(stageStatus.dbKey); statusExpanded = false },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

                )
                if (index != allStatus.size - 1) {
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
