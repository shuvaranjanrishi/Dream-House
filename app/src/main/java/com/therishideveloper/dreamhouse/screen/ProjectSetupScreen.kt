package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.therishideveloper.dreamhouse.data.model.DreamHouseStrings
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSetupScreen(
    viewModel: ProjectViewModel = hiltViewModel(),
    projectId: Int? = null,
    onProjectSaved: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val strings = DreamHouseStrings.current

    // UI States
    var projectName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var totalBudget by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis() + 86400000L * 90) }

    val diffInMs = endDate - startDate
    val totalDays = (diffInMs / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    // Load data for edit mode
    LaunchedEffect(key1 = projectId) {
        if (projectId != null) {
            viewModel.activeProject.collect { project ->
                project?.let {
                    projectName = it.projectName
                    address = it.address
                    totalBudget = it.totalBudget.toString()
                    startDate = it.startDate
                    endDate = it.endDate
                }
            }
        }
    }

    Scaffold(
        topBar = {
            // শুধুমাত্র এডিট মোডে টপ বার দেখাবে
            if (projectId != null) {
                TopAppBar(
                    title = { Text(strings.menuConstructionPlan, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { onBack?.invoke() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // টপ বার থাকলে প্যাডিং নিবে, না থাকলে ০
                .padding(if (projectId == null) PaddingValues(0.dp) else paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (projectId == null) 40.dp else 0.dp))

            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = strings.hintProjectInfo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = { Text(strings.labelHouseName) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(strings.labelAddress) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = totalBudget,
                onValueChange = { totalBudget = it },
                label = { Text(strings.labelTotalBudgetTaka) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                strings.labelProjectTimeline,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateSelectionRow(
                label = strings.labelStartDate,
                date = startDate,
                onDateSelected = {
                    startDate = it
                    if (startDate > endDate) endDate = startDate
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            DateSelectionRow(
                label = strings.labelEndDate,
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

            Button(
                onClick = {
                    val budgetValue = totalBudget.toDoubleOrNull() ?: 0.0
                    if (projectName.isNotEmpty() && budgetValue > 0) {
                        val project = ProjectEntity(
                            id = projectId ?: 0,
                            projectName = projectName,
                            address = address,
                            totalBudget = budgetValue,
                            startDate = startDate,
                            endDate = endDate
                        )
                        // ProjectSetupScreen.kt এর বাটন ক্লিকের ভেতরে
                        if (projectId == null) { // নতুন প্রোজেক্ট
                            viewModel.saveProject(project)
                            viewModel.triggerWelcome() // শুধুমাত্র এখানে অভিনন্দন ট্রিগার হবে
                            onProjectSaved()
                        } else { // এডিট মোড
                            viewModel.saveProject(project)
                            onProjectSaved()
                        }
                    } else {
                        showToast(context, strings.errInvalidInputs)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = tealColor)
            ) {
                Text(
                    text = if (projectId == null) strings.labelCreateProject else strings.labelUpdateProject,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
