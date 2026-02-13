package com.therishideveloper.dreamhouse.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.entity.EstimationRecord
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.util.NumberUtils

// --- ছোট ছোট রিইউজেবল কম্পোনেন্টসমূহ ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, color = Color.White) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, null, tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF008080))
    )
}

@Composable
fun CalculatorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun InputRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall,
        color = Color.DarkGray,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun CalculateButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = tealColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(stringResource(R.string.btn_calculate), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EstimationDetailsDialog(
    record: EstimationRecord,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = tealColor)
            ) {
                Text(stringResource(R.string.btn_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        },
        title = { DialogHeader() },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // এরিয়া এবং তারিখ সেকশন
                AreaAndDateRow(record)

                // তলা বা ফ্লোর ইনফো
                FloorInfoRow(record)

                HorizontalDivider(thickness = 0.5.dp)

                // টেবিল হেডার
                MaterialTableHeader()

                // মালামালের লিস্ট
                MaterialList(record)

                Spacer(modifier = Modifier.height(8.dp))

                // গ্র্যান্ড টোটাল সেকশন
                GrandTotalSection(record.totalEstimatedCost)
            }
        }
    )
}

// --- ডায়ালগের ছোট ছোট সাব-কম্পোনেন্ট ---

@Composable
fun DialogHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.Home, null, tint = tealColor, modifier = Modifier.size(28.dp))
        Column {
            Text(
                stringResource(R.string.title_estimation_summary),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                stringResource(R.string.estimation_disclaimer),
                color = Color.Red.copy(alpha = 0.6f),
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AreaAndDateRow(record: EstimationRecord) {
    val context = LocalContext.current
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(
            "${stringResource(R.string.label_area)} ${
                NumberUtils.formatByLocale(
                    context,
                    record.totalArea
                )
            } ${stringResource(R.string.unit_sqft)}",
            color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp
        )
        Text(
            "${stringResource(R.string.label_date)} ${
                DateUtils.formatToDisplay(
                    context,
                    record.date
                )
            }",
            color = Color.Gray, fontSize = 12.sp
        )
    }
}

@Composable
fun FloorInfoRow(record: EstimationRecord) {
    val context = LocalContext.current
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
        Text(
            text = "${stringResource(R.string.label_foundation)}: ${
                NumberUtils.formatByLocale(
                    context,
                    record.foundationFloors.toString()
                )
            } ${stringResource(R.string.unit_floor)}",
            color = tealColor, fontWeight = FontWeight.SemiBold, fontSize = 12.sp
        )
        Text(
            text = "${stringResource(R.string.label_build_floor)}: ${
                NumberUtils.formatByLocale(
                    context,
                    record.floorsToBuild.toString()
                )
            } ${stringResource(R.string.unit_floor)}",
            color = tealColor, fontWeight = FontWeight.SemiBold, fontSize = 12.sp
        )
    }
}

@Composable
fun MaterialTableHeader() {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(
            text = stringResource(R.string.label_material),
            modifier = Modifier.weight(1.2f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Text(
            text = stringResource(R.string.label_qty),
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Text(
            text = stringResource(R.string.label_amount),
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun MaterialList(record: EstimationRecord) {
    val context = LocalContext.current

    // রড, সিমেন্ট, বালি, ইট, পাথর, লেবার
    val items = listOf(
        Triple(
            stringResource(Category.ROD.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.rodQty
                )
            } ${stringResource(Category.ROD.unitRes)}",
            record.rodCost
        ),
        Triple(
            stringResource(Category.CEMENT.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.cementQty
                )
            } ${stringResource(Category.CEMENT.unitRes)}",
            record.cementCost
        ),
        Triple(
            stringResource(Category.SAND.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.sandQty
                )
            } ${stringResource(Category.SAND.unitRes)}",
            record.sandCost
        ),
        Triple(
            stringResource(Category.BRICKS.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.brickQty
                )
            } ${stringResource(Category.BRICKS.unitRes)}",
            record.brickCost
        ),
        Triple(
            stringResource(Category.STONE.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.stoneQty
                )
            } ${stringResource(Category.STONE.unitRes)}",
            record.stoneCost
        ),
        Triple(
            stringResource(Category.MASON_LABOR.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.laborQty
                )
            } ${stringResource(Category.MASON_LABOR.unitRes)}",
            record.laborCost
        )
    )

    items.forEach { (label, qty, cost) ->
        DialogRowItem(label, qty, cost)
    }

    // Others (বিবিধ) সেকশন
    val parts = record.othersDetails.split(",")
    if (parts.size >= 3) {
        val othersQty = "${stringResource(R.string.guna)}: ${
            NumberUtils.formatByLocale(
                context,
                parts[0]
            )
        }${stringResource(R.string.unit_kg)}\n" +
                "${stringResource(R.string.loha)}: ${
                    NumberUtils.formatByLocale(
                        context,
                        parts[1]
                    )
                }${stringResource(R.string.unit_kg)}\n" +
                "${stringResource(R.string.poly)}: ${
                    NumberUtils.formatByLocale(
                        context,
                        parts[2]
                    )
                }${stringResource(R.string.unit_sqft)}"
        DialogRowItem(stringResource(Category.OTHERS.titleRes), othersQty, record.othersCost)
    }
}

@Composable
fun GrandTotalSection(totalCost: String) {
    val context = LocalContext.current
    Surface(
        color = tealColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.label_grand_total),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                NumberUtils.formatAmountByLocale(context, totalCost),
                fontWeight = FontWeight.ExtraBold, color = tealColor, fontSize = 18.sp
            )
        }
    }
}

@Composable
fun DialogRowItem(label: String, qty: String, cost: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            modifier = Modifier.weight(1.2f),
            color = Color.Gray,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
        Text(
            qty,
            modifier = Modifier.weight(1f),
            color = Color.Gray,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
        Text(
            NumberUtils.formatAmountByLocale(context, cost),
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

// --- ১. টপ বার সেকশন ---
@Composable
fun EstimationTopBar(
    onBack: () -> Unit,
    onShowPolicy: () -> Unit,
    onDownload: () -> Unit
) {
    Column(modifier = Modifier.background(Color(0xFF00796B))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 4.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
            Text(
                stringResource(R.string.title_final_estimation),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDownload) {
                Icon(Icons.Default.Download, null, tint = Color.White)
            }
            TextButton(onClick = onShowPolicy) {
                Text(stringResource(R.string.btn_policy), color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

// --- ২. মেইন কন্টেন্ট বডি ---
@Composable
fun EstimationContent(
    padding: PaddingValues,
    record: EstimationRecord,
    onNewCalculation: () -> Unit
) {
    Column(modifier = Modifier.padding(padding).fillMaxSize()) {
        // স্টিকি হেডার (তারিখ, এরিয়া, ফ্লোর)
        Surface(color = Color(0xFF00796B), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                EstimationHeaderInfo(record)
                Spacer(Modifier.height(12.dp))
                NewCalculationButton(onNewCalculation)
            }
        }

        // মালামালের তালিকা (স্ক্রোলযোগ্য)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                stringResource(R.string.label_material_breakdown),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray
            )

            MaterialCardsList(record)

            Spacer(modifier = Modifier.height(80.dp)) // বটম বারের জন্য স্পেস
        }
    }
}

// --- ৩. হেডার ইনফো (তারিখ, এরিয়া, ফ্লোর) ---
@Composable
 fun EstimationHeaderInfo(record: EstimationRecord) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            HeaderItem(
                label = stringResource(R.string.date),
                value = DateUtils.formatToDisplay(context, record.date)
            )
            HeaderItem(
                label = stringResource(R.string.label_total_area),
                value = NumberUtils.formatByLocale(context, record.totalArea),
                alignment = Alignment.End
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            HeaderItem(
                label = stringResource(R.string.label_foundation),
                value = NumberUtils.formatByLocale(context, record.foundationFloors.toString()) + " " + stringResource(R.string.unit_floor)
            )
            HeaderItem(
                label = stringResource(R.string.label_build_floor),
                value = NumberUtils.formatByLocale(context, record.floorsToBuild.toString()) + " " + stringResource(R.string.unit_floor),
                alignment = Alignment.End
            )
        }
    }
}

// --- ৪. ম্যাটেরিয়াল কার্ড লিস্ট ---
@Composable
fun MaterialCardsList(record: EstimationRecord) {
    val context = LocalContext.current

    val materialItems = listOf(
        Category.ROD to (record.rodQty to record.rodCost),
        Category.CEMENT to (record.cementQty to record.cementCost),
        Category.SAND to (record.sandQty to record.sandCost),
        Category.BRICKS to (record.brickQty to record.brickCost),
        Category.STONE to (record.stoneQty to record.stoneCost),
        Category.MASON_LABOR to (record.laborQty to record.laborCost)
    )

    materialItems.forEach { (category, data) ->
        MaterialDetailCard(
            icon = category.icon,
            name = stringResource(category.titleRes),
            qty = "${data.first} ${stringResource(category.unitRes)}",
            rate = "${getRateByCategory(category, record)}/${stringResource(category.unitRes)}",
            cost = data.second
        )
    }

    // Others Section
    val othersQty = formatOthersQty(context, record.othersDetails)
    MaterialDetailCard(
        icon = Category.OTHERS.icon,
        name = stringResource(Category.OTHERS.titleRes),
        qty = othersQty,
        rate = stringResource(R.string.label_standard),
        cost = record.othersCost
    )
}

// --- ৫. বটম বার (টোটাল কস্ট) ---
@Composable
fun EstimationBottomBar(totalCost: String) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = tealColor,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.label_total_estimated_cost), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(
                text = NumberUtils.formatAmountByLocale(context, totalCost),
                color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// --- হেল্পার ফাংশনসমূহ ---

@Composable
fun NewCalculationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(Icons.Default.Calculate, null, tint = Color.White, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.btn_new_calculation), color = Color.White)
    }
}

@Composable
fun EmptyState(padding: PaddingValues) {
    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.msg_no_estimation), color = Color.Gray)
    }
}

fun getRateByCategory(category: Category, record: EstimationRecord): String = when(category) {
    Category.ROD -> record.rodRate
    Category.CEMENT -> record.cementRate
    Category.SAND -> record.sandRate
    Category.BRICKS -> record.brickRate
    Category.STONE -> record.stoneRate
    Category.MASON_LABOR -> record.laborRate
    else -> "0"
}

@Composable
fun formatOthersQty(context: android.content.Context, details: String): String {
    val parts = details.split(",")
    if (parts.size < 3) return ""
    return "${stringResource(R.string.guna)}: ${parts[0]} ${stringResource(R.string.unit_kg)}\n" +
            "${stringResource(R.string.loha)}: ${parts[1]} ${stringResource(R.string.unit_kg)}\n" +
            "${stringResource(R.string.poly)}: ${parts[2]} ${stringResource(R.string.unit_sqft)}"
}

@Composable
fun MaterialDetailCard(icon: ImageVector, name: String, qty: String, rate: String, cost: String) {
    val context = LocalContext.current
    val qty = NumberUtils.formatByLocale(context, qty)
    val rate = NumberUtils.formatByLocale(context, rate)
    val cost = NumberUtils.formatAmountByLocale(context, cost)
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        tealColor.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = tealColor
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                Text(
                    stringResource(R.string.label_qty_short) + ": $qty",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    stringResource(R.string.label_rate_short) + ": $rate",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(cost, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF00796B))
        }
    }
}

@Composable
fun HeaderItem(label: String, value: String, alignment: Alignment.Horizontal = Alignment.Start) {
    Column(horizontalAlignment = alignment) {
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
