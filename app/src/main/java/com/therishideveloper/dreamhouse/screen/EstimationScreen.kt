package com.therishideveloper.dreamhouse.screen

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.therishideveloper.dreamhouse.component.EmptyState
import com.therishideveloper.dreamhouse.component.EstimationBottomBar
import com.therishideveloper.dreamhouse.component.EstimationContent
import com.therishideveloper.dreamhouse.component.EstimationTopBar
import com.therishideveloper.dreamhouse.component.PolicyDialog
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel

@Composable
fun EstimationScreen(
    onBack: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    viewModel: ProjectViewModel
) {
    val history by viewModel.estimationHistory.collectAsState()
    val lastRecord = history.firstOrNull()
    var showPolicy by remember { mutableStateOf(false) }

    if (showPolicy) {
        PolicyDialog(onDismiss = { showPolicy = false })
    }

    Scaffold(
        topBar = {
            EstimationTopBar(
                onBack = onBack,
                onShowPolicy = { showPolicy = true },
                onDownload = { /* Future PDF Logic */ }
            )
        },
        bottomBar = {
            lastRecord?.let {
                EstimationBottomBar(totalCost = it.totalEstimatedCost)
            }
        }
    ) { padding ->
        if (lastRecord == null) {
            EmptyState(padding)
        } else {
            EstimationContent(
                padding = padding,
                record = lastRecord,
                onNewCalculation = onNavigateToCalculator
            )
        }
    }
}
