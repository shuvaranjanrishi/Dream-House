package com.therishideveloper.dreamhouse.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.therishideveloper.dreamhouse.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.data.model.DreamHouseStrings
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.NumberUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onMenuClick: () -> Unit) {
    val context = LocalContext.current
    val strings = DreamHouseStrings.current
    val appVersion = getAppVersion(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.menuAbout, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            "Menu",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F7FA)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // 1. App Logo & Name
            Card(
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.size(100.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo), // আপনার লোগো দিন
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = strings.appName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = tealColor
            )
            Text(
                text = strings.appSlogan,
                fontSize = 14.sp,
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            )

            // 2. Features Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.desc_project),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val features = listOf(
                        strings.feat1,
                        strings.feat2,
                        strings.feat3,
                        strings.feat4,
                        strings.feat5,
                        strings.feat6,
                        strings.feat7,
                        strings.feat8,
                        strings.feat9,
                        strings.feat10,
                        strings.feat11
                    )
                    features.forEach { feature ->
                        Text(
                            text = feature,
                            fontSize = 13.sp,
                            color = tealColor,
                            modifier = Modifier.padding(vertical = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 3. Developer Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = tealColor.copy(0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Code,
                        null,
                        tint = tealColor,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            strings.labelDeveloper,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            strings.labelDevName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            strings.devBrand,
                            fontSize = 13.sp,
                            color = tealColor
                        )
                        Text(
                            strings.devEmail,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Version
            Text(
                text = strings.labelAppVersion + NumberUtils.formatByLocale(
                    context,
                    appVersion
                ),
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "1.0.0"
    } catch (e: Exception) {
        e.printStackTrace()
        "1.0.0"
    }
}
