package com.therishideveloper.dreamhouse.screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.showToast
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(onMenuClick: () -> Unit, viewModel: TransactionViewModel) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    // File picker for restoration logic
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            showRestoreDialog = true
        }
    }

    // Share intent result handler
    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { isLoading = false }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.backup_top_title), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp), // Fixed overall horizontal padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // 1. Instruction Cards
                InfoSection()
                Spacer(modifier = Modifier.height(12.dp))
                WhyBackupSection()

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Visual Identity Section
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    tint = tealColor
                )
                Text(
                    text = stringResource(R.string.backup_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = stringResource(R.string.backup_subtitle),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 28.dp)
                )

                // 3. Main Action Buttons
                Button(
                    onClick = {
                        isLoading = true
                        viewModel.exportBackup { uri ->
                            if (uri != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                shareLauncher.launch(
                                    Intent.createChooser(
                                        intent,
                                        "Save Backup File"
                                    )
                                )
                            } else {
                                isLoading = false
                                showToast(context, context.getString(R.string.msg_backup_failed))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = tealColor)
                ) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.btn_export), fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { filePickerLauncher.launch("application/json") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                ) {
                    Icon(Icons.Default.SettingsBackupRestore, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.btn_import),
                        fontWeight = FontWeight.SemiBold,
                        color = tealColor
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }

            // 4. Centered Loading Indicator overlay
            if (isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.3f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = tealColor, strokeWidth = 4.dp)
                    }
                }
            }
        }
    }

    // 5. Confirmation Dialog Logic
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = {
                Text(
                    stringResource(R.string.restore_confirm_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(stringResource(R.string.restore_confirm_msg)) },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    onClick = {
                        showRestoreDialog = false
                        isLoading = true
                        selectedUri?.let { uri ->
                            viewModel.importBackup(uri)
                            showToast(context, context.getString(R.string.msg_restore_success))
                        }
                        isLoading = false
                    }
                ) { Text(stringResource(R.string.btn_yes_restore), color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRestoreDialog = false
                }) { Text(stringResource(android.R.string.cancel)) }
            }
        )
    }
}

// Reusable Section Composables
@Composable
fun InfoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = tealColor.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(
                title = stringResource(R.string.label_why_backup),
                icon = Icons.Default.CheckCircle
            )
            Spacer(modifier = Modifier.height(8.dp))
            BulletItem(stringResource(R.string.why_1))
            BulletItem(stringResource(R.string.why_2))
            BulletItem(stringResource(R.string.why_3))
        }
    }
}

@Composable
fun WhyBackupSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(
                title = stringResource(R.string.label_instructions),
                icon = Icons.Default.Info
            )
            Spacer(modifier = Modifier.height(8.dp))
            BulletItem(stringResource(R.string.inst_1))
            BulletItem(stringResource(R.string.inst_2))
            BulletItem(stringResource(R.string.inst_3))
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = tealColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontWeight = FontWeight.ExtraBold, color = tealColor, fontSize = 15.sp)
    }
}

@Composable
fun BulletItem(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(tealColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 19.sp)
    }
}

//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BackupScreen(onMenuClick: () -> Unit, viewModel: TransactionViewModel) {
//    val context = LocalContext.current
//    var isLoading by remember { mutableStateOf(false) }
//    var showRestoreDialog by remember { mutableStateOf(false) }
//    var selectedUri by remember { mutableStateOf<Uri?>(null) }
//
//    // File picker for restoration
//    val filePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        if (uri != null) {
//            selectedUri = uri
//            showRestoreDialog = true
//        }
//    }
//
//    // Share launcher for backup file
//    val shareLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { isLoading = false }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(stringResource(R.string.menu_backup), color = Color.White) },
//                navigationIcon = {
//                    IconButton(onClick = onMenuClick) {
//                        Icon(
//                            Icons.Default.Menu,
//                            "Menu",
//                            tint = Color.White
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor)
//            )
//        }
//    ) { padding ->
////    Box(modifier = Modifier.fillMaxSize()) {
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(30.dp))
//
//            InfoSection()
//            Spacer(modifier = Modifier.height(16.dp))
//            WhyBackupSection()
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // 2. Icon and Title
//            Icon(
//                Icons.Default.CloudUpload,
//                contentDescription = null,
//                modifier = Modifier.size(80.dp),
//                tint = tealColor
//            )
//            Text(
//                text = "Secure Your Data",
//                fontSize = 22.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//            Text(
//                text = "Keep your financial records safe and portable",
//                fontSize = 14.sp,
//                color = Color.Gray,
//                modifier = Modifier.padding(bottom = 24.dp)
//            )
//
//            // 3. Export Button
//            Button(
//                onClick = {
//                    isLoading = true
//                    viewModel.exportBackup { uri ->
//                        if (uri != null) {
//                            val intent = Intent(Intent.ACTION_SEND).apply {
//                                type = "application/json"
//                                putExtra(Intent.EXTRA_STREAM, uri)
//                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                            }
//                            shareLauncher.launch(Intent.createChooser(intent, "Save Backup File"))
//                        } else {
//                            isLoading = false
//                            showToast(context, "Backup Failed!")
//                        }
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = tealColor)
//            ) {
//                Icon(Icons.Default.Share, null)
//                Spacer(Modifier.width(8.dp))
//                Text("Export Backup File", fontWeight = FontWeight.SemiBold)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // 4. Import Button
//            OutlinedButton(
//                onClick = { filePickerLauncher.launch("application/json") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Icon(Icons.Default.SettingsBackupRestore, null)
//                Spacer(Modifier.width(8.dp))
//                Text("Import & Restore Data", fontWeight = FontWeight.SemiBold)
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//        }
//
//        // Loading Indicator
//        if (isLoading) {
//            Surface(
//                modifier = Modifier.fillMaxSize(),
//                color = Color.Black.copy(alpha = 0.4f)
//            ) {
//                Box(contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator(color = tealColor)
//                }
//            }
//        }
//    }
//
//    // Confirmation Dialog
//    if (showRestoreDialog) {
//        AlertDialog(
//            onDismissRequest = { showRestoreDialog = false },
//            title = { Text("Restore Confirmation") },
//            text = { Text("Warning: Restoring data will permanently overwrite all your current local records with the data from the backup file. Do you want to proceed?") },
//            confirmButton = {
//                Button(
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
//                    onClick = {
//                        showRestoreDialog = false
//                        isLoading = true
//                        selectedUri?.let { uri ->
//                            viewModel.importBackup(uri)
//                            showToast(context, "Data Restored Successfully!")
//                        }
//                        isLoading = false
//                    }
//                ) { Text("Yes, Restore", color = Color.White) }
//            },
//            dismissButton = {
//                TextButton(onClick = { showRestoreDialog = false }) { Text("Cancel") }
//            }
//        )
//    }
//}
//
//@Composable
//fun InfoSection() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = tealColor.copy(alpha = 0.08f))
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            SectionHeader(title = "Instructions", icon = Icons.Default.Info)
//            Spacer(modifier = Modifier.height(8.dp))
//            BulletItem("Backup files are stored in the 'DailyExpense' folder.")
//            BulletItem("Daily backups will replace the previous one for the same day.")
//            BulletItem("Upload your .json file to Cloud Storage for extra safety.")
//        }
//    }
//}
//
//@Composable
//fun WhyBackupSection() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            SectionHeader(title = "Why use Backup?", icon = Icons.Default.CheckCircle)
//            Spacer(modifier = Modifier.height(8.dp))
//            BulletItem("Avoid data loss during phone damage or factory reset.")
//            BulletItem("Seamlessly migrate data to your new Android device.")
//            BulletItem("Keep your local offline records permanently secure.")
//        }
//    }
//}
//
//@Composable
//fun SectionHeader(title: String, icon: ImageVector) {
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Icon(icon, contentDescription = null, tint = tealColor, modifier = Modifier.size(20.dp))
//        Spacer(modifier = Modifier.width(8.dp))
//        Text(text = title, fontWeight = FontWeight.Bold, color = tealColor, fontSize = 16.sp)
//    }
//}
//
//@Composable
//fun BulletItem(text: String) {
//    Row(modifier = Modifier.padding(vertical = 3.dp)) {
//        Box(
//            modifier = Modifier
//                .padding(top = 6.dp)
//                .size(6.dp)
//                .clip(CircleShape)
//                .background(tealColor)
//        )
//        Spacer(modifier = Modifier.width(10.dp))
//        Text(text = text, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
//    }
//}
