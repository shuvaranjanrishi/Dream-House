package com.therishideveloper.dreamhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therishideveloper.dreamhouse.data.dao.TransactionDao
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.data.model.CategorySum
import com.therishideveloper.dreamhouse.data.model.SectionData
import com.therishideveloper.dreamhouse.data.model.SectionUiState
import com.therishideveloper.dreamhouse.data.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SectionChartViewModel @Inject constructor(
    private val dao: TransactionDao
) : ViewModel() {

    // Income এবং Expense এর জন্য আলাদা স্টেট
    private val _expenseState = MutableStateFlow(SectionUiState())
    val expenseState = _expenseState.asStateFlow()

    private val _incomeState = MutableStateFlow(SectionUiState())
    val incomeState = _incomeState.asStateFlow()

    init {
        loadReportData()
    }

    private fun loadReportData() {
        // Expense ডাটা ফেচিং
        viewModelScope.launch {
            dao.getCategoryWiseSum(TransactionType.EXPENSE.dbKey).collect { categorySums ->
                _expenseState.value = processCategorySums(categorySums)
            }
        }

        // Income ডাটা ফেচিং
        viewModelScope.launch {
            dao.getCategoryWiseSum(TransactionType.INCOME.dbKey).collect { categorySums ->
                _incomeState.value = processCategorySums(categorySums)
            }
        }
    }

    private fun processCategorySums(sums: List<CategorySum>): SectionUiState {
        if (sums.isEmpty()) return SectionUiState(isLoading = false)

        val totalAmount = sums.sumOf { it.totalAmount }

        // ক্যাটাগরিগুলোকে তাদের সেকশন অনুযায়ী গ্রুপ করা
        val groupedBySection = sums.groupBy { sum ->
            Category.fromDbKey(sum.category).section
        }.map { (section, categories) ->
            SectionData(
                sectionKey = section,
                sectionTotal = categories.sumOf { it.totalAmount },
                // ক্যাটাগরিগুলোকেও হাই টু লো সর্ট করা
                categories = categories.sortedByDescending { it.totalAmount }
            )
        }.sortedByDescending { it.sectionTotal } // সেকশনগুলোকেও সর্ট করা

        return SectionUiState(
            sectionData = groupedBySection,
            totalAmount = totalAmount,
            isLoading = false
        )
    }
}