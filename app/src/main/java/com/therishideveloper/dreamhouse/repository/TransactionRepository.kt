package com.therishideveloper.dreamhouse.domain.repository

import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.data.model.CategorySum
import com.therishideveloper.dreamhouse.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun insert(transaction: Transaction)
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)

    fun getAllTransactions(): Flow<List<Transaction>>
    fun getAllIncome(): Flow<List<Transaction>>
    fun getAllExpense(): Flow<List<Transaction>>

    fun getTransactionsByRange(startTime: Long, endTime: Long): Flow<List<Transaction>>

    fun getCurrentBalance(): Flow<Double?>

    fun getCategoryWiseSum(type: TransactionType): Flow<List<CategorySum>>

    fun getCategoryWiseSumByRange(
        type: TransactionType,
        startTime: Long,
        endTime: Long
    ): Flow<List<CategorySum>>

    suspend fun getAllTransactionsList(): List<Transaction>
    suspend fun deleteAllTransactions()
}
