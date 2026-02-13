package com.therishideveloper.dreamhouse.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.therishideveloper.dreamhouse.data.dao.EstimationDao
import com.therishideveloper.dreamhouse.data.dao.NoteDao
import com.therishideveloper.dreamhouse.data.dao.ProjectDao
import com.therishideveloper.dreamhouse.data.dao.StageDao
import com.therishideveloper.dreamhouse.data.dao.TransactionDao
import com.therishideveloper.dreamhouse.data.entity.EstimationRecord
import com.therishideveloper.dreamhouse.data.entity.Note
import com.therishideveloper.dreamhouse.data.entity.ProjectEntity
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import com.therishideveloper.dreamhouse.data.entity.Transaction

@Database(
    entities = [
        Transaction::class,
        Note::class,
        ProjectEntity::class,
        StageEntity::class,
        EstimationRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun noteDao(): NoteDao
    abstract fun projectDao(): ProjectDao
    abstract fun stageDao(): StageDao
    abstract fun estimationDao(): EstimationDao
}