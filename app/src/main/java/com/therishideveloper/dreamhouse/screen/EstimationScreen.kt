package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val project by viewModel.activeProject.collectAsStateWithLifecycle()
    val lastRecord by viewModel.estimationHistory.collectAsStateWithLifecycle()
    val isEmpty = lastRecord.totalEstimatedCost == "0"

    var showPolicy by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pdfHelper = remember { PdfGenerator(context) }

    if(showPolicy){
        PolicyDialog(onDismiss = { showPolicy = false })
    }

    Scaffold(
        topBar = {
            EstimationTopBar(
                onBack = onBack,
                onShowPolicy = { showPolicy = true },
                onDownload = {
                    if (!isEmpty) {
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
            EstimationBottomBar(totalCost = lastRecord.totalEstimatedCost)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            EstimationContent(
                padding = padding,
                record = lastRecord,
                onNewCalculation = onNavigateToCalculator,
                isEmpty = isEmpty
            )
        }
    }
}

