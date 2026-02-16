package com.therishideveloper.dreamhouse.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.therishideveloper.dreamhouse.R
import java.io.File

object FileHelper {

    private const val CHANNEL_ID = "file_download_channel"
    private const val AUTHORITY_SUFFIX = ".fileProvider"
    const val MIME_TYPE_SHEET =
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    const val MIME_TYPE_PDF = "application/pdf"

    fun getSaveDir(subFolder: String): File {
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val root = File(downloadDir, subFolder)
        if (!root.exists()) root.mkdirs()
        return root
    }

    fun getUniqueFile(directory: File, baseName: String, extension: String): File {
        val cleanBaseName = baseName.replace(" ", "_")
        var file = File(directory, "$cleanBaseName.$extension")
        var counter = 1
        while (file.exists()) {
            file = File(directory, "${cleanBaseName}_($counter).$extension")
            counter++
        }
        return file
    }

    fun showDownloadNotification(context: Context, file: File, fileType: Int) {
        val mimeType = if (fileType == 0) MIME_TYPE_SHEET else MIME_TYPE_PDF
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, "File Status", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val uri =
            FileProvider.getUriForFile(context, "${context.packageName}$AUTHORITY_SUFFIX", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo) // আপনার লোগো রিসোর্স নিশ্চিত করুন
            .setContentTitle(context.getString(R.string.download_complete))
            .setContentText("${file.name} ফাইলটি সেভ করা হয়েছে")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}