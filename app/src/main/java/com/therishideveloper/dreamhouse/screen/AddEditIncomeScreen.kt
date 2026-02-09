package com.therishideveloper.dreamhouse.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.LoadingDialog
import com.therishideveloper.dreamhouse.component.TransactionEntryBase
import com.therishideveloper.dreamhouse.component.showToast
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.data.model.UiEvent
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditIncomeScreen(
    transactionId: Int? = null,
    onBack: () -> Unit,
    viewModel: TransactionViewModel
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Loading -> isLoading = true
                is UiEvent.Success -> {
                    isLoading = false
                    showToast(context, event.successMsg)
                    onBack()
                }

                is UiEvent.Error -> {
                    isLoading = false
                    showToast(context, event.errorMsg)
                }
            }
        }
    }

    if (isLoading) {
        LoadingDialog(message = stringResource(R.string.label_saving_data))
    }

    TransactionEntryBase(
        transactionId = transactionId,
        transactionType = TransactionType.INCOME,
        viewModel = viewModel,
        onBack = onBack
    )
}
