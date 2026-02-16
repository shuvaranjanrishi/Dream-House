package com.therishideveloper.dreamhouse.screen

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.therishideveloper.dreamhouse.component.EmptyState
import com.therishideveloper.dreamhouse.component.EstimationBottomBar
import com.therishideveloper.dreamhouse.component.EstimationContent
import com.therishideveloper.dreamhouse.component.EstimationTopBar
import com.therishideveloper.dreamhouse.component.PolicyDialog
import com.therishideveloper.dreamhouse.util.PdfGenerator
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel

@Composable
fun EstimationScreen(
    onBack: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    viewModel: ProjectViewModel
) {
    val project by viewModel.activeProject.collectAsState()
    val history by viewModel.estimationHistory.collectAsState()
    val lastRecord = history.firstOrNull()
    var showPolicy by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pdfHelper = remember { PdfGenerator(context) }

    if (showPolicy) {
        PolicyDialog(onDismiss = { showPolicy = false })
    }

    Scaffold(
        topBar = {
            EstimationTopBar(
                onBack = onBack,
                onShowPolicy = { showPolicy = true },
                onDownload = {
                    lastRecord?.let { record ->
                        pdfHelper.generateEstimationPdf(
                            lastRecord,
                            project?.projectName ?: "",
                            project?.address ?: ""
                        )
                    }
                }
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
