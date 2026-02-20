package com.therishideveloper.dreamhouse.repository

import com.therishideveloper.dreamhouse.data.dao.EstimationDao
import com.therishideveloper.dreamhouse.data.dao.ProjectDao
import com.therishideveloper.dreamhouse.data.dao.StageDao
import com.therishideveloper.dreamhouse.data.entity.EstimationRecord
import com.therishideveloper.dreamhouse.data.entity.ProjectEntity
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao,
    private val stageDao: StageDao,
    private val estimationDao: EstimationDao
) : ProjectRepository {

    override suspend fun insertOrUpdateProject(project: ProjectEntity): Long =
        projectDao.insertOrUpdateProject(project)

    override fun getProjectById(projectId: Int): Flow<ProjectEntity?> =
        projectDao.getProjectById(projectId)

    override suspend fun insertOrUpdateStage(stage: StageEntity) =
        stageDao.insertStage(stage)

    override fun getStagesForProject(projectId: Int): Flow<List<StageEntity>> =
        stageDao.getStagesForProject(projectId)

    override fun getTotalAllocatedBudget(projectId: Int): Flow<Double?> =
        stageDao.getTotalAllocatedBudget(projectId)

    override suspend fun deleteStage(stage: StageEntity) =
        stageDao.deleteStage(stage)

    override fun getStageById(stageId: Int): Flow<StageEntity?> = stageDao.getStageById(stageId)

    override fun getAllEstimations(): Flow<List<EstimationRecord>> =
        estimationDao.getAllEstimations()

    override suspend fun saveEstimation(record: EstimationRecord) {
        estimationDao.insertOrUpdateEstimation(record)
    }

    override suspend fun getEstimationById(id: Int): EstimationRecord? =
        estimationDao.getEstimationById(id)

}