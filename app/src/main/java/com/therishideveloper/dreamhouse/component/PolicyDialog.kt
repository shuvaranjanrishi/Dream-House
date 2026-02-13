package com.therishideveloper.dreamhouse.component

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.MenuGridItem
import com.therishideveloper.dreamhouse.component.TransactionSummaryRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.component.ActionButton
import com.therishideveloper.dreamhouse.component.CalculatorDialog
import com.therishideveloper.dreamhouse.component.CalculatorFab
import com.therishideveloper.dreamhouse.component.CurrentBalance
import com.therishideveloper.dreamhouse.component.MagicWelcomeOverlay
import com.therishideveloper.dreamhouse.component.SolidPieChart
import com.therishideveloper.dreamhouse.component.SummaryClickableRow
import com.therishideveloper.dreamhouse.data.model.TransactionPeriod
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.navigation.Screens
import com.therishideveloper.dreamhouse.ui.theme.expenseRed
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DashboardUtils
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.util.NumberUtils
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay

@Composable
fun PolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = stringResource(R.string.title_policy),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(8.dp))
                // Disclaimer Section
                Surface(
                    color = Color.Red.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.disclaimer_text),
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 14.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Steps from 1 to 5
                PolicyStepItem(stringResource(R.string.policy_step_1))
                PolicyStepItem(stringResource(R.string.policy_step_2))
                PolicyStepItem(stringResource(R.string.policy_step_3))
                PolicyStepItem(stringResource(R.string.policy_step_4))
                PolicyStepItem(stringResource(R.string.policy_step_5))

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)

                // Excluded Section (Finishing Items)
                Text(
                    text = stringResource(R.string.policy_excluded),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_close), color = tealColor)
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White
    )
}

@Composable
fun PolicyStepItem(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color.Black.copy(alpha = 0.8f),
        lineHeight = 18.sp
    )
}