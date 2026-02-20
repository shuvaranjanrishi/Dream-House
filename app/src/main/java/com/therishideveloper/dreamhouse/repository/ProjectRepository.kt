package com.therishideveloper.dreamhouse.repository

import com.therishideveloper.dreamhouse.data.entity.EstimationRecord
import com.therishideveloper.dreamhouse.data.entity.ProjectEntity
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    suspend fun insertOrUpdateProject(project: ProjectEntity): Long
    fun getProjectById(projectId: Int): Flow<ProjectEntity?>
    suspend fun insertOrUpdateStage(stage: StageEntity)
    fun getStagesForProject(projectId: Int): Flow<List<StageEntity>>
    fun getTotalAllocatedBudget(projectId: Int): Flow<Double?>
    suspend fun deleteStage(stage: StageEntity)
    fun getStageById(stageId: Int): Flow<StageEntity?>
    fun getAllEstimations(): Flow<List<EstimationRecord>>
    suspend fun saveEstimation(record: EstimationRecord)
    suspend fun getEstimationById(id: Int): EstimationRecord?
}