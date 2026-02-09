package com.therishideveloper.dreamhouse.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.entity.Transaction
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.data.model.TransactionType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelHelper {

    private const val CHANNEL_ID = "download_channel"
    private const val AUTHORITY_SUFFIX = ".fileProvider"
    private const val FOLDER_NAME = "Daily Expense/Transaction Report"

    fun showDownloadNotification(context: Context, file: File) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID, "File Downloads", NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val uri =
            FileProvider.getUriForFile(context, "${context.packageName}$AUTHORITY_SUFFIX", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle("Download Complete")
            .setContentText("File saved: ${file.name}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun createTransactionExcel(context: Context, transactions: List<Transaction>): File? {
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
            val downloadDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val root = File(downloadDir, FOLDER_NAME)
            if (!root.exists()) root.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
            val baseFileName = "Transaction_Report_$timeStamp"

            val file = getUniqueFile(root, baseFileName, "xlsx")

            val out = FileOutputStream(file)
            workbook.write(out)
            out.close()
            workbook.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getUniqueFile(directory: File, baseName: String, extension: String): File {
        var file = File(directory, "$baseName.$extension")
        var counter = 1

        while (file.exists()) {
            file = File(directory, "${baseName}_($counter).$extension")
            counter++
        }
        return file
    }

    fun shareExcelFile(context: Context, file: File) {
        val uri =
            FileProvider.getUriForFile(context, "${context.packageName}$AUTHORITY_SUFFIX", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
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