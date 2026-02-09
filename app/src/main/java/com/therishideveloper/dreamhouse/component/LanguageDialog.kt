package com.therishideveloper.dreamhouse.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.data.model.Language
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.R

@Composable
fun LanguageDialog(
    currentLanguageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = stringResource(R.string.title_select_language),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = tealColor
            )
        },
        text = {
            Column {
                languages.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(lang.code) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(lang.flag, fontSize = 24.sp)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = lang.name,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f),
                            color = if (currentLanguageCode == lang.code) tealColor else Color.Black,
                            fontWeight = if (currentLanguageCode == lang.code) FontWeight.Bold else FontWeight.Normal
                        )
                        if (currentLanguageCode == lang.code) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = tealColor)
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}



val languages = listOf(
    Language("English", "en", "🇺🇸"),
    Language("বাংলা (Bengali)", "bn", "🇧🇩"),
    Language("हिंदी (Hindi)", "hi", "🇮🇳")
)