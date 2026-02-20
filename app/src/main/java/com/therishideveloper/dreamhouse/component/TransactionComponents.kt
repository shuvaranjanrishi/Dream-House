package com.therishideveloper.dreamhouse.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.ui.theme.softRedColor
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.util.NumberUtils

@Composable
fun DownloadLoaderDialog() {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 32.dp, horizontal = 24.dp)
                    .fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = tealColor,
                    strokeWidth = 4.dp,
                    trackColor = tealColor.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.msg_generating_report), // Excel report generation text
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.msg_wait_excel),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val context = LocalContext.current
    val category = Category.fromDbKey(transaction.category)
    val isIncome = transaction.transactionType == TransactionType.INCOME.dbKey

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isIncome) tealColor.copy(alpha = 0.1f) else softRedColor.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Showing category icon instead of first letter for better UI
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isIncome) tealColor else softRedColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(category.titleRes) + " • ${
                        DateUtils.formatToDisplay(
                            context,
                            transaction.date
                        )
                    }",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            val amount =
                (if (isIncome) "+ " else "- ") + NumberUtils.formatAmountByLocale(
                    context,
                    transaction.amount.toString()
                )
            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) tealColor else softRedColor,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun TransactionItemWithActions(
    transaction: Transaction,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val category = Category.fromDbKey(transaction.category)
    val type = TransactionType.fromDbKey(transaction.transactionType)
    val isIncome = type == TransactionType.INCOME

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isIncome) tealColor.copy(alpha = 0.1f) else softRedColor.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Showing category icon instead of first letter for better UI
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isIncome) tealColor else softRedColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.description, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Text(
                    text = stringResource(category.titleRes) + " • ${
                        DateUtils.formatToDisplay(
                            context,
                            transaction.date
                        )
                    }",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val amount = (if (isIncome) "+" else "-") + NumberUtils.formatAmountByLocale(
                    context,
                    transaction.amount.toString()
                )
                Text(
                    text = amount,
                    color = if (isIncome) tealColor else softRedColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailSheet(
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val category = Category.fromDbKey(transaction.category)
    val type = TransactionType.fromDbKey(transaction.transactionType)
    val isIncome = type == TransactionType.INCOME

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isIncome) tealColor.copy(0.1f) else softRedColor.copy(0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = if (isIncome) tealColor else softRedColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Text(
                text = stringResource(category.titleRes),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = (if (isIncome) "+ " + stringResource(R.string.currency_symbol) else "- " + stringResource(
                    R.string.currency_symbol
                )) + NumberUtils.formatByLocale(context, transaction.amount.toString()),
                color = if (isIncome) tealColor else softRedColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            DetailInfoRow(
                label = stringResource(R.string.date),
                value = DateUtils.formatToDisplay(context, transaction.date)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.description),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = transaction.description,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}
