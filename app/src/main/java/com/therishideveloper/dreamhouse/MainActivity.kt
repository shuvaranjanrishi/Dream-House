package com.therishideveloper.dreamhouse

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.therishideveloper.dreamhouse.component.DrawerHeader
import com.therishideveloper.dreamhouse.component.LanguageDialog
import com.therishideveloper.dreamhouse.navigation.AppNavigation
import com.therishideveloper.dreamhouse.navigation.Screens
import com.therishideveloper.dreamhouse.navigation.listOfNavItems
import com.therishideveloper.dreamhouse.screen.SplashScreen
import com.therishideveloper.dreamhouse.screen.getAppVersion
import com.therishideveloper.dreamhouse.ui.theme.DreamHouseTheme
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.LocaleHelper
import com.therishideveloper.dreamhouse.util.NumberUtils
import com.therishideveloper.dreamhouse.util.ThemeHelper
import com.therishideveloper.dreamhouse.util.shareApp
import com.therishideveloper.dreamhouse.viewmodel.ProjectViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val savedLang = LocaleHelper.getSavedLocale(this)
        LocaleHelper.applyLocale(this, savedLang)

        setContent {
            var isDarkTheme by remember { mutableStateOf(ThemeHelper.isDarkTheme(this)) }

            DreamHouseTheme(darkTheme = isDarkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SetupNavigation(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = {
                            val newValue = !isDarkTheme
                            isDarkTheme = newValue
                            ThemeHelper.setDarkTheme(this@MainActivity, newValue)
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun SetupNavigation(
        isDarkTheme: Boolean,
        onToggleTheme: () -> Unit
    ) {
        val projectViewModel: ProjectViewModel = hiltViewModel()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val isHome =
            currentRoute == Screens.HomeScreen.route ||
                currentRoute == Screens.ProjectOverviewScreen.route ||
                currentRoute == Screens.BackupScreen.route ||
                currentRoute == Screens.CategoryManualScreen.route ||
                currentRoute == Screens.AboutScreen.route ||
                currentRoute == Screens.DeveloperScreen.route

        var showLanguageDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val appVersion = remember(context) { getAppVersion(context) }

        val activeProject by projectViewModel.activeProject.collectAsStateWithLifecycle()
        val isLoading by projectViewModel.isLoading.collectAsStateWithLifecycle()

        if (isLoading) {
            SplashScreen()
        } else {
            val startDest = remember(activeProject) {
                if (activeProject == null) {
                    Screens.ProjectSetupScreen.route
                } else {
                    Screens.HomeScreen.route
                }
            }

            ModalNavigationDrawer(
                gesturesEnabled = isHome,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        drawerContainerColor = MaterialTheme.colorScheme.surface,
                        drawerContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Column(modifier = Modifier.fillMaxHeight()) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
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
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(
                                            start = 28.dp,
                                            top = 16.dp,
                                            bottom = 6.dp
                                        )
                                    )

                                    items.forEach { navItem ->
                                        val isSelected = currentRoute == navItem.route
                                        NavigationDrawerItem(
                                            label = {
                                                Text(
                                                    text = stringResource(navItem.titleRes),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            selected = isSelected,
                                            onClick = {
                                                scope.launch {
                                                    drawerState.close()

                                                    when (navItem.route) {
                                                        Screens.ShareAppScreen.route -> {
                                                            shareApp(context)
                                                        }

                                                        else -> {
                                                            navController.navigate(navItem.route) {
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
                                                    imageVector = if (isSelected) navItem.selectedIcon else navItem.unselectedIcon,
                                                    contentDescription = null
                                                )
                                            },
                                            colors = NavigationDrawerItemDefaults.colors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                unselectedTextColor = MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier
                                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                                                .fillMaxWidth()
                                        )
                                    }

                                    if (section != groupedItems.keys.last()) {
                                        HorizontalDivider(
                                            modifier = Modifier
                                                .padding(
                                                    vertical = 10.dp,
                                                    horizontal = 28.dp
                                                )
                                                .fillMaxWidth(0.7f),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                            }

                            HorizontalDivider(
                                thickness = 0.8.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            )

                            // Fixed Bottom Footer Row: Language (Left) | App Name & Version (Center) | Theme (Right)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            showLanguageDialog = true
                                        }
                                    },
                                    modifier = Modifier.testTag("drawer_language_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = stringResource(R.string.cd_change_language),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(R.string.app_name),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "v${NumberUtils.formatByLocale(context, appVersion)}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                IconButton(
                                    onClick = onToggleTheme,
                                    modifier = Modifier.testTag("drawer_theme_button")
                                ) {
                                    Icon(
                                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = stringResource(R.string.cd_toggle_theme),
                                        tint = if (isDarkTheme) MaterialTheme.colorScheme.tertiary else tealColor
                                    )
                                }
                            }
                        }
                    }
                },
                drawerState = drawerState
            ) {
                AppNavigation(
                    navController = navController,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    startDestination = startDest
                )
            }
        }

        if (showLanguageDialog) {
            LanguageDialog(
                currentLanguageCode = LocaleHelper.getSavedLocale(context),
                onDismiss = { showLanguageDialog = false },
                onLanguageSelected = { selectedCode ->
                    LocaleHelper.applyLocale(this@MainActivity, selectedCode)
                    showLanguageDialog = false
                    recreate()
                }
            )
        }
    }
}