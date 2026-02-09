package com.therishideveloper.dreamhouse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.therishideveloper.dreamhouse.data.entity.Note
import com.therishideveloper.dreamhouse.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM tbl_notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM tbl_notes WHERE type = :noteType ORDER BY id DESC")
    fun getNotesByType(noteType: String): Flow<List<Note>>


    @Query("SELECT * FROM tbl_notes")
    suspend fun getAllNotesList(): List<Note>

    @Query("DELETE FROM tbl_notes")
    suspend fun deleteAllNotes()
}