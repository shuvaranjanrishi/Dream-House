package com.therishideveloper.dreamhouse.viewmodel

import androidx.lifecycle.viewModelScope
import com.therishideveloper.dreamhouse.domain.repository.NoteRepository
import com.therishideveloper.dreamhouse.data.entity.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    val notes = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(
                note
            )
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}