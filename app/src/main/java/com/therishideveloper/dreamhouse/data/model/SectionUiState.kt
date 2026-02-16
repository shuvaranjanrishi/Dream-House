package com.therishideveloper.dreamhouse.data.model

data class SectionUiState(
    val sectionData: List<SectionData> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = true
)