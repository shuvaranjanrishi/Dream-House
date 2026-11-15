package com.therishideveloper.dreamhouse.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.ui.theme.LocalIsDarkTheme
import com.therishideveloper.dreamhouse.ui.theme.amberAccent
import com.therishideveloper.dreamhouse.ui.theme.tealColor

private data class DeveloperAppItem(
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val categoryRes: Int,
    @StringRes val descRes: Int,
    val playStoreUrl: String
)

private val developerApps = listOf(
    DeveloperAppItem(
        iconRes = R.drawable.ic_app_children_name,
        titleRes = R.string.app_children_name_title,
        categoryRes = R.string.app_children_name_cat,
        descRes = R.string.app_children_name_desc,
        playStoreUrl = "https://play.google.com/store/apps/details?id=com.therishideveloper.children_name"
    ),
    DeveloperAppItem(
        iconRes = R.drawable.ic_app_daily_expense,
        titleRes = R.string.app_daily_expense_title,
        categoryRes = R.string.app_daily_expense_cat,
        descRes = R.string.app_daily_expense_desc,
        playStoreUrl = "https://play.google.com/store/apps/details?id=com.therishideveloper.dailyexpense"
    ),
    DeveloperAppItem(
        iconRes = R.drawable.ic_app_dream_house,
        titleRes = R.string.app_dream_house_title,
        categoryRes = R.string.app_dream_house_cat,
        descRes = R.string.app_dream_house_desc,
        playStoreUrl = "https://play.google.com/store/apps/details?id=com.therishideveloper.dreamhouse"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperScreen(onMenuClick: () -> Unit) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current

    val pageBackground = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFFAF6EE)
    val cardBackground = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    val nestedCardBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFFCF9F2)
    val cardBorderColor = if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) else Color(0xFFEBDCCB)
    val accentColor = if (isDark) Color(0xFFFFA726) else amberAccent
    val softBadgeBg = if (isDark) accentColor.copy(alpha = 0.16f) else Color(0xFFFFF3E0)
    val softBadgeText = if (isDark) Color(0xFFFFCC80) else Color(0xFF8C4A00)
    val onCardText = MaterialTheme.colorScheme.onSurface
    val mutedText = MaterialTheme.colorScheme.onSurfaceVariant

    val email = stringResource(R.string.dev_email)
    val emailCopiedMsg = stringResource(R.string.dev_email_copied)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.menu_developer_info),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.dev_brand),
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier.testTag("developer_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.menu_developer_info),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(pageBackground),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card 1: Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = BorderStroke(1.dp, cardBorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Subtle top-right watermark
                        Text(
                            text = "ॐ",
                            fontSize = 44.sp,
                            color = accentColor.copy(alpha = if (isDark) 0.12f else 0.08f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 12.dp, end = 18.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFFFF9800), amberAccent)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = softBadgeBg,
                                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = stringResource(R.string.dev_badge_brand),
                                    color = softBadgeText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = stringResource(R.string.dev_name),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = onCardText,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.dev_role),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = softBadgeText
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.dev_university),
                                    fontSize = 13.sp,
                                    color = mutedText
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                thickness = 0.8.dp,
                                color = cardBorderColor
                            )

                            // Send Email CTA
                            Button(
                                onClick = { openEmailClient(context, email) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("developer_send_email_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = amberAccent,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(R.string.dev_send_email_label),
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Text(
                                        text = email,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Copy Email Button
                            OutlinedButton(
                                onClick = {
                                    val clipboard =
                                        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    clipboard?.setPrimaryClip(ClipData.newPlainText("email", email))
                                    Toast.makeText(context, emailCopiedMsg, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("developer_copy_email_button"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, cardBorderColor),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = nestedCardBg,
                                    contentColor = onCardText
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = mutedText
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.dev_copy_email),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Card 2: Mission & Tech Service
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = BorderStroke(1.dp, cardBorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Handshake,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.dev_mission_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = onCardText
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.dev_mission_desc),
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = mutedText
                        )
                    }
                }

                // Card 3: More Apps
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = BorderStroke(1.dp, cardBorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Smartphone,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.dev_more_apps_title),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = onCardText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.dev_play_store),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor,
                                modifier = Modifier.clickable {
                                    openUrl(
                                        context,
                                        "https://play.google.com/store/apps/developer?id=The+Rishi+Developer"
                                    )
                                }
                            )
                        }

                        Text(
                            text = stringResource(R.string.dev_more_apps_subtitle),
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = mutedText
                        )

                        developerApps.forEachIndexed { index, appItem ->
                            DeveloperAppRowCard(
                                item = appItem,
                                backgroundColor = nestedCardBg,
                                borderColor = cardBorderColor,
                                badgeBg = softBadgeBg,
                                badgeTextColor = softBadgeText,
                                onCardText = onCardText,
                                mutedText = mutedText,
                                testTag = "developer_app_install_$index",
                                onInstallClick = { openUrl(context, appItem.playStoreUrl) }
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Surface(
                            onClick = {
                                openUrl(
                                    context,
                                    "https://play.google.com/store/apps/developer?id=The+Rishi+Developer"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("developer_all_apps_button"),
                            shape = RoundedCornerShape(14.dp),
                            color = softBadgeBg,
                            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${stringResource(R.string.dev_btn_all_apps)} →",
                                    color = softBadgeText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Footer
                Text(
                    text = stringResource(R.string.dev_footer_copyright),
                    fontSize = 12.sp,
                    color = mutedText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun DeveloperAppRowCard(
    item: DeveloperAppItem,
    backgroundColor: Color,
    borderColor: Color,
    badgeBg: Color,
    badgeTextColor: Color,
    onCardText: Color,
    mutedText: Color,
    testTag: String,
    onInstallClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                shadowElevation = 1.dp
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = stringResource(item.titleRes),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(item.titleRes),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = onCardText,
                    maxLines = 2,
                    lineHeight = 18.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = stringResource(item.categoryRes),
                        fontSize = 11.sp,
                        color = mutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = stringResource(item.descRes),
                    fontSize = 12.sp,
                    color = mutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                onClick = onInstallClick,
                modifier = Modifier
                    .height(48.dp)
                    .testTag(testTag),
                shape = RoundedCornerShape(12.dp),
                color = badgeBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.dev_btn_install),
                        color = badgeTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = badgeTextColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, R.string.msg_error, Toast.LENGTH_SHORT).show()
    }
}

private fun openEmailClient(context: Context, email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, R.string.msg_error, Toast.LENGTH_SHORT).show()
    }
}
