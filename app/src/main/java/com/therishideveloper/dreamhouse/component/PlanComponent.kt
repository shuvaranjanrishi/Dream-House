package com.therishideveloper.dreamhouse.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import com.therishideveloper.dreamhouse.data.model.ConstructionStage
import com.therishideveloper.dreamhouse.data.model.StageStatus
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.util.NumberUtils

@Composable
fun ProjectBudgetCard(
    projectName: String,
    address: String,
    totalBudget: Double,
    allocatedAmount: Double,
    totalDurationDays: Long,
    allocatedDays: Long,
    startDate: Long,
    endDate: Long
) {
    val context = LocalContext.current
    val softRed = Color(0xFFD32F2F)

    val budgetProgress = if (totalBudget > 0) (allocatedAmount / totalBudget).toFloat() else 0f
    val isBudgetOverflow = allocatedAmount > totalBudget

    val timeProgress =
        if (totalDurationDays > 0) (allocatedDays.toFloat() / totalDurationDays.toFloat()) else 0f
    val isTimeOverflow = allocatedDays > totalDurationDays

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                color = tealColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        text = projectName,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = tealColor,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = address,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            val warningMsg = when {
                isBudgetOverflow && isTimeOverflow -> stringResource(R.string.msg_warning_both)
                isBudgetOverflow -> stringResource(R.string.msg_warning_budget)
                else -> stringResource(R.string.msg_warning_time)
            }

            if (isBudgetOverflow || isTimeOverflow) {
                Text(
                    text = warningMsg,
                    color = softRed,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.label_master_budget),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = NumberUtils.formatAmountByLocale(context, totalBudget.toString()),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                VerticalDivider(thickness = 1.dp, color = Color.LightGray.copy(0.4f))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.total_duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = formatDuration(totalDurationDays),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "(${
                            DateUtils.formatToDisplay(
                                context,
                                startDate
                            )
                        } - ${DateUtils.formatToDisplay(context, endDate)})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProgressSection(
                leftText = stringResource(
                    R.string.label_allocated_cost,
                    NumberUtils.formatAmountByLocale(context, allocatedAmount.toString())
                ),
                rightText = NumberUtils.formatByLocale(
                    context,
                    (budgetProgress * 100).toInt().toString()
                ) + "%",
                progress = budgetProgress.coerceIn(0f, 1f),
                accentColor = if (isBudgetOverflow) softRed else tealColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProgressSection(
                leftText = stringResource(
                    R.string.label_allocated_time,
                    formatDuration(allocatedDays)
                ),
                rightText = NumberUtils.formatByLocale(
                    context,
                    (timeProgress * 100).toInt().toString()
                ) + "%",
                progress = timeProgress.coerceIn(0f, 1f),
                accentColor = if (isTimeOverflow) softRed else tealColor
            )
        }
    }
}

@Composable
fun ProgressSection(leftText: String, rightText: String, progress: Float, accentColor: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                leftText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                rightText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = accentColor,
            trackColor = accentColor.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun formatDuration(days: Long): String {

    val context = LocalContext.current

    val years = days / 365
    val months = (days % 365) / 30
    val remainingDays = days % 30

    return buildString {
        if (years > 0) append(
            NumberUtils.formatByLocale(
                context,
                years.toString()
            ) + " " + stringResource(R.string.year) + " "
        )
        if (months > 0) append(
            NumberUtils.formatByLocale(
                context,
                months.toString()
            ) + " " + stringResource(R.string.month) + " "
        )
        if (remainingDays > 0 || (years == 0L && months == 0L)) append(
            NumberUtils.formatByLocale(
                context,
                remainingDays.toString()
            ) + " " + stringResource(R.string.day)
        )
    }.trim()
}

@Composable
fun StageCard(
    index: Int,
    stage: StageEntity,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val durationDays = ((stage.endDate - stage.startDate) / (1000 * 60 * 60 * 24)).toInt()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(12.dp),
                color = tealColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = index.toString(), color = tealColor, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                val displayNameRes = ConstructionStage.fromDbKey(stage.stageName).titleRes
                Text(
                    text = stringResource(displayNameRes),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${
                        DateUtils.formatToDisplay(
                            context,
                            stage.startDate
                        )
                    } • " + NumberUtils.formatByLocale(
                        context,
                        durationDays.toString()
                    ) + stringResource(R.string.days),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(0.dp),
                    onClick = onClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Stage",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = NumberUtils.formatAmountByLocale(
                        context,
                        stage.estimatedCost.toString()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = tealColor
                )

                val status = StageStatus.fromDbKey(stage.status)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(status.color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(status.titleRes),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DateSelectionRow(
    label: String,
    date: Long,
    minDate: Long? = null,
    onDateSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = DateUtils.formatToDisplay(context, date),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )

        Surface(
            onClick = {
                DateUtils.showDatePicker(context, date, minDate) { onDateSelected(it) }
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
}

@Composable
fun StepNumberBadge(number: Int) {
    Surface(
        modifier = Modifier.size(28.dp),
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFF0F0F0),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}