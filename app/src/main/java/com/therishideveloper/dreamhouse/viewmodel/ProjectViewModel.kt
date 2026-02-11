package com.therishideveloper.dreamhouse.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
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

    fun insertOrUpdateStage(stage: StageEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateStage(stage)
        }
    }

    fun getStages(projectId: Int) = repository.getStagesForProject(projectId)

    fun getTotalAllocated(projectId: Int): Flow<Double> {
        return repository.getTotalAllocatedBudget(projectId).map { it ?: 0.0 }
    }

    // শুধুমাত্র স্ট্যাটাস পরিবর্তন করার জন্য (পেন্ডিং/কমপ্লিট)
    fun updateStageStatus(stageId: Int, newStatus: String) {
        viewModelScope.launch {
            // আপনি চাইলে রিপোজিটরিতে আলাদা updateStatus ফাংশন লিখে নিতে পারেন
            // অথবা পুরনো স্টেজটা নিয়ে এসে শুধু স্ট্যাটাস বদলে সেভ করতে পারেন
        }
    }

    // নির্দিষ্ট একটি স্টেজ আইডি দিয়ে খুঁজে বের করা (এডিট মোডের জন্য)
    fun getStageById(stageId: Int): Flow<StageEntity?> {
        return repository.getStageById(stageId) // রিপোজিটরিতে এই ফাংশনটি থাকতে হবে
    }
}