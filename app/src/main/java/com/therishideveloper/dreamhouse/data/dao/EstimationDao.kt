package com.therishideveloper.dreamhouse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.therishideveloper.dreamhouse.data.entity.EstimationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateEstimation(record: EstimationRecord)

    @Query("SELECT * FROM tbl_estimation ORDER BY id DESC")
    fun getAllEstimations(): Flow<List<EstimationRecord>>

    @Query("SELECT * FROM tbl_estimation WHERE id = :id")
    suspend fun getEstimationById(id: Int): EstimationRecord?

    @Delete
    suspend fun deleteEstimation(record: EstimationRecord)
}