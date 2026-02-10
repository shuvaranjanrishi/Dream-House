package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.DateSelectionRow
import com.therishideveloper.dreamhouse.component.showToast
import com.therishideveloper.dreamhouse.data.entity.ProjectEntity
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSetupScreen(
    viewModel: ProjectViewModel = hiltViewModel(),
    onProjectSaved: () -> Unit
) {
    val context = LocalContext.current
    var projectName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var totalBudget by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis() + 86400000L * 90) }
    val diffInMs = endDate - startDate
    val totalDays = (diffInMs / (1000 * 60 * 60 * 24)).coerceAtLeast(0)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_project_setup), color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            Text(
                text = stringResource(R.string.hint_project_info),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = { Text(stringResource(R.string.label_house_name)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.label_address)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = totalBudget,
                onValueChange = { totalBudget = it },
                label = { Text(stringResource(R.string.label_total_budget_taka)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.label_project_timeline),
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateSelectionRow(
                label = stringResource(R.string.label_start_date),
                date = startDate,
                onDateSelected = {
                    startDate = it
                    if (startDate > endDate) endDate = startDate
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            DateSelectionRow(
                label = stringResource(R.string.label_end_date),
                date = endDate,
                minDate = startDate,
                onDateSelected = { endDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = tealColor.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, tealColor.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        null,
                        tint = tealColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.label_total_days_needed, totalDays.toInt()),
                        fontWeight = FontWeight.Bold,
                        color = tealColor,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            val errMsg = stringResource(R.string.err_invalid_input)

            Button(
                onClick = {
                    val budgetValue = totalBudget.toDoubleOrNull() ?: 0.0
                    if (projectName.isNotEmpty() && budgetValue > 0 && totalDays > 0) {
                        val project = ProjectEntity(
                            projectName = projectName,
                            address = address,
                            totalBudget = budgetValue,
                            startDate = startDate,
                            endDate = endDate
                        )
                        viewModel.saveProject(project)
                        onProjectSaved()
                    } else {
                        showToast(context, errMsg)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = tealColor)
            ) {
                Text(
                    stringResource(R.string.btn_start),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}