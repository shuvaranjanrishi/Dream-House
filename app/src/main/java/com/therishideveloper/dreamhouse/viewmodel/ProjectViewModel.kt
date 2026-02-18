package com.therishideveloper.dreamhouse.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import com.therishideveloper.dreamhouse.data.entity.EstimationRecord
import com.therishideveloper.dreamhouse.data.entity.ProjectEntity
import com.therishideveloper.dreamhouse.data.entity.StageEntity
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    var showWelcomeCelebration by mutableStateOf(false)

    fun triggerWelcome() {
        showWelcomeCelebration = true
    }

    fun welcomeShown() {
        showWelcomeCelebration = false
    }

    val activeProject: StateFlow<ProjectEntity> = repository.getProjectById(1)
        .map { project ->
            project ?: ProjectEntity(
                id = 1,
                projectName = "Dream House",
                address = "Address not set",
                totalBudget = 0.0,
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProjectEntity(
                id = 1,
                projectName = "Loading...",
                address = "...",
                totalBudget = 0.0,
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis()
            )
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

    fun getStageById(stageId: Int): Flow<StageEntity?> {
        return repository.getStageById(stageId)
    }

    val estimationHistory: StateFlow<EstimationRecord> = repository.getAllEstimations()
        .map { list ->
            list.firstOrNull() ?: EstimationRecord(
                date = System.currentTimeMillis(),
                totalArea = "0",
                foundationFloors = 0,
                floorsToBuild = 0,
                rod = Category.ROD.dbKey,
                cement = Category.CEMENT.dbKey,
                sand = Category.SAND.dbKey,
                brick = Category.BRICKS.dbKey,
                stone = Category.STONE.dbKey,
                labor = Category.MASON_LABOR.dbKey,
                others = Category.OTHERS.dbKey,
                rodQty = "0", cementQty = "0", sandQty = "0",
                brickQty = "0", stoneQty = "0", laborQty = "0",
                bindingWireQty = "0", nailsQty = "0", polytheneQty = "0",
                othersQty = "0",
                rodCost = "0", cementCost = "0", sandCost = "0",
                brickCost = "0", stoneCost = "0", laborCost = "0",
                bindingWireCost = "0", nailsCost = "0", polytheneCost = "0",
                othersCost = "0",
                rodRate = "0", cementRate = "0", sandRate = "0",
                brickRate = "0", stoneRate = "0", laborRate = "0",
                bindingWireRate = "0", nailsRate = "0", polytheneRate = "0",
                othersRate = "0",
                totalEstimatedCost = "0"
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EstimationRecord( // Initial state while loading
                date = System.currentTimeMillis(),
                totalArea = "0",
                foundationFloors = 0,
                floorsToBuild = 0,
                rod = "",
                cement = "",
                sand = "",
                brick = "",
                stone = "",
                labor = "",
                others = "",
                rodQty = "0",
                cementQty = "0",
                sandQty = "0",
                brickQty = "0",
                stoneQty = "0",
                laborQty = "0",
                bindingWireQty = "0",
                nailsQty = "0",
                polytheneQty = "0",
                othersQty = "0",
                rodCost = "0",
                cementCost = "0",
                sandCost = "0",
                brickCost = "0",
                stoneCost = "0",
                laborCost = "0",
                bindingWireCost = "0",
                nailsCost = "0",
                polytheneCost = "0",
                othersCost = "0",
                rodRate = "0",
                cementRate = "0",
                sandRate = "0",
                brickRate = "0",
                stoneRate = "0",
                laborRate = "0",
                bindingWireRate = "0",
                nailsRate = "0",
                polytheneRate = "0",
                othersRate = "0",
                totalEstimatedCost = "0"
            )
        )

    private val _currentCalculation = MutableStateFlow<EstimationRecord?>(null)
    val currentCalculation = _currentCalculation.asStateFlow()

    fun performCalculation(
        area: Double,
        floorsToBuild: Int,
        foundationFloors: Int,
        rodRate: Double, cementRate: Double, sandRate: Double,
        brickRate: Double, stoneRate: Double, laborRate: Double
    ) {
        if (area <= 0 || floorsToBuild <= 0) return

        viewModelScope.launch(Dispatchers.Default) {
            //general probable rate
            val bindingWireRate = 140
            val nailsRate = 150
            val polytheneRate = 2.50
            val safetyTankRate = 15.0
            val excavationRate = 10.0

            // --- ১. মাটি খনন ও সেফটি ট্যাংক (Septic Tank & Excavation) ---
            val safetyTankCost = 60000.0 + (area * safetyTankRate)
            val excavationCost = area * excavationRate * foundationFloors

            // --- ২. ফাউন্ডেশন ফ্যাক্টর ---
            val foundationFactor = 1.0 + (foundationFloors * 0.18)

            // মাটির নিচের ম্যাটেরিয়াল
            val fRod = area * 4.0 * foundationFactor
            val fCement = area * 0.3 * foundationFactor
            val fSand = area * 0.7 * foundationFactor
            val fStone = area * 1.2 * foundationFactor

            // --- ৩. সুপার-স্ট্রাকচার (প্রতি তলার জন্য) ---
            val sRodPerFloor = area * 3.2
            val sCementPerFloor = area * 0.42
            val sSandPerFloor = area * 0.85
            val sStonePerFloor = area * 1.6

            // --- ৪. দেয়াল ও প্লাস্টার ---
            val bricksPerFloor = area * 12.5
            val wallCementPerFloor = area * 0.18
            val wallSandPerFloor = area * 0.5

            // --- ৫. মোট উপকরণের পরিমাণ ---
            val totalRod = fRod + (sRodPerFloor * floorsToBuild)
            val totalCement = fCement + ((sCementPerFloor + wallCementPerFloor) * floorsToBuild)
            val totalSand = fSand + ((sSandPerFloor + wallSandPerFloor) * floorsToBuild)
            val totalStone = fStone + (sStonePerFloor * floorsToBuild)
            val totalBricks = bricksPerFloor * floorsToBuild

            // --- ৬. গুনা, লোহা ও পলিথিন হিসাব (আগের লজিক অনুযায়ী আপডেট করা) ---
            // যেহেতু এখন তলা বেশি, তাই (area * floorsToBuild) দিয়ে গুণ হবে
            val totalEffectiveArea =
                area * (floorsToBuild + 0.5) // ফাউন্ডেশনের জন্য ০.৫ এক্সট্রা ধরা হয়েছে
            val totalBindingWire = (totalEffectiveArea * 0.007)
            val totalNails = (totalEffectiveArea * 0.005)
            val totalPolythene = (totalEffectiveArea * 1.1)

            // --- ৭. লেবার ও বিবিধ খরচ ---
            val foundationLabor = area * laborRate * 0.9
            val constructionLabor = (area * floorsToBuild) * laborRate
            val totalLaborCost = foundationLabor + constructionLabor

            //others cost
            val shutteringAndMisc = (area * floorsToBuild) * 50.0
            val othersCost = shutteringAndMisc + safetyTankCost + excavationCost

            // --- ৮. ফাইনাল হিসাব ---
            val rCost = totalRod * rodRate
            val cCost = totalCement * cementRate
            val sCost = totalSand * sandRate
            val bCost = totalBricks * brickRate
            val stCost = totalStone * stoneRate
            val bwCost = totalBindingWire * bindingWireRate
            val nCost = totalNails * nailsRate
            val pCost = totalPolythene * polytheneRate
            val grandTotal =
                rCost + cCost + sCost + bCost + stCost + totalLaborCost + bwCost + nCost + pCost + othersCost

            // --- ডাটাবেজে সেভ ---
            _currentCalculation.value = EstimationRecord(
                date = System.currentTimeMillis(),
                totalArea = area.toString(),
                foundationFloors = foundationFloors,
                floorsToBuild = floorsToBuild,

                rod = Category.ROD.dbKey,
                cement = Category.CEMENT.dbKey,
                sand = Category.SAND.dbKey,
                brick = Category.BRICKS.dbKey,
                stone = Category.STONE.dbKey,
                labor = Category.MASON_LABOR.dbKey,
                others = Category.OTHERS.dbKey,

                rodQty = totalRod.roundToInt().toString(),
                cementQty = totalCement.roundToInt().toString(),
                sandQty = totalSand.roundToInt().toString(),
                brickQty = totalBricks.roundToInt().toString(),
                stoneQty = totalStone.roundToInt().toString(),
                laborQty = (area * floorsToBuild).roundToInt().toString(),
                bindingWireQty = totalBindingWire.roundToInt().toString(),
                nailsQty = totalNails.roundToInt().toString(),
                polytheneQty = totalPolythene.roundToInt().toString(),
                othersQty = "-",

                rodCost = rCost.roundToInt().toString(),
                cementCost = cCost.roundToInt().toString(),
                sandCost = sCost.roundToInt().toString(),
                brickCost = bCost.roundToInt().toString(),
                stoneCost = stCost.roundToInt().toString(),
                laborCost = totalLaborCost.roundToInt().toString(),
                bindingWireCost = bwCost.roundToInt().toString(),
                nailsCost = nCost.roundToInt().toString(),
                polytheneCost = pCost.roundToInt().toString(),
                othersCost = othersCost.roundToInt().toString(),

                rodRate = rodRate.toInt().toString(),
                cementRate = cementRate.toInt().toString(),
                sandRate = sandRate.toInt().toString(),
                brickRate = brickRate.toInt().toString(),
                stoneRate = stoneRate.toInt().toString(),
                laborRate = laborRate.toInt().toString(),
                bindingWireRate = bindingWireRate.toString(),
                nailsRate = nailsRate.toString(),
                polytheneRate = polytheneRate.toString(),
                othersRate = "---",

                totalEstimatedCost = grandTotal.roundToInt().toString()
            )
        }
    }

    fun saveCurrentEstimation() {
        _currentCalculation.value?.let { record ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.saveEstimation(record)
                _currentCalculation.value = null
            }
        }
    }

    fun clearCalculation() {
        _currentCalculation.value = null
    }
}