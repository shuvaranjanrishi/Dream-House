package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therishideveloper.dailyexpense.ui.theme.expenseRed
import com.therishideveloper.dailyexpense.ui.theme.tealColor
import android.graphics.Paint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.component.CurrentBalance
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel
import kotlin.math.cos
import kotlin.math.sin
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.data.model.CategorySum
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.util.NumberUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChartScreen(
    onBack: () -> Unit,
    viewModel: TransactionViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()

    val tabs = listOf(
        stringResource(R.string.tab_income),
        stringResource(R.string.tab_expense)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.title_category_analytics),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor),
                actions = { CurrentBalance(currentBalance.toString()) }

            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // ১. Tab Selection
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = tealColor,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = if (selectedTab == 0) tealColor else expenseRed
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == index) (if (index == 0) tealColor else expenseRed) else Color.Gray
                            )
                        }
                    )
                }
            }

            // ২. Content based on selection
            CategoryAnalysisContent(isIncome = selectedTab == 0, viewModel)
        }
    }
}

@Composable
fun CategoryAnalysisContent(isIncome: Boolean, viewModel: TransactionViewModel) {
    val context = LocalContext.current
    val categoryData by (if (isIncome) viewModel.incomeCategorySums else viewModel.expenseCategorySums)
        .collectAsStateWithLifecycle()

    val themeColor = if (isIncome) tealColor else expenseRed

    // 2. Chart colors list (Kept the same as your UI)
    val chartColors = listOf(
        themeColor,
        Color(0xFFFFC107), // Amber
        Color(0xFF03A9F4), // Light Blue
        Color(0xFF8BC34A), // Light Green
        Color(0xFF9C27B0), // Purple
        Color(0xFFFF5722), // Deep Orange
        Color(0xFFE91E63)  // Pink
    )

    // 3. Calculate total amount from database records
    val totalAmount = categoryData.sumOf { it.totalAmount }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp), // Added padding for better look
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Text(
                            stringResource(if (isIncome) R.string.label_total_income_colon else R.string.label_total_expense_colon),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            stringResource(R.string.currency_symbol) + NumberUtils.formatByLocale(
                                context,
                                totalAmount.toString()
                            ),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = themeColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- Solid Multi-color Chart Section ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (categoryData.isNotEmpty()) {
                            MultiColorPieChart(
                                data = categoryData.map { it.category to it.totalAmount },
                                colors = chartColors,
                                modifier = Modifier.size(200.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.label_no_data_available),
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.label_category_distribution),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        itemsIndexed(categoryData) { index, item ->
            val categoryColor = chartColors[index % chartColors.size]
            CategoryStatItem(
                isIncome = isIncome,
                categorySum = item,
                amount = item.totalAmount,
                percentage = if (totalAmount > 0) (item.totalAmount / totalAmount).toFloat() else 0f,
                color = categoryColor
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MultiColorPieChart(
    data: List<Pair<String, Double>>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val totalSum = data.sumOf { it.second }.toFloat()
    val context = LocalContext.current

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = size.minDimension / 2
        var startAngle = -90f

        if (totalSum == 0f) {
            drawCircle(color = Color.LightGray.copy(0.2f), radius = radius)
        } else {
            data.forEachIndexed { index, item ->
                val sweepAngle = (item.second.toFloat() / totalSum) * 360f
                val percentage = (item.second.toFloat() / totalSum * 100).toInt()

                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )

                if (percentage > 5) {
                    val angleInRadians = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                    val textRadius = radius * 0.65f
                    val x = (canvasWidth / 2) + textRadius * cos(angleInRadians).toFloat()
                    val y = (canvasHeight / 2) + textRadius * sin(angleInRadians).toFloat()

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 35f
                            textAlign = Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(
                            NumberUtils.formatByLocale(context, percentage.toString()) + "%",
                            x,
                            y + 10f,
                            paint
                        )
                    }
                }
                startAngle += sweepAngle
            }
        }
    }
}

@Composable
fun CategoryStatItem(
    categorySum: CategorySum,
    amount: Double,
    percentage: Float,
    color: Color,
    isIncome: Boolean
) {
    val context = LocalContext.current
    val category = Category.fromDbKey(categorySum.category)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Color Indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color.copy(0.15f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isIncome) tealColor else expenseRed
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(category.titleRes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        stringResource(R.string.currency_symbol) + NumberUtils.formatByLocale(
                            context,
                            amount.toString()
                        ), fontWeight = FontWeight.Bold, color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { percentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = color,
                    trackColor = color.copy(0.1f),
                    strokeCap = StrokeCap.Round
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val formattedPercentage = NumberUtils.formatByLocale(
                        context,
                        (percentage * 100).toInt().toString()
                    )
                    Text(
                        text = stringResource(
                            R.string.label_percentage_of_total,
                            formattedPercentage
                        ),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = stringResource(R.string.label_target_100),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}