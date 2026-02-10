package com.therishideveloper.dreamhouse.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.data.model.AppBackupData
import com.therishideveloper.dreamhouse.data.model.CategorySum
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.data.model.UiEvent
import com.therishideveloper.dreamhouse.domain.repository.NoteRepository
import com.therishideveloper.dreamhouse.domain.repository.TransactionRepository
import com.therishideveloper.dreamhouse.util.BackupHelper
import com.therishideveloper.dreamhouse.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val noteRepo: NoteRepository,
    private val backupHelper: BackupHelper
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val allIncomeTransactions: StateFlow<List<Transaction>> = transactionRepo.getAllIncome()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allExpenseTransactions: StateFlow<List<Transaction>> = transactionRepo.getAllExpense()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allTransactions = transactionRepo.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- ১. বর্তমান ব্যালেন্স (প্যারামিটার ছাড়াই কল হবে, কারণ রিপোজিটরি ইন্টারনালি Enum ব্যবহার করছে) ---
    val currentBalance: StateFlow<Double?> = transactionRepo.getCurrentBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // --- ২. ক্যাটাগরি ভিত্তিক ইনকাম সামারি (TransactionType.INCOME ব্যবহার করে) ---
    val incomeCategorySums: StateFlow<List<CategorySum>> = transactionRepo.getCategoryWiseSum(
        TransactionType.INCOME
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- ৩. ক্যাটাগরি ভিত্তিক খরচ সামারি (TransactionType.EXPENSE ব্যবহার করে) ---
    val expenseCategorySums: StateFlow<List<CategorySum>> =
        transactionRepo.getCategoryWiseSum(TransactionType.EXPENSE)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addTransaction(context: Context, transaction: Transaction) {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.Loading)
            try {
                transactionRepo.insert(transaction)
                _eventFlow.emit(UiEvent.Success(context.getString(R.string.msg_added)))
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.Error(context.getString(R.string.msg_error)))
            }
        }
    }

    fun updateTransaction(context: Context, transaction: Transaction) {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.Loading)
            try {
                transactionRepo.update(transaction)
                _eventFlow.emit(UiEvent.Success(context.getString(R.string.msg_updated)))
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.Error(context.getString(R.string.msg_error)))
            }
        }
    }

    fun deleteTransaction(context: Context, transaction: Transaction) {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.Loading)
            try {
                transactionRepo.delete(transaction)
                _eventFlow.emit(UiEvent.Success(context.getString(R.string.msg_deleted)))
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.Error(context.getString(R.string.msg_error)))
            }
        }
    }

    fun getTodayData(dateMillis: Long): Flow<List<Transaction>> {
        val range = DateUtils.getDayRange(dateMillis)
        return transactionRepo.getTransactionsByRange(range.first, range.second)
    }

    fun getMonthlyData(dateMillis: Long): Flow<List<Transaction>> {
        val range = DateUtils.getMonthRange(dateMillis)
        return transactionRepo.getTransactionsByRange(range.first, range.second)
    }

    fun getYearlyData(dateMillis: Long): Flow<List<Transaction>> {
        val range = DateUtils.getYearRange(dateMillis)
        return transactionRepo.getTransactionsByRange(range.first, range.second)
    }

    fun exportBackup(onResult: (Uri?) -> Unit) {
        viewModelScope.launch {
            try {
                val transactions = transactionRepo.getAllTransactionsList()
                val notes = noteRepo.getAllNotesList()
                val backupData = AppBackupData(transactions, notes)
                val uri = backupHelper.createBackup(backupData)
                onResult(uri)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            try {
                val backupData = backupHelper.restoreBackup(uri)
                if (backupData != null) {
                    transactionRepo.deleteAllTransactions()
                    noteRepo.deleteAllNotes()
                    backupData.transactions.forEach { transactionRepo.insert(it) }
                    backupData.notes.forEach { noteRepo.insertNote(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}