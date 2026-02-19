package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.therishideveloper.dreamhouse.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dailyexpense.ui.theme.stageColors
import com.therishideveloper.dreamhouse.component.ProjectBudgetCard
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import com.therishideveloper.dreamhouse.data.model.StageStatus
import com.therishideveloper.dreamhouse.data.model.ConstructionStage
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
    val stages by viewModel.getStages(1).collectAsStateWithLifecycle(initialValue = emptyList())
    val totalAllocatedValue by viewModel.getTotalAllocated(1)
        .collectAsStateWithLifecycle(initialValue = 0.0)

    // Data Calculations
    val masterDuration = activeProject?.let {
        ((it.endDate - it.startDate) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    } ?: 0L

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
                        Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        activeProject?.id?.let { id ->
                            navController.navigate("project_setup_screen?projectId=$id")
                        }
                    }) {
                        Icon(Icons.Default.Edit, "Edit", tint = Color.White)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Budget Summary Card
            ProjectBudgetCard(
                projectName = activeProject?.projectName
                    ?: stringResource(R.string.label_default_project_name),
                address = activeProject?.address ?: stringResource(R.string.label_address),
                totalBudget = activeProject?.totalBudget ?: 0.0,
                allocatedAmount = totalAllocatedValue,
                totalDurationDays = masterDuration,
                allocatedDays = allocatedDays,
                startDate = activeProject?.startDate ?: System.currentTimeMillis(),
                endDate = activeProject?.endDate ?: System.currentTimeMillis()
            )

            // 2. Construction Stages Card with Progress Bar
            ActionCardWithProgress(
                title = stringResource(R.string.label_stages_title),
                subtitle = stringResource(R.string.label_stages_subtitle),
                icon = Icons.Default.Architecture,
                stages = stages,
                onClick = { navController.navigate(Screens.ConstructionStageScreen.route) }
            )

            // 3. Material Estimation Card
            SimpleActionCard(
                title = stringResource(R.string.label_estimation_title),
                subtitle = stringResource(R.string.label_estimation_subtitle),
                icon = Icons.Default.Calculate,
                onClick = { navController.navigate(Screens.EstimationScreen.route) }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ActionCardWithProgress(
    title: String,
    subtitle: String,
    icon: ImageVector,
    stages: List<StageEntity>,
    onClick: () -> Unit
) {
    val completedCount = stages.count { it.status == StageStatus.COMPLETED.dbKey }
    val progressPercentage = if (stages.isNotEmpty()) (completedCount / 8f * 100).toInt() else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Main Info Row
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tealColor.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = tealColor
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.LightGray
                )
            }

            // Progress Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tealColor.copy(alpha = 0.03f)) // হালকা টিল ব্যাকগ্রাউন্ড যা দেখতে প্রিমিয়াম
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_project_progress),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Multi-color Segmented Progress Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    repeat(8) { index ->
                        val stage = stages.getOrNull(index)
                        val isCompleted = stage?.status == StageStatus.COMPLETED.dbKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(horizontal = 1.dp)
                                .background(
                                    if (isCompleted) stageColors[index] else Color(
                                        0xFFE0E0E0
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            R.string.label_progress_percentage,
                            progressPercentage
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = tealColor
                    )
                    Text(
                        text = "$completedCount / 8",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = tealColor.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = tealColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.LightGray
            )
        }
    }
}