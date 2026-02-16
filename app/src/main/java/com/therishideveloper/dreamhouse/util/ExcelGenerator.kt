package com.therishideveloper.dreamhouse.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.data.model.TransactionType
import com.therishideveloper.dreamhouse.util.FileHelper.MIME_TYPE_SHEET
import com.therishideveloper.dreamhouse.util.FileHelper.showDownloadNotification
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelGenerator {

    private const val AUTHORITY_SUFFIX = ".fileProvider"
    private const val FOLDER_NAME = "/Transaction Report"

    fun generateTransactionExcel(context: Context, transactions: List<Transaction>): File? {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Transactions")

        val headerRow = sheet.createRow(0)
        val headers = listOf("Date", "Category", "Description", "Amount", "Type")
        headers.forEachIndexed { index, title ->
            headerRow.createCell(index).setCellValue(title)
        }

        transactions.forEachIndexed { index, tx ->
            val row = sheet.createRow(index + 1)
            val category = Category.fromDbKey(tx.category)
            val categoryLabel = context.getString(category.titleRes)
            val type = TransactionType.fromDbKey(tx.transactionType)
            val typeLabel = context.getString(type.titleRes)
            val amount = NumberUtils.formatByLocale(context, tx.amount.toString())
            row.createCell(0).setCellValue(DateUtils.formatToDisplay(tx.date))
            row.createCell(1).setCellValue(categoryLabel)
            row.createCell(2).setCellValue(tx.description)
            row.createCell(3).setCellValue(amount)
            row.createCell(4).setCellValue(typeLabel)
        }

        sheet.setColumnWidth(2, 30 * 256)

        return try {
            val folder = FileHelper.getSaveDir(context.getString(R.string.app_name) + FOLDER_NAME)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
            val file = FileHelper.getUniqueFile(folder, "Transaction_Report_$timeStamp", "xlsx")

            val out = FileOutputStream(file)
            workbook.write(out)
            out.close()
            workbook.close()

            showDownloadNotification(context, file, 1)

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareExcelFile(context: Context, file: File) {
        val uri =
            FileProvider.getUriForFile(context, "${context.packageName}$AUTHORITY_SUFFIX", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = MIME_TYPE_SHEET
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Transaction Report")
            putExtra(
                Intent.EXTRA_TEXT,
                "Here is my transaction report generated from Daily Expense app."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share Report via"))
    }
}