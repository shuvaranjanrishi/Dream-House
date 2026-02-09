package com.therishideveloper.dreamhouse.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.therishideveloper.dreamhouse.data.dao.NoteDao
import com.therishideveloper.dreamhouse.data.dao.TransactionDao
import com.therishideveloper.dreamhouse.data.entity.Note
import com.therishideveloper.dreamhouse.data.entity.Transaction

@Database(
    entities = [Transaction::class, Note::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun noteDao(): NoteDao
}