package com.therishideveloper.dreamhouse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStage(stage: StageEntity)

    @Query("SELECT * FROM stages WHERE projectId = :projectId ORDER BY startDate ASC")
    fun getStagesForProject(projectId: Int): Flow<List<StageEntity>>

    @Query("SELECT SUM(estimatedCost) FROM stages WHERE projectId = :projectId")
    fun getTotalAllocatedBudget(projectId: Int): Flow<Double?>

    @Delete
    suspend fun deleteStage(stage: StageEntity)
}