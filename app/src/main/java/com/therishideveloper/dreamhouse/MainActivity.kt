package com.therishideveloper.dreamhouse

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.therishideveloper.dreamhouse.component.LanguageDialog
import com.therishideveloper.dreamhouse.navigation.AppNavigation
import com.therishideveloper.dreamhouse.navigation.Screens
import com.therishideveloper.dreamhouse.navigation.listOfNavItems
import com.therishideveloper.dreamhouse.ui.theme.DreamHouseTheme
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.LocaleHelper
import com.therishideveloper.dreamhouse.util.shareApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedLang = LocaleHelper.getSavedLocale(this)
        LocaleHelper.applyLocale(this, savedLang)

        setContent {
            DreamHouseTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SetupNavigation()
                }
            }
        }
    }

    @Composable
    private fun SetupNavigation() {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val isHome =
            currentRoute == Screens.HomeScreen.route || currentRoute == Screens.BackupScreen.route || currentRoute == Screens.CategoryManualScreen.route || currentRoute == Screens.AboutScreen.route

        var showLanguageDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current

        ModalNavigationDrawer(
            gesturesEnabled = isHome,
            drawerContent = {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        DrawerHeader()

                        val groupedItems = listOfNavItems.groupBy { it.section }

                        groupedItems.forEach { (section, items) ->
                            val sectionLabel = when (section) {
                                "Main" -> stringResource(R.string.label_main_menu)
                                "Settings" -> stringResource(R.string.label_settings)
                                "Help" -> stringResource(R.string.label_help)
                                "More" -> stringResource(R.string.label_more)
                                else -> section
                            }

                            Text(
                                text = sectionLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = tealColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    start = 28.dp,
                                    top = 20.dp,
                                    bottom = 8.dp
                                )
                            )

                            items.forEach { navigationItem ->
                                val isSelected = currentRoute == navigationItem.route
                                NavigationDrawerItem(
                                    label = {
                                        val labelText = when (navigationItem.title) {
                                            "Home" -> stringResource(R.string.menu_home)
                                            "Language" -> stringResource(R.string.menu_language)
                                            "Backup" -> stringResource(R.string.menu_backup)
                                            "Category Guide" -> stringResource(R.string.cat_guide)
                                            "Share App" -> stringResource(R.string.menu_share)
                                            "About" -> stringResource(R.string.menu_about)
                                            else -> navigationItem.title
                                        }
                                        Text(text = labelText)
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()

                                            when (navigationItem.route) {
                                                Screens.LanguageScreen.route -> {
                                                    showLanguageDialog = true
                                                }

                                                Screens.ShareAppScreen.route -> {
                                                    shareApp(context)
                                                }

                                                else -> {
                                                    navController.navigate(navigationItem.route) {
                                                        popUpTo(navController.graph.startDestinationId) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) navigationItem.selectedIcon else navigationItem.unselectedIcon,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                                        .fillMaxWidth(0.8f)
                                )
                            }

                            if (section != groupedItems.keys.last()) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(
                                            vertical = 12.dp,
                                            horizontal = 28.dp
                                        )
                                        .fillMaxWidth(0.7f),
                                    thickness = 0.5.dp,
                                    color = Color.Gray.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            },
            drawerState = drawerState
        ) {
            AppNavigation(navController, onOpenDrawer = {
                scope.launch { drawerState.open() }
            })
        }

        if (showLanguageDialog) {
            LanguageDialog(
                currentLanguageCode = LocaleHelper.getSavedLocale(context),
                onDismiss = { showLanguageDialog = false },
                onLanguageSelected = { selectedCode ->
                    LocaleHelper.applyLocale(this, selectedCode)
                    (context as? Activity)?.recreate()
                    showLanguageDialog = false
                }
            )
        }
    }

    @Composable
    fun DrawerHeader() {
        Column {
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(tealColor, Color(0xFF004D40))
                        )
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(12.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.app_slogan),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}