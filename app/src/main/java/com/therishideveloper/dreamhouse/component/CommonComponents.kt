package com.therishideveloper.dreamhouse.component

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.ui.theme.softRedColor
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.NumberUtils

@Composable
fun CurrentBalance(currentBalance: String) {
    val context = LocalContext.current
    val displayBalance = try {
        NumberUtils.formatAmountByLocale(context, currentBalance)
    } catch (e: Exception) {
        NumberUtils.formatByLocale(context, "0.00")
    }

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .padding(end = 12.dp)
            .widthIn(max = 175.dp)
    ) {
        Text(
            text = stringResource(R.string.label_current_balance),
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = displayBalance,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SolidPieChart(
    incomeProgress: Float,
    incomeColor: Color,
    expenseColor: Color,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current
    val safeProgress = incomeProgress.coerceIn(0f, 1f)
    val incomePercent = (safeProgress * 100).toInt()
    val expensePercent = 100 - incomePercent

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = size.minDimension / 2f

        // Draw Expense Slice (bottom/background arc)
        drawArc(
            color = expenseColor.copy(alpha = 0.8f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = true
        )

        // Draw Income Slice (top arc)
        drawArc(
            color = incomeColor.copy(alpha = 0.85f),
            startAngle = -90f,
            sweepAngle = 360f * safeProgress,
            useCenter = true
        )

        // Draw Percentage Text centered in each slice
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                textAlign = Paint.Align.CENTER
                textSize = (radius * 0.34f).coerceIn(22f, 36f)
                isFakeBoldText = true
                typeface = Typeface.DEFAULT_BOLD
                color = Color.White.toArgb()
            }

            if (incomePercent > 5) {
                val incomeMidAngle = (-90f + (360f * safeProgress) / 2f) * (Math.PI / 180.0)
                val incomeX = (canvasWidth / 2f + (radius * 0.58f) * Math.cos(incomeMidAngle)).toFloat()
                val incomeY = (canvasHeight / 2f + (radius * 0.58f) * Math.sin(incomeMidAngle)).toFloat()
                drawText(
                    NumberUtils.formatByLocale(context, "$incomePercent%"),
                    incomeX,
                    incomeY + paint.textSize * 0.35f,
                    paint
                )
            }

            if (expensePercent > 5) {
                val expenseMidAngle =
                    (-90f + 360f * safeProgress + (360f * (1f - safeProgress)) / 2f) * (Math.PI / 180.0)
                val expenseX = (canvasWidth / 2f + (radius * 0.58f) * Math.cos(expenseMidAngle)).toFloat()
                val expenseY = (canvasHeight / 2f + (radius * 0.58f) * Math.sin(expenseMidAngle)).toFloat()
                drawText(
                    NumberUtils.formatByLocale(context, "$expensePercent%"),
                    expenseX,
                    expenseY + paint.textSize * 0.35f,
                    paint
                )
            }
        }
    }
}

@Composable
fun TransactionSummaryRow(
    label: String,
    amount: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            fontSize = 13.sp,
            maxLines = 1,
            softWrap = false
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = NumberUtils.formatAmountByLocale(context, amount),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

@Composable
fun TransactionSummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    balance: Double,
    incomeProgress: Float
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(96.dp)) {
                SolidPieChart(
                    incomeProgress = incomeProgress,
                    incomeColor = tealColor,
                    expenseColor = softRedColor
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                TransactionSummaryRow(
                    stringResource(R.string.income),
                    totalIncome.toString(),
                    MaterialTheme.colorScheme.primary
                )
                TransactionSummaryRow(
                    stringResource(R.string.expense),
                    totalExpense.toString(),
                    softRedColor
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
                TransactionSummaryRow(
                    stringResource(R.string.balance),
                    balance.toString(),
                    if (balance >= 0) MaterialTheme.colorScheme.primary else softRedColor
                )
            }
        }
    }
}
