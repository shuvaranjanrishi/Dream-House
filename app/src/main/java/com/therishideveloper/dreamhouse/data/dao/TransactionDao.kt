package com.therishideveloper.dreamhouse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.data.model.CategorySum
import com.therishideveloper.dreamhouse.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM tbl_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM tbl_transactions WHERE transactionType = :type ORDER BY date DESC")
    fun getAllIncome(type: String = TransactionType.INCOME.dbKey): Flow<List<Transaction>>

    @Query("SELECT * FROM tbl_transactions WHERE transactionType = :type ORDER BY date DESC")
    fun getAllExpense(type: String = TransactionType.EXPENSE.dbKey): Flow<List<Transaction>>

    @Query("SELECT * FROM tbl_transactions WHERE date BETWEEN :startTime AND :endTime ORDER BY date DESC")
    fun getTransactionsByRange(startTime: Long, endTime: Long): Flow<List<Transaction>>

    @Query(
        """
        SELECT 
        COALESCE(SUM(CASE WHEN transactionType = :incomeType THEN amount ELSE 0 END), 0.0) - 
        COALESCE(SUM(CASE WHEN transactionType = :expenseType THEN amount ELSE 0 END), 0.0) 
        FROM tbl_transactions
        """
    )
    fun getCurrentBalance(incomeType: String, expenseType: String): Flow<Double?>

    @Query("SELECT category, SUM(amount) as totalAmount FROM tbl_transactions WHERE transactionType = :type GROUP BY category")
    fun getCategoryWiseSum(type: String): Flow<List<CategorySum>>

    @Query(
        """
        SELECT category, SUM(amount) as totalAmount 
        FROM tbl_transactions 
        WHERE transactionType = :type AND date BETWEEN :startTime AND :endTime 
        GROUP BY category
        """
    )
    fun getCategoryWiseSumByRange(
        type: String,
        startTime: Long,
        endTime: Long
    ): Flow<List<CategorySum>>

    @Query("SELECT * FROM tbl_transactions")
    suspend fun getAllTransactionsList(): List<Transaction>

    @Query("DELETE FROM tbl_transactions")
    suspend fun deleteAllTransactions()

}
