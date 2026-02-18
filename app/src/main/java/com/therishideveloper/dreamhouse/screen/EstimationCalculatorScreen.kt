package com.therishideveloper.dreamhouse.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.CalculateButton
import com.therishideveloper.dreamhouse.component.CalculatorTextField
import com.therishideveloper.dreamhouse.component.CalculatorTopBar
import com.therishideveloper.dreamhouse.component.EstimationDetailsDialog
import com.therishideveloper.dreamhouse.component.InputRow
import com.therishideveloper.dreamhouse.component.SectionTitle
import com.therishideveloper.dreamhouse.component.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimationCalculatorScreen(
    onBack: () -> Unit,
    viewModel: ProjectViewModel
) {
    val context = LocalContext.current

    // ইনপুট স্টেটসমূহ
    var areaInput by remember { mutableStateOf("") }
    var rodUnitPrice by remember { mutableStateOf("") }
    var cementUnitPrice by remember { mutableStateOf("") }
    var sandUnitPrice by remember { mutableStateOf("") }
    var brickUnitPrice by remember { mutableStateOf("") }
    var stoneUnitPrice by remember { mutableStateOf("") }
    var laborRate by remember { mutableStateOf("") }
    var foundationFloors by remember { mutableStateOf("1") }
    var floorsToBuild by remember { mutableStateOf("1") }

    var isLoading by remember { mutableStateOf(false) }
    val calculationResult by viewModel.currentCalculation.collectAsState()

    // ভ্যালিডেশন লজিক
    fun validateAndCalculate() {
        if (areaInput.isEmpty() || rodUnitPrice.isEmpty() || cementUnitPrice.isEmpty() ||
            sandUnitPrice.isEmpty() || brickUnitPrice.isEmpty() || stoneUnitPrice.isEmpty() || laborRate.isEmpty()
        ) {
            android.widget.Toast.makeText(
                context,
                context.getString(R.string.error_empty_fields),
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }

        val area = areaInput.toDoubleOrNull() ?: 0.0
        if (area <= 0) {
            android.widget.Toast.makeText(
                context,
                context.getString(R.string.error_area_zero),
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            isLoading = true
            viewModel.performCalculation(
                area = area,
                floorsToBuild = floorsToBuild.toIntOrNull() ?: 1,
                foundationFloors = foundationFloors.toIntOrNull() ?: 1,
                rodRate = rodUnitPrice.toDoubleOrNull() ?: 0.0,
                cementRate = cementUnitPrice.toDoubleOrNull() ?: 0.0,
                sandRate = sandUnitPrice.toDoubleOrNull() ?: 0.0,
                brickRate = brickUnitPrice.toDoubleOrNull() ?: 0.0,
                stoneRate = stoneUnitPrice.toDoubleOrNull() ?: 0.0,
                laborRate = laborRate.toDoubleOrNull() ?: 0.0
            )
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            showToast(context, context.getString(R.string.error_invalid_input))
        }
    }

    Scaffold(
        topBar = {
            CalculatorTopBar(
                title = stringResource(R.string.title_estimation_calculator),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle(stringResource(R.string.label_dimensions))

            // ফ্লোর ইনপুট রো
            InputRow {
                CalculatorTextField(
                    value = foundationFloors,
                    onValueChange = { if (it.all { c -> c.isDigit() }) foundationFloors = it },
                    label = stringResource(R.string.label_foundation),
                    modifier = Modifier.weight(1f)
                )
                CalculatorTextField(
                    value = floorsToBuild,
                    onValueChange = { if (it.all { c -> c.isDigit() }) floorsToBuild = it },
                    label = stringResource(R.string.label_build_floor),
                    modifier = Modifier.weight(1f)
                )
            }

            CalculatorTextField(
                value = areaInput,
                onValueChange = { areaInput = it },
                label = stringResource(R.string.label_total_area)
            )

            SectionTitle(stringResource(R.string.label_material_rates))

            // ম্যাটেরিয়াল রেট ইনপুটগুলো
            InputRow {
                CalculatorTextField(
                    value = rodUnitPrice,
                    onValueChange = { rodUnitPrice = it },
                    label = "(${stringResource(Category.ROD.titleRes)})/${stringResource(Category.ROD.unitRes)}",
                    modifier = Modifier.weight(1f)
                )
                CalculatorTextField(
                    value = cementUnitPrice,
                    onValueChange = { cementUnitPrice = it },
                    label = "${stringResource(Category.CEMENT.titleRes)}/${stringResource(Category.CEMENT.unitRes)}",
                    modifier = Modifier.weight(1f)
                )
            }

            InputRow {
                CalculatorTextField(
                    value = sandUnitPrice,
                    onValueChange = { sandUnitPrice = it },
                    label = "${stringResource(Category.SAND.titleRes)}/${stringResource(Category.SAND.unitRes)}",
                    modifier = Modifier.weight(1f)
                )
                CalculatorTextField(
                    value = brickUnitPrice,
                    onValueChange = { brickUnitPrice = it },
                    label = "(${stringResource(Category.BRICKS.titleRes)})/${stringResource(Category.BRICKS.unitRes)}",
                    modifier = Modifier.weight(1f)
                )
            }

            InputRow {
                CalculatorTextField(
                    value = stoneUnitPrice,
                    onValueChange = { stoneUnitPrice = it },
                    label = "(${stringResource(Category.STONE.titleRes)})/${stringResource(Category.STONE.unitRes)}",
                    modifier = Modifier.weight(1f)
                )
                CalculatorTextField(
                    value = laborRate,
                    onValueChange = { laborRate = it },
                    label = "${stringResource(Category.MASON_LABOR.titleRes)}/${
                        stringResource(Category.MASON_LABOR.unitRes)
                    }",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ক্যালকুলেট বাটন
            CalculateButton(isLoading = isLoading, onClick = { validateAndCalculate() })

            // রেজাল্ট ডায়ালগ
            calculationResult?.let { result ->
                EstimationDetailsDialog(
                    record = result,
                    onDismiss = { viewModel.clearCalculation() },
                    onSave = {
                        viewModel.saveCurrentEstimation()
                        onBack()
                    }
                )
            }
        }
    }
}

