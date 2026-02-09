package com.therishideveloper.dreamhouse.domain.repository

import com.therishideveloper.dreamhouse.data.entity.Note
import com.therishideveloper.dreamhouse.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun getAllNotesList(): List<Note>
    suspend fun deleteAllNotes()

}