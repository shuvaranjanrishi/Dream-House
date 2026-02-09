package com.therishideveloper.dreamhouse.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.NumberUtils
import kotlin.math.roundToInt
import com.therishideveloper.dreamhouse.R

@Composable
fun CalculatorDialog(
    initialExpression: String,
    initialResult: String,
    onMinimize: (String, String) -> Unit,
    onClose: () -> Unit
) {
    var expression by remember { mutableStateOf(initialExpression) }
    var result by remember { mutableStateOf(initialResult) }

    // Drag and Scroll states
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Auto scroll to end when expression changes
    LaunchedEffect(expression) { scrollState.animateScrollTo(scrollState.maxValue) }

    // Reusable Button List Configuration
    val buttonRows = remember {
        listOf(
            listOf(
                CalcButtonData("7", CalcAction.Number("7")),
                CalcButtonData("8", CalcAction.Number("8")),
                CalcButtonData("9", CalcAction.Number("9")),
                CalcButtonData("/", CalcAction.Operation("/"))
            ),
            listOf(
                CalcButtonData("4", CalcAction.Number("4")),
                CalcButtonData("5", CalcAction.Number("5")),
                CalcButtonData("6", CalcAction.Number("6")),
                CalcButtonData("*", CalcAction.Operation("*"))
            ),
            listOf(
                CalcButtonData("1", CalcAction.Number("1")),
                CalcButtonData("2", CalcAction.Number("2")),
                CalcButtonData("3", CalcAction.Number("3")),
                CalcButtonData("-", CalcAction.Operation("-"))
            ),
            listOf(
                CalcButtonData(".", CalcAction.Decimal),
                CalcButtonData("0", CalcAction.Number("0")),
                CalcButtonData("Del", CalcAction.Delete),
                CalcButtonData("+", CalcAction.Operation("+"))
            ),
            listOf(
                CalcButtonData("C", CalcAction.Clear),
                CalcButtonData("=", CalcAction.Calculate, weight = 2f)
            )
        )
    }

    Dialog(
        onDismissRequest = { onMinimize(expression, result) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = { isDragging = false }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                    .width(320.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = if (isDragging) 0.8f else 0.98f),
                shadowElevation = 12.dp,
                border = if (isDragging) BorderStroke(1.dp, tealColor.copy(0.5f)) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .size(40.dp, 4.dp)
                            .background(Color.LightGray.copy(0.5f), CircleShape)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display Section
                    DisplayArea(expression, result, scrollState)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons Grid
                    buttonRows.forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { btn ->
                                CalculatorKey(
                                    data = btn,
                                    modifier = Modifier.weight(btn.weight),
                                    onClick = {
                                        handleCalculatorAction(
                                            btn.action,
                                            expression
                                        ) { newExp, newRes ->
                                            expression = newExp
                                            if (newRes.isNotEmpty()) result = newRes
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bottom Actions
                    DialogFooter(onClose, onMinimize = { onMinimize(expression, result) })
                }
            }
        }
    }
}

private fun simpleEvaluate(expr: String): String {
    return try {
        val tokens = expr.split(Regex("(?<=[-+*/])|(?=[-+*/])"))
        if (tokens.isEmpty() || tokens[0].isEmpty()) return "0"
        var total = tokens[0].toDouble()
        for (i in 1 until tokens.size step 2) {
            val op = tokens[i]
            val next = tokens[i + 1].toDouble()
            when (op) {
                "+" -> total += next
                "-" -> total -= next
                "*" -> total *= next
                "/" -> if (next != 0.0) total /= next
            }
        }
        if (total % 1 == 0.0) total.toLong().toString() else "%.2f".format(total)
    } catch (e: Exception) {
        "0: ${e.message}"
    }
}

// Define calculator button types for better logic handling
sealed class CalcAction {
    data class Number(val value: String) : CalcAction()
    data class Operation(val op: String) : CalcAction()
    object Calculate : CalcAction()
    object Clear : CalcAction()
    object Delete : CalcAction()
    object Decimal : CalcAction()
}

// Data class for representing a Button UI
data class CalcButtonData(
    val label: String,
    val action: CalcAction,
    val weight: Float = 1f
)

// Centralized logic to handle expression updates
private fun handleCalculatorAction(
    action: CalcAction,
    currentExpr: String,
    onUpdate: (String, String) -> Unit
) {
    var newExpr = currentExpr
    var newResult = ""

    when (action) {
        is CalcAction.Clear -> {
            newExpr = ""; newResult = "0"
        }

        is CalcAction.Delete -> {
            if (newExpr.isNotEmpty()) newExpr = newExpr.dropLast(1)
        }

        is CalcAction.Calculate -> {
            newResult = try {
                simpleEvaluate(newExpr)
            } catch (e: Exception) {
                "Error: " + e.message
            }
        }

        is CalcAction.Decimal -> {
            if (!newExpr.endsWith(".")) newExpr += "."
        }

        is CalcAction.Number -> newExpr += action.value
        is CalcAction.Operation -> newExpr += action.op
    }
    onUpdate(newExpr, newResult)
}

@Composable
fun DisplayArea(expression: String, result: String, scrollState: ScrollState) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
        Text(
            text = NumberUtils.formatByLocale(context, expression).ifEmpty { "0" },
            fontSize = 18.sp,
            color = Color.Gray,
            maxLines = 1,
            modifier = Modifier.horizontalScroll(scrollState)
        )
        Text(
            text = NumberUtils.formatByLocale(context, result),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF008080),
            maxLines = 1
        )
    }
}

@Composable
fun CalculatorKey(data: CalcButtonData, modifier: Modifier, onClick: () -> Unit) {
    val context = LocalContext.current
    val tealColor = Color(0xFF008080)
    val color = when (data.action) {
        is CalcAction.Calculate -> tealColor
        is CalcAction.Clear, is CalcAction.Delete -> Color(0xFFFF5252).copy(0.1f)
        is CalcAction.Operation -> tealColor.copy(0.1f)
        else -> Color.LightGray.copy(0.25f)
    }
    val contentColor = if (data.action is CalcAction.Calculate) Color.White
    else if (data.action is CalcAction.Clear || data.action is CalcAction.Delete) Color(0xFFFF5252)
    else Color.Black

    Surface(
        modifier = modifier
            .height(50.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = color
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                NumberUtils.formatByLocale(context, data.label),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
fun DialogFooter(onClose: () -> Unit, onMinimize: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onClose) {
            Text(
                stringResource(R.string.btn_close),
                color = Color.Red.copy(0.7f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onMinimize,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008080))
        ) {
            Text(stringResource(R.string.btn_minimize))
        }
    }
}