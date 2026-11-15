package com.therishideveloper.dreamhouse.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.model.AppBackupData
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupHelper(private val context: Context) {

    private val backupFolder = context.getString(R.string.app_name) + "/Backup"

    fun createBackup(backupData: AppBackupData): Uri? {
        return try {
            val jsonData = Gson().toJson(backupData)
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            val root = File(downloadDir, backupFolder)
            if (!root.exists()) root.mkdirs()

            val timeStamp = SimpleDateFormat("yyyy_MM_dd", Locale.US).format(Date())
            val fileName = "DreamHouse_Backup_$timeStamp.json"
            val file = File(root, fileName)

            file.writeText(jsonData)

            FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun restoreBackup(uri: Uri): AppBackupData? {
        return try {
            val jsonData = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: return null
            val type = object : TypeToken<AppBackupData>() {}.type
            Gson().fromJson(jsonData, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
