package com.therishideveloper.dreamhouse.data.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import com.therishideveloper.dreamhouse.R

object DreamHouseStrings {
    val current: AppStrings
        @Composable
        @ReadOnlyComposable
        get() = LocalStrings.current
}

val LocalStrings = staticCompositionLocalOf<AppStrings> {
    error("No Strings provided")
}

data class CommonStrings(
    val success: String,
    val save: String,
    val cancel: String,
    val currency: String
)

data class SplashStrings(
    val poweredBy: String
)

data class SetupStrings(
    val errInvalidInputs: String,
    val menuConstructionPlan: String,
    val hintProjectInfo: String,
    val labelHouseName: String,
    val labelAddress: String,
    val labelTotalBudgetTaka: String,
    val labelProjectTimeline: String,
    val labelStartDate: String,
    val labelEndDate: String,
    val labelCreateProject: String,
    val labelUpdateProject: String,
    val stageName: String,
    val estimatedBudget: String,
    val budgetTaka: String,
    val selectTimeFrame: String,
    val errPreviousStageMissing: String
)

data class AboutStrings(
    val appName: String,
    val appSlogan: String,
    val menuAbout: String,
    val descProject: String,
    val feat1: String,
    val feat2: String,
    val feat3: String,
    val feat4: String,
    val feat5: String,
    val feat6: String,
    val feat7: String,
    val feat8: String,
    val feat9: String,
    val feat10: String,
    val feat11: String,
    val labelAppVersion: String,
    val labelDeveloper: String,
    val labelDevName: String,
    val devBrand: String,
    val devEmail: String,
    val labelMainMenu: String,
    val labelSettings: String,
    val labelHelp: String,
    val labelMore: String,
    val shareText: String
)

data class StageStrings(
    val addStageTitle: String,
    val updateStageTitle: String,
    val errNoProject: String,
    val errInvalidDuration: String,
    val errFillAll: String,
    val errDuplicate: String,
    val errEditLock: String,
    val selectWorkStage: String,
    val labelUpdateStatus: String,
    val stageListTitle: String,
    val noStages: String
)

data class AppStrings(
    val common: CommonStrings,
    val splash: SplashStrings,
    val setup: SetupStrings,
    val stage: StageStrings,
    val about: AboutStrings
)

@Composable
@ReadOnlyComposable
private fun get(id: Int): String = stringResource(id)

@Composable
@ReadOnlyComposable
fun provideStrings(): AppStrings {
    return AppStrings(
        common = CommonStrings(
            success = get(R.string.msg_success),
            save = get(R.string.btn_save),
            cancel = get(R.string.btn_cancel),
            currency = get(R.string.currency_symbol)
        ),
        splash = SplashStrings(
            poweredBy = get(R.string.powered_by)
        ),
        setup = SetupStrings(
            errInvalidInputs = get(R.string.err_invalid_input),
            menuConstructionPlan = get(R.string.menu_construction_plan),
            hintProjectInfo = get(R.string.hint_project_info),
            labelHouseName = get(R.string.label_house_name),
            labelAddress = get(R.string.label_address),
            labelTotalBudgetTaka = get(R.string.label_total_budget_taka),
            labelProjectTimeline = get(R.string.label_project_timeline),
            labelStartDate = get(R.string.label_start_date),
            labelEndDate = get(R.string.label_end_date),
            labelCreateProject = get(R.string.label_create_project),
            labelUpdateProject = get(R.string.label_update_project),
            stageName = get(R.string.stage_name),
            estimatedBudget = get(R.string.estimated_budget),
            budgetTaka = get(R.string.budget_taka),
            selectTimeFrame = get(R.string.select_timeframe),
            errPreviousStageMissing = get(R.string.err_previous_stage_missing)
        ),
        stage = StageStrings(
            addStageTitle = get(R.string.label_add_stage),
            updateStageTitle = get(R.string.label_update_stage),
            errNoProject = get(R.string.err_no_active_project),
            errInvalidDuration = get(R.string.err_invalid_duration),
            errFillAll = get(R.string.err_fill_all),
            errDuplicate = get(R.string.err_duplicate_stage),
            errEditLock = get(R.string.err_edit_lock),
            selectWorkStage = get(R.string.select_work_stage),
            labelUpdateStatus = get(R.string.label_update_status),
            stageListTitle = get(R.string.label_stages),
            noStages = get(R.string.no_stages_added)
        ),
        about = AboutStrings(
            appName = get(R.string.app_name),
            appSlogan = get(R.string.app_slogan),
            menuAbout = get(R.string.menu_about),
            descProject = get(R.string.desc_project),
            feat1 = get(R.string.feat_1),
            feat2 = get(R.string.feat_2),
            feat3 = get(R.string.feat_3),
            feat4 = get(R.string.feat_4),
            feat5 = get(R.string.feat_5),
            feat6 = get(R.string.feat_6),
            feat7 = get(R.string.feat_7),
            feat8 = get(R.string.feat_8),
            feat9 = get(R.string.feat_9),
            feat10 = get(R.string.feat_10),
            feat11 = get(R.string.feat_11),
            labelAppVersion = get(R.string.label_app_version),
            labelDeveloper = get(R.string.label_developer),
            labelDevName = get(R.string.dev_name),
            devBrand = get(R.string.dev_brand),
            devEmail = get(R.string.dev_email),
            labelMainMenu = get(R.string.label_main_menu),
            labelSettings = get(R.string.label_settings),
            labelHelp = get(R.string.label_help),
            labelMore = get(R.string.label_more),
            shareText = get(R.string.share_text),
        )
    )
}