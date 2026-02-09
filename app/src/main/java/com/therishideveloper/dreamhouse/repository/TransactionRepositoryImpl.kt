package com.therishideveloper.dreamhouse.domain.repository

import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.data.dao.TransactionDao
import com.therishideveloper.dreamhouse.data.model.CategorySum
import com.therishideveloper.dreamhouse.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun insert(transaction: Transaction) {
        dao.insertTransaction(transaction)
    }

    override suspend fun update(transaction: Transaction) {
        dao.updateTransaction(transaction)
    }

    override suspend fun delete(transaction: Transaction) {
        dao.delete(transaction)
    }

    override fun getAllTransactions(): Flow<List<Transaction>> =
        dao.getAllTransactions()

    override fun getAllIncome(): Flow<List<Transaction>> =
        dao.getAllIncome()

    override fun getAllExpense(): Flow<List<Transaction>> =
        dao.getAllExpense()

    // ১. রেঞ্জ কুয়েরি ইমপ্লিমেন্টেশন (এটি দিয়েই আজ, মাস এবং বছর ফিল্টার হবে)
    override fun getTransactionsByRange(startTime: Long, endTime: Long): Flow<List<Transaction>> =
        dao.getTransactionsByRange(startTime, endTime)

    // --- ১. বর্তমান ব্যালেন্স (প্যারামিটার সহ DAO কল) ---
    override fun getCurrentBalance(): Flow<Double?> =
        dao.getCurrentBalance(
            incomeType = TransactionType.INCOME.dbKey,
            expenseType = TransactionType.EXPENSE.dbKey
        )

    // --- ২. ক্যাটাগরি ভিত্তিক যোগফল ---
    override fun getCategoryWiseSum(type: TransactionType): Flow<List<CategorySum>> =
        dao.getCategoryWiseSum(type.dbKey)

    // --- ৩. নির্দিষ্ট সময়ের মধ্যে ক্যাটাগরি ভিত্তিক যোগফল ---
    override fun getCategoryWiseSumByRange(
        type: TransactionType,
        startTime: Long,
        endTime: Long
    ): Flow<List<CategorySum>> =
        dao.getCategoryWiseSumByRange(
            type = type.dbKey,
            startTime = startTime,
            endTime = endTime
        )

    override suspend fun getAllTransactionsList(): List<Transaction> {
        return dao.getAllTransactionsList()
    }

    override suspend fun deleteAllTransactions() {
        dao.deleteAllTransactions()
    }
}
