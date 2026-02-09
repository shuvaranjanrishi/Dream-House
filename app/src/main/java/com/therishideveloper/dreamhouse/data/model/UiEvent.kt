package com.therishideveloper.dreamhouse.data.model

sealed class UiEvent {
    object Loading : UiEvent()
    data class Success(val successMsg: String) : UiEvent()
    data class Error(val errorMsg: String) : UiEvent()
}