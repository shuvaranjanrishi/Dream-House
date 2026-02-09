package com.therishideveloper.dreamhouse.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel

@Composable
fun TransactionEntryBase(
    transactionId: Int?,
    transactionType: TransactionType,
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    val transactionToEdit = remember(transactionId, allTransactions) {
        allTransactions.find { it.id == transactionId }
    }

    val screenTitle = if (transactionId != null) {
        if (transactionType == TransactionType.INCOME) {
            stringResource(R.string.label_edit_income)
        } else {
            stringResource(R.string.label_edit_expense)
        }
    } else {
        if (transactionType == TransactionType.INCOME) {
            stringResource(R.string.label_add_income)
        } else {
            stringResource(R.string.label_add_expense)
        }
    }

    val buttonLabel = if (transactionId != null) {
        if (transactionType == TransactionType.INCOME) {
            stringResource(R.string.btn_update_income)
        } else {
            stringResource(R.string.btn_update_expense)
        }
    } else {
        if (transactionType == TransactionType.INCOME) {
            stringResource(R.string.btn_save_income)
        } else {
            stringResource(R.string.btn_save_expense)
        }
    }

    AddEditForm(
        title = screenTitle,
        buttonText = buttonLabel,
        transactionType = transactionType,
        transactionId = transactionId,
        initialCategory = transactionToEdit?.category ?: "",
        initialDescription = transactionToEdit?.description ?: "",
        initialAmount = transactionToEdit?.amount?.toString() ?: "",
        initialDate = transactionToEdit?.date ?: System.currentTimeMillis(),
        onSave = { category, description, amount, date ->
            val transaction = Transaction(
                id = transactionId ?: 0,
                category = category,
                description = description,
                amount = amount.toDoubleOrNull() ?: 0.0,
                date = date,
                transactionType = transactionType.dbKey
            )

            if (transactionId == null) {
                viewModel.addTransaction(context,transaction)
            } else {
                viewModel.updateTransaction(context,transaction)
            }
        },
        onDelete = {
            transactionToEdit?.let { transaction ->
                viewModel.deleteTransaction(context,transaction)
            }
        },
        onBack = onBack,
        currentBalance = currentBalance.toString()
    )
}