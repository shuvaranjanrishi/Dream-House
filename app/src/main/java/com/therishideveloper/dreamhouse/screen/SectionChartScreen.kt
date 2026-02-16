package com.therishideveloper.dreamhouse.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dailyexpense.ui.theme.tealColor
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.CurrentBalance
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.data.model.SectionData
import com.therishideveloper.dreamhouse.data.model.SectionMeta
import com.therishideveloper.dreamhouse.util.NumberUtils
import com.therishideveloper.dreamhouse.util.PdfGenerator
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel
import com.therishideveloper.dreamhouse.viewmodel.SectionChartViewModel
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionChartScreen(
    onBack: () -> Unit,
    projectViewModel: ProjectViewModel,
    transactionViewModel: TransactionViewModel,
    viewModel: SectionChartViewModel
) {
    val context = LocalContext.current
    val project by projectViewModel.activeProject.collectAsStateWithLifecycle()
    val currentBalance by transactionViewModel.currentBalance.collectAsStateWithLifecycle()
    val expenseState by viewModel.expenseState.collectAsState()
    val incomeState by viewModel.incomeState.collectAsState()
    val pdfGenerator = remember { PdfGenerator(context) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_income),
        stringResource(R.string.tab_expense)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.menu_section_chart),
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
                actions = {
                    IconButton(onClick = {
                        pdfGenerator.generateSectionReport(
                            context = context,
                            incomeState = incomeState,
                            expenseState = expenseState,
                            projectName = project?.projectName
                                ?: "Dream House Project", // আপনি ডাইনামিক করতে পারেন
                            projectAddress = project?.address ?: "Project Address"
                        )
                    }) {
                        Icon(Icons.Default.Download, null, tint = Color.White)
                    }
                    CurrentBalance(currentBalance.toString())
                }

            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            SummaryCard(
                income = incomeState.totalAmount,
                expense = expenseState.totalAmount
            )

            SecondaryTabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // --- ৩. ডাইনামিক লিস্ট এরিয়া ---
            val currentState = if (selectedTab == 0) incomeState else expenseState

            if (currentState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (currentState.sectionData.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.label_no_data_available), color = Color.Gray)
                }
            } else {
                SectionList(
                    sectionDataList = currentState.sectionData,
                    grandTotal = currentState.totalAmount,
                    isIncome = selectedTab == 0
                )
            }
        }
    }
}

@Composable
fun SectionList(
    sectionDataList: List<SectionData>,
    grandTotal: Double,
    isIncome: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(sectionDataList) { section ->
            SectionItem(
                sectionKey = section.sectionKey,
                sectionTotal = section.sectionTotal,
                grandTotal = grandTotal,
                isIncome = isIncome,
                categories = section.categories
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SectionItem(
    sectionKey: String,
    sectionTotal: Double,
    grandTotal: Double,
    isIncome: Boolean,
    categories: List<com.therishideveloper.dreamhouse.data.model.CategorySum>
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val progress = if (grandTotal > 0) (sectionTotal / grandTotal).toFloat() else 0f
    val percentage = NumberUtils.formatByLocale(context, (progress * 100).roundToInt().toString())
    val sectionName = stringResource(SectionMeta.getSectionTitleRes(sectionKey))
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(sectionName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // প্রোগ্রেস বার
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828),
                        trackColor = Color.LightGray.copy(0.4f)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // টাকা এবং পার্সেন্টেজ টেক্সট
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = NumberUtils.formatAmountByLocale(
                            context,
                            sectionTotal.roundToInt().toString()
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    // এখানে পার্সেন্টেজ দেখানো হচ্ছে
                    Text(
                        text = "$percentage%",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp, start = 8.dp)) {
                    categories.forEach { catSum ->
                        val categoryEnum = Category.fromDbKey(catSum.category)
                        CategoryRow(
                            category = categoryEnum,
                            catTotal = catSum.totalAmount,
                            sectionTotal = sectionTotal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(category: Category, catTotal: Double, sectionTotal: Double) {
    val context = LocalContext.current
    val catProgress = if (sectionTotal > 0) (catTotal / sectionTotal).toFloat() else 0f
    val catTotal = NumberUtils.formatAmountByLocale(context, catTotal.roundToInt().toString())
    val percentage =
        NumberUtils.formatByLocale(context, (catProgress * 100).roundToInt().toString())

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))

            // ক্যাটাগরি নাম
            Text(
                stringResource(category.titleRes),
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "$catTotal ($percentage%)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { catProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF008080).copy(0.6f),
            trackColor = Color.LightGray.copy(0.2f)
        )
    }
}

@Composable
fun SummaryCard(income: Double, expense: Double) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = tealColor.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, tealColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // বাম পাশে ইনকাম
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.tab_income),
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = NumberUtils.formatAmountByLocale(
                        context,
                        income.roundToInt().toString()
                    ),
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .fillMaxHeight(),
                thickness = 1.dp,
                color = Color.DarkGray
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.tab_expense),
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = NumberUtils.formatAmountByLocale(
                        context,
                        expense.roundToInt().toString()
                    ),
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}