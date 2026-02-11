package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.therishideveloper.dreamhouse.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.component.ProjectBudgetCard
import com.therishideveloper.dreamhouse.navigation.Screens
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectOverviewScreen(
    onMenuClick: () -> Unit,
    navController: NavController,
    viewModel: ProjectViewModel
) {
    val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()
    val totalAllocatedValue by viewModel.getTotalAllocated(1)
        .collectAsStateWithLifecycle(initialValue = 0.0)

    val masterDuration = activeProject?.let {
        ((it.endDate - it.startDate) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    } ?: 0L

    val stages by viewModel.getStages(1).collectAsStateWithLifecycle(initialValue = emptyList())
    val allocatedDays = stages.sumOf {
        ((it.endDate - it.startDate) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.menu_construction_plan),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            "Menu",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        activeProject?.id?.let { id ->
                            navController.navigate("project_setup_screen?projectId=$id")
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            ProjectBudgetCard(
                projectName = activeProject?.projectName
                    ?: stringResource(R.string.label_default_project_name),
                address = activeProject?.address
                    ?: stringResource(R.string.label_address),
                totalBudget = activeProject?.totalBudget ?: 0.0,
                allocatedAmount = totalAllocatedValue,
                totalDurationDays = masterDuration,
                allocatedDays = allocatedDays,
                startDate = activeProject?.startDate ?: System.currentTimeMillis(),
                endDate = activeProject?.endDate ?: System.currentTimeMillis()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screens.ConstructionStageScreen.route) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = null,
                        tint = tealColor
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            stringResource(R.string.label_stages_title),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.label_stages_subtitle),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}