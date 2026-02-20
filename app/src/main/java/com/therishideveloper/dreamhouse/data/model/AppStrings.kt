package com.therishideveloper.dreamhouse.data.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import com.therishideveloper.dreamhouse.R

data class AppStrings(
    // Common
    val success: String,
    val save: String,
    val cancel: String,
    val currency: String,

    //ProjectSetupScreen
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
    val errPreviousStageMissing: String,

    // Add Stage Screen
    val addStageTitle: String,
    val updateStageTitle: String,
    val errNoProject: String,
    val errInvalidDuration: String,
    val errFillAll: String,
    val errDuplicate: String,
    val errEditLock: String,
    val selectWorkStage: String,
    val labelUpdateStatus: String,

    //AboutScreen
    val appName:String,
    val appSlogan:String,
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
    val shareText: String,

    // Stage List Screen
    val stageListTitle: String,
    val noStages: String
)

@Composable
@ReadOnlyComposable
fun provideStrings(): AppStrings {
    return AppStrings(
        success = stringResource(R.string.msg_success),
        save = stringResource(R.string.btn_save),
        cancel = stringResource(R.string.btn_cancel),
        currency = stringResource(R.string.currency_symbol),

        errInvalidInputs = stringResource(R.string.err_invalid_input),
        menuConstructionPlan = stringResource(R.string.menu_construction_plan),
        hintProjectInfo = stringResource(R.string.hint_project_info),
        labelHouseName = stringResource(R.string.label_house_name),
        labelAddress = stringResource(R.string.label_address),
        labelTotalBudgetTaka = stringResource(R.string.label_total_budget_taka),
        labelProjectTimeline = stringResource(R.string.label_project_timeline),
        labelStartDate = stringResource(R.string.label_start_date),
        labelEndDate = stringResource(R.string.label_end_date),
        labelCreateProject = stringResource(R.string.label_create_project),
        labelUpdateProject = stringResource(R.string.label_update_project),
        selectWorkStage = stringResource(R.string.select_work_stage),
        estimatedBudget = stringResource(R.string.estimated_budget),
        budgetTaka = stringResource(R.string.budget_taka),
        selectTimeFrame = stringResource(R.string.select_timeframe),
        labelUpdateStatus = stringResource(R.string.label_update_status),
        errPreviousStageMissing = stringResource(R.string.err_previous_stage_missing),

        // Add Stage Screen
        addStageTitle = stringResource(R.string.label_add_stage),
        updateStageTitle = stringResource(R.string.label_update_stage),
        errNoProject = stringResource(R.string.err_no_active_project),
        errInvalidDuration = stringResource(R.string.err_invalid_duration),
        errFillAll = stringResource(R.string.err_fill_all),
        errDuplicate = stringResource(R.string.err_duplicate_stage),
        stageName = stringResource(R.string.stage_name),
        errEditLock = stringResource(R.string.err_edit_lock),

        stageListTitle = stringResource(R.string.label_stages),
        noStages = stringResource(R.string.no_stages_added),

        //AboutScreen
        appName = stringResource(R.string.app_name),
        appSlogan = stringResource(R.string.app_slogan),
        menuAbout = stringResource(R.string.menu_about),
        descProject = stringResource(R.string.desc_project),
        feat1 = stringResource(R.string.feat_1),
        feat2 = stringResource(R.string.feat_2),
        feat3 = stringResource(R.string.feat_3),
        feat4 = stringResource(R.string.feat_4),
        feat5 = stringResource(R.string.feat_5),
        feat6 = stringResource(R.string.feat_6),
        feat7 = stringResource(R.string.feat_7),
        feat8 = stringResource(R.string.feat_8),
        feat9 = stringResource(R.string.feat_9),
        feat10 = stringResource(R.string.feat_10),
        feat11 = stringResource(R.string.feat_11),
        labelAppVersion = stringResource(R.string.label_app_version),
        labelDeveloper = stringResource(R.string.label_developer),
        labelDevName = stringResource(R.string.dev_name),
        devBrand = stringResource(R.string.dev_brand),
        devEmail = stringResource(R.string.dev_email),
        labelMainMenu = stringResource(R.string.label_main_menu),
        labelSettings = stringResource(R.string.label_settings),
        labelHelp = stringResource(R.string.label_help),
        labelMore = stringResource(R.string.label_more),
        shareText = stringResource(R.string.share_text),
    )
}

val LocalStrings = staticCompositionLocalOf<AppStrings> {
    error("No Strings provided")
}

object DreamHouseStrings {
    val current: AppStrings
        @Composable
        @ReadOnlyComposable
        get() = LocalStrings.current
}