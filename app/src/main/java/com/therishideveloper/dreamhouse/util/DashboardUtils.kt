package com.therishideveloper.dreamhouse.util

import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.model.DashboardItem
import com.therishideveloper.dreamhouse.data.model.TransactionPeriod
import com.therishideveloper.dreamhouse.navigation.Screens

object DashboardUtils {
    fun getDashboardMenus() = listOf(
        DashboardItem(
            R.string.menu_today,
            R.drawable.outline_today_24,
            TransactionPeriod.TODAY.dbKey
        ),
        DashboardItem(
            R.string.menu_note,
            R.drawable.outline_note_add_24,
            Screens.NoteScreen.route
        ),
        DashboardItem(
            R.string.menu_monthly,
            R.drawable.outline_calendar_month_24,
            TransactionPeriod.MONTHLY.dbKey
        ),
        DashboardItem(
            R.string.menu_all_transactions,
            R.drawable.outline_view_list_24,
            Screens.AllTransactionsScreen.route
        ),
        DashboardItem(
            R.string.menu_yearly,
            R.drawable.outline_calendar_today_24,
            TransactionPeriod.YEARLY.dbKey
        ),
        DashboardItem(
            R.string.menu_category_chart,
            R.drawable.outline_category_24,
            Screens.CategoryChartScreen.route
        ),
        DashboardItem(
            R.string.menu_section_chart,
            R.drawable.outline_list_alt_24,
            Screens.SectionChartScreen.route
        )
    )
}