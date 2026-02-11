package com.therishideveloper.dreamhouse.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.therishideveloper.dreamhouse.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.therishideveloper.dreamhouse.component.CalculatorDialog
import com.therishideveloper.dreamhouse.component.CalculatorFab
import com.therishideveloper.dreamhouse.component.StageCard
import com.therishideveloper.dreamhouse.component.formatDuration
import com.therishideveloper.dreamhouse.navigation.Screens
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.NumberUtils
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel
import kotlin.math.acos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConstructionStageScreen(
    onBack: () -> Unit,
    navController: NavController,
    viewModel: ProjectViewModel
) {
    val context = LocalContext.current
    val stages by viewModel.getStages(1).collectAsStateWithLifecycle(initialValue = emptyList())
    val totalAllocated by viewModel.getTotalAllocated(1)
        .collectAsStateWithLifecycle(initialValue = 0.0)

    val totalTime =
        stages.sumOf { ((it.endDate - it.startDate) / (1000 * 60 * 60 * 24)).coerceAtLeast(0) }

    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.label_stages), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screens.AddStageScreen.route)
                    }) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            tint = Color.White
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor)
            )
        },

        floatingActionButton = {
            CalculatorFab(
                lazyListState = lazyListState,
                isListEmpty = stages.isEmpty()
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // --- Summary Header Section (With Vertical Divider) ---
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shadowElevation = 1.dp
            ) {
                IntrinsicSize.Min.let { height ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(R.string.total_allocated_budget),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = NumberUtils.formatAmountByLocale(
                                    context,
                                    totalAllocated.toString()
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(R.string.total_duration),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = formatDuration(totalTime),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            // --- Stages List ---
            if (stages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_stages_added), color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    itemsIndexed(
                        items = stages.sortedBy { it.startDate },
                        key = { _, stage -> stage.id }
                    ) { index, stage ->
                        StageCard(
                            index = index + 1,
                            stage = stage,
                            onClick = {
                                navController.navigate("add_stage_screen?stageId=${stage.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}


