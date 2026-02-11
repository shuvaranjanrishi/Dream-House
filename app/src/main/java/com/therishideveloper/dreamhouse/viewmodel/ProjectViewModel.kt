package com.therishideveloper.dreamhouse.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.therishideveloper.dreamhouse.domain.repository.NoteRepository
import com.therishideveloper.dreamhouse.data.entity.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import com.therishideveloper.dreamhouse.data.entity.ProjectEntity
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import com.therishideveloper.dreamhouse.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    // ProjectViewModel.kt
    var showWelcomeCelebration by mutableStateOf(false)

    fun triggerWelcome() {
        showWelcomeCelebration = true
    }

    fun welcomeShown() {
        showWelcomeCelebration = false
    }

    val activeProject: StateFlow<ProjectEntity?> = repository.getProjectById(1)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            activeProject.collect {
                _isLoading.value = false
            }
        }
    }

    fun saveProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateProject(project)
        }
    }

    fun addStage(projectId: Int, name: String, cost: Double, start: Long, end: Long) {
        viewModelScope.launch {
            val newStage = StageEntity(
                projectId = projectId,
                stageName = name,
                estimatedCost = cost,
                startDate = start,
                endDate = end,
                status = "PENDING"
            )
            repository.insertStage(newStage)
        }
    }

    fun getStages(projectId: Int) = repository.getStagesForProject(projectId)

    fun getTotalAllocated(projectId: Int): Flow<Double> {
        return repository.getTotalAllocatedBudget(projectId).map { it ?: 0.0 }
    }
}