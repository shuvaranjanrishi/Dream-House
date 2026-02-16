package com.therishideveloper.dreamhouse.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.util.ExcelGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor() : ViewModel() {

    var isDownloading by mutableStateOf(false)
        private set

    var downloadedFile by mutableStateOf<File?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun downloadExcel(
        context: Context,
        transactions: List<Transaction>,
        onComplete: (File) -> Unit
    ) {
        resetState()
        if (transactions.isEmpty()) {
            errorMessage = "No transactions found to download!"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            isDownloading = true
            errorMessage = null

            val file = ExcelGenerator.generateTransactionExcel(context,transactions)

            withContext(Dispatchers.Main) {
                isDownloading = false
                if (file != null) {
                    downloadedFile = file
                    onComplete(file)
                } else {
                    errorMessage = "Failed to create Excel file..."
                }
            }
        }
    }

    fun shareExcel(transactions: List<Transaction>, context: Context) {
        resetState()
        viewModelScope.launch(Dispatchers.IO) {
            isDownloading = true
            val file = ExcelGenerator.generateTransactionExcel(context,transactions)
            withContext(Dispatchers.Main) {
                isDownloading = false
                if (file != null) {
                    ExcelGenerator.shareExcelFile(context, file)
                } else {
                    errorMessage = "Failed to create file for sharing..."
                }
            }
        }
    }

    fun resetState() {
        downloadedFile = null
        errorMessage = null
    }
}