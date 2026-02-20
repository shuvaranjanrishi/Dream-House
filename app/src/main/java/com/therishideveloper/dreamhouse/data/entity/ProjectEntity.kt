package com.therishideveloper.dreamhouse.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectName: String,
    val address: String,
    val totalBudget: Double,
    val startDate: Long,
    val endDate: Long,
    val createdAt: Long = System.currentTimeMillis()
)