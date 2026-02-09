package com.therishideveloper.dreamhouse.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val date: Long,
    val type: String
)