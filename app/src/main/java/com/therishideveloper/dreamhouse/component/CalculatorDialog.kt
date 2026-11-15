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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.NumberUtils
import java.util.Locale
import kotlin.math.roundToInt

private object CalculatorWindowState {
    const val DEFAULT_WIDTH_DP = 248f
    const val MIN_WIDTH_DP = 200f
    const val MAX_WIDTH_DP = 350f

    var widthDp: Float = DEFAULT_WIDTH_DP
    var offsetX: Float = 0f
    var offsetY: Float = 0f

    fun save(width: Float, x: Float, y: Float) {
        widthDp = width
        offsetX = x
        offsetY = y
    }

    fun reset() {
        widthDp = DEFAULT_WIDTH_DP
        offsetX = 0f
        offsetY = 0f
    }
}

@Composable
fun CalculatorDialog(
    initialExpression: String,
    initialResult: String,
    onMinimize: (String, String) -> Unit,
    onClose: () -> Unit
) {
    var expression by remember { mutableStateOf(initialExpression) }
    var result by remember { mutableStateOf(initialResult) }

    // Drag, Resize, and Scroll states
    var offsetX by remember { mutableFloatStateOf(CalculatorWindowState.offsetX) }
    var offsetY by remember { mutableFloatStateOf(CalculatorWindowState.offsetY) }
    var isDragging by remember { mutableStateOf(false) }
    var isResizing by remember { mutableStateOf(false) }
    var widthDp by remember { mutableFloatStateOf(CalculatorWindowState.widthDp) }
    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    val keyHeight = (widthDp * 0.165f).coerceIn(34f, 54f).dp
    val exprFontSize = (widthDp * 0.06f).coerceIn(13f, 20f).sp
    val resultFontSize = (widthDp * 0.105f).coerceIn(21f, 34f).sp
    val keyFontSize = (widthDp * 0.062f).coerceIn(12f, 19f).sp

    val handleMinimize = {
        CalculatorWindowState.save(widthDp, offsetX, offsetY)
        onMinimize(expression, result)
    }

    val handleClose = {
        CalculatorWindowState.reset()
        onClose()
    }

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

    val resizeDesc = stringResource(R.string.cd_resize_calculator)

    Dialog(
        onDismissRequest = handleMinimize,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val maxWidthDp = ((constraints.maxWidth / density.density) - 24f)
                .coerceIn(CalculatorWindowState.MIN_WIDTH_DP, CalculatorWindowState.MAX_WIDTH_DP)
            val maxHorizontalOffset = ((constraints.maxWidth - (widthDp * density.density)) / 2f + 40f)
                .coerceAtLeast(0f)
            val maxVerticalOffset = (constraints.maxHeight / 2f - 100f)
                .coerceAtLeast(0f)
            val currentMaxWidthDp by rememberUpdatedState(maxWidthDp)
            val currentMaxHorizontalOffset by rememberUpdatedState(maxHorizontalOffset)
            val currentMaxVerticalOffset by rememberUpdatedState(maxVerticalOffset)

            Surface(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            offsetX.coerceIn(-currentMaxHorizontalOffset, currentMaxHorizontalOffset).roundToInt(),
                            offsetY.coerceIn(-currentMaxVerticalOffset, currentMaxVerticalOffset).roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = { isDragging = false },
                            onDragCancel = { isDragging = false }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount.x)
                                .coerceIn(-currentMaxHorizontalOffset, currentMaxHorizontalOffset)
                            offsetY = (offsetY + dragAmount.y)
                                .coerceIn(-currentMaxVerticalOffset, currentMaxVerticalOffset)
                        }
                    }
                    .width(widthDp.coerceIn(CalculatorWindowState.MIN_WIDTH_DP, maxWidthDp).dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(
                    alpha = if (isDragging || isResizing) 0.88f else 0.98f
                ),
                shadowElevation = 12.dp,
                border = BorderStroke(
                    width = if (isDragging || isResizing) 1.5.dp else 0.8.dp,
                    color = if (isDragging || isResizing) {
                        tealColor.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    }
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        // Top Drag Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp, 4.dp)
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                        CircleShape
                                    )
                            )
                        }

                        // Display Section
                        DisplayArea(
                            expression = expression,
                            result = result,
                            scrollState = scrollState,
                            exprFontSize = exprFontSize,
                            resultFontSize = resultFontSize
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Buttons Grid
                        buttonRows.forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                row.forEach { btn ->
                                    CalculatorKey(
                                        data = btn,
                                        keyHeight = keyHeight,
                                        fontSize = keyFontSize,
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bottom Actions
                        DialogFooter(onClose = handleClose, onMinimize = handleMinimize)
                    }

                    // Top-Left Touch & Drag Resize Button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(48.dp)
                            .testTag("calculator_resize_handle")
                            .semantics { contentDescription = resizeDesc }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { isResizing = true },
                                    onDragEnd = { isResizing = false },
                                    onDragCancel = { isResizing = false }
                                ) { change, dragAmount ->
                                    change.consume()
                                    val deltaDp = (-dragAmount.x - dragAmount.y * 0.5f) / density.density
                                    val newWidth = (widthDp + deltaDp).coerceIn(
                                        CalculatorWindowState.MIN_WIDTH_DP,
                                        currentMaxWidthDp
                                    )
                                    val actualDeltaPx = (newWidth - widthDp) * density.density
                                    widthDp = newWidth
                                    offsetX = (offsetX - actualDeltaPx / 2f)
                                        .coerceIn(-currentMaxHorizontalOffset, currentMaxHorizontalOffset)
                                    offsetY = (offsetY - actualDeltaPx * 0.45f)
                                        .coerceIn(-currentMaxVerticalOffset, currentMaxVerticalOffset)
                                }
                            },
                        contentAlignment = Alignment.TopStart
                    ) {
                        Surface(
                            modifier = Modifier
                                .padding(start = 6.dp, top = 6.dp)
                                .size(26.dp),
                            shape = CircleShape,
                            color = if (isResizing) {
                                tealColor
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.OpenInFull,
                                    contentDescription = resizeDesc,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .graphicsLayer { rotationZ = 90f },
                                    tint = if (isResizing) {
                                        Color.White
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun simpleEvaluate(expr: String): String {
    return try {
        val cleaned = expr.trim()
        if (cleaned.isEmpty()) return "0"

        val normalized = if (cleaned.startsWith("-")) "0$cleaned" else cleaned
        val rawTokens = normalized.split(Regex("(?<=[-+*/])|(?=[-+*/])"))
            .filter { it.isNotEmpty() }
        if (rawTokens.isEmpty()) return "0"

        // Trim trailing dangling operator if user tapped '=' mid-expression
        val tokens = if (rawTokens.last() in listOf("+", "-", "*", "/")) {
            rawTokens.dropLast(1)
        } else {
            rawTokens
        }
        if (tokens.isEmpty()) return "0"

        var total = tokens[0].toDoubleOrNull() ?: return "0"
        var i = 1
        while (i + 1 < tokens.size) {
            val op = tokens[i]
            val next = tokens[i + 1].toDoubleOrNull() ?: break
            when (op) {
                "+" -> total += next
                "-" -> total -= next
                "*" -> total *= next
                "/" -> if (next != 0.0) total /= next else return "0"
            }
            i += 2
        }
        if (total.isNaN() || total.isInfinite()) return "0"
        if (total % 1.0 == 0.0) {
            total.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", total).trimEnd('0').trimEnd('.')
        }
    } catch (_: Exception) {
        "0"
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
            newResult = simpleEvaluate(newExpr)
        }

        is CalcAction.Decimal -> {
            val lastNumberSegment = newExpr.substringAfterLastAny(charArrayOf('+', '-', '*', '/'))
            if (!lastNumberSegment.contains('.')) {
                newExpr += if (lastNumberSegment.isEmpty()) "0." else "."
            }
        }

        is CalcAction.Number -> newExpr += action.value
        is CalcAction.Operation -> {
            if (newExpr.isEmpty() && action.op != "-") return
            if (newExpr.lastOrNull() in listOf('+', '-', '*', '/')) {
                newExpr = newExpr.dropLast(1) + action.op
            } else {
                newExpr += action.op
            }
        }
    }
    onUpdate(newExpr, newResult)
}

private fun String.substringAfterLastAny(delimiters: CharArray): String {
    val idx = lastIndexOfAny(delimiters)
    return if (idx == -1) this else substring(idx + 1)
}

@Composable
fun DisplayArea(
    expression: String,
    result: String,
    scrollState: ScrollState,
    exprFontSize: TextUnit = 16.sp,
    resultFontSize: TextUnit = 26.sp
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
        Text(
            text = NumberUtils.formatByLocale(context, expression).ifEmpty { "0" },
            fontSize = exprFontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.horizontalScroll(scrollState)
        )
        Text(
            text = NumberUtils.formatByLocale(context, result),
            fontSize = resultFontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )
    }
}

@Composable
fun CalculatorKey(
    data: CalcButtonData,
    keyHeight: Dp = 40.dp,
    fontSize: TextUnit = 15.sp,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val actionTint = MaterialTheme.colorScheme.primary
    val color = when (data.action) {
        is CalcAction.Calculate -> tealColor
        is CalcAction.Clear, is CalcAction.Delete -> Color(0xFFFF5252).copy(0.14f)
        is CalcAction.Operation -> actionTint.copy(0.14f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (data.action) {
        is CalcAction.Calculate -> Color.White
        is CalcAction.Clear, is CalcAction.Delete -> Color(0xFFFF5252)
        is CalcAction.Operation -> actionTint
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .height(keyHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = color
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                NumberUtils.formatByLocale(context, data.label),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1
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
        TextButton(
            onClick = onClose,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                stringResource(R.string.btn_close),
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Button(
            onClick = onMinimize,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = tealColor,
                contentColor = Color.White
            )
        ) {
            Text(
                stringResource(R.string.btn_minimize),
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
}