package com.therishideveloper.dreamhouse.data.model

import com.therishideveloper.dreamhouse.R

enum class ConstructionStage(
    val dbKey: String,
    val titleRes: Int,
    val descriptionRes: Int
) {
    FOUNDATION("FOUNDATION", R.string.stage_foundation, R.string.desc_foundation),
    SHORT_COLUMN("SHORT_COLUMN", R.string.stage_short_column, R.string.desc_short_column),
    GRADE_BEAM("GRADE_BEAM", R.string.stage_grade_beam, R.string.desc_grade_beam),
    COLUMN("COLUMN", R.string.stage_column, R.string.desc_column),
    ROOF_SLAB("ROOF_SLAB", R.string.stage_slab, R.string.desc_slab),
    BRICK_PLASTER("BRICK_PLASTER", R.string.stage_brick_plaster, R.string.desc_brick_plaster),
    FINISHING("FINISHING", R.string.stage_finishing, R.string.desc_finishing),
    OTHERS("OTHERS", R.string.stage_others, R.string.desc_others);

    companion object {
        fun fromDbKey(key: String?): ConstructionStage =
            entries.find { it.dbKey == key } ?: OTHERS

        fun getAllStages() = entries.toList()
    }
}