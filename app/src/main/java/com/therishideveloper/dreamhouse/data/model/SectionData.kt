package com.therishideveloper.dreamhouse.data.model

data class SectionData(
    val sectionKey: String,
    val sectionTotal: Double,
    val categories: List<CategorySum>
)