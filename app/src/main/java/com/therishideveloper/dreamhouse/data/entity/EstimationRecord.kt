package com.therishideveloper.dreamhouse.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_estimation")
data class EstimationRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val totalArea: String,

    // নতুন ফিল্ড যা আপনি চাইলেন
    val foundationFloors: Int,
    val floorsToBuild: Int,

    // Titles (DB Keys)
    val rod: String,
    val cement: String,
    val sand: String,
    val brick: String,
    val stone: String,
    val labor: String,
    val others: String,

    // Quantities
    val rodQty: String,
    val cementQty: String,
    val sandQty: String,
    val brickQty: String,
    val stoneQty: String,
    val laborQty: String,
    val bindingWireQty: String,
    val nailsQty: String,
    val polytheneQty: String,
    val othersQty: String,

    // Costs
    val rodCost: String,
    val cementCost: String,
    val sandCost: String,
    val brickCost: String,
    val stoneCost: String,
    val laborCost: String,
    val bindingWireCost: String,
    val nailsCost: String,
    val polytheneCost: String,
    val othersCost: String,

    // Rates
    val rodRate: String,
    val cementRate: String,
    val sandRate: String,
    val brickRate: String,
    val stoneRate: String,
    val laborRate: String,
    val bindingWireRate: String,
    val nailsRate: String,
    val polytheneRate: String,
    val othersRate: String,

    //
    val totalEstimatedCost: String
)
