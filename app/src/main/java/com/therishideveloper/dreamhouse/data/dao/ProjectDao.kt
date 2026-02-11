package com.therishideveloper.dreamhouse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.therishideveloper.dreamhouse.data.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProject(project: ProjectEntity): Long

    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: Int): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects LIMIT 1")
    fun getActiveProject(): Flow<ProjectEntity?>
}