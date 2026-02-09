package com.therishideveloper.dreamhouse.util

import android.content.Context
import android.content.Intent
import com.therishideveloper.dreamhouse.R

fun shareApp(context: Context) {
    val appPackageName = context.packageName
    val shareMessage = context.getString(R.string.share_text) +
            "https://play.google.com/store/apps/details?id=$appPackageName"

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.menu_share))
    context.startActivity(shareIntent)
}