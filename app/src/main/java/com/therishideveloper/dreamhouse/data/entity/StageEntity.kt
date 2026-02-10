package com.therishideveloper.dreamhouse.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "stages",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val stageName: String,
    val estimatedCost: Double,
    val startDate: Long,
    val endDate: Long,
    val status: String = "PENDING" // PENDING, IN_PROGRESS, COMPLETED
)