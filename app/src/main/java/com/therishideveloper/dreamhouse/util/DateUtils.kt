package com.therishideveloper.dreamhouse.util

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.therishideveloper.dreamhouse.data.model.TransactionPeriod
import java.text.SimpleDateFormat
import java.util.*
import com.therishideveloper.dreamhouse.R

object DateUtils {

    const val DATE_FORMAT_STANDARD = "dd-MM-yyyy"
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
    const val DATE_FORMAT_FULL_DISPLAY = "EEEE, dd MMMM yyyy"
    private val locale = Locale.US

    fun getTodayDateForHomeScreen(context: Context): String {
        val locale = context.resources.configuration.locales[0]
        val sdf = SimpleDateFormat(DATE_FORMAT_FULL_DISPLAY, locale)
        val formattedDate = sdf.format(Date())
        return NumberUtils.formatByLocale(context, formattedDate)
    }

    fun formatToDisplay(context: Context, dateMillis: Long): String {
        val locale = context.resources.configuration.locales[0]
        val sdf = SimpleDateFormat(DATE_FORMAT_DISPLAY, locale)
        val formattedDate = sdf.format(dateMillis)
        return NumberUtils.formatByLocale(context, formattedDate)
    }

    fun getDayRange(dateMillis: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    fun getMonthRange(dateMillis: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }

        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    fun getYearRange(dateMillis: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }

        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.MONTH, Calendar.DECEMBER)
        cal.set(Calendar.DAY_OF_MONTH, 31)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    fun formatToDisplay(dateMillis: Long, pattern: String = DATE_FORMAT_DISPLAY): String {
        return try {
            val formatter = SimpleDateFormat(pattern, Locale.US)
            formatter.format(Date(dateMillis))
        } catch (e: Exception) {
            ""
        }
    }

    fun formatDate(date: Date, pattern: String = DATE_FORMAT_STANDARD): String {
        val sdf = SimpleDateFormat(pattern, locale)
        return sdf.format(date)
    }

    fun getLocalizedMonthName(context: Context, monthIndex: Int, isShort: Boolean = true): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, monthIndex)
        val pattern = if (isShort) "MMM" else "MMMM"
        val locale = context.resources.configuration.locales[0]
        val sdf = SimpleDateFormat(pattern, locale)
        return sdf.format(calendar.time)
    }

    fun getLocalizedMonthYear(context: Context, month: Int, year: Int): String {
        val monthName = getLocalizedMonthName(context, month - 1, isShort = false)
        val formattedYear = NumberUtils.formatByLocale(context, year.toString())
        return "$monthName $formattedYear"
    }

    @Composable
    fun getScreenTitle(type: String): String = when (type) {
        TransactionPeriod.MONTHLY.dbKey -> stringResource(R.string.title_monthly_transactions)
        TransactionPeriod.YEARLY.dbKey -> stringResource(R.string.title_yearly_transactions)
        else -> stringResource(R.string.title_today_transactions)
    }

    fun showDatePicker(
        context: Context,
        initialDate: Long = System.currentTimeMillis(),
        onDateSelected: (Long) -> Unit
    ) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = initialDate
        }

        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onDateSelected(selectedCal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}