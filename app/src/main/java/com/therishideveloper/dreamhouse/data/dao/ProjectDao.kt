package com.therishideveloper.dreamhouse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.therishideveloper.dreamhouse.data.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    // এটি একটি কাস্টম ট্রানজ্যাকশন যা স্মার্টলি হ্যান্ডেল করবে
    @Transaction
    suspend fun insertOrUpdateProject(project: ProjectEntity): Long {
        val id = insertProject(project)
        if (id == -1L) {
            updateProject(project)
            return project.id.toLong()
        }
        return id
    }
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrUpdateProject(project: ProjectEntity): Long

    @Query("SELECT * FROM tbl_projects WHERE id = :projectId")
    fun getProjectById(projectId: Int): Flow<ProjectEntity?>

    @Query("SELECT * FROM tbl_projects LIMIT 1")
    fun getActiveProject(): Flow<ProjectEntity?>
}