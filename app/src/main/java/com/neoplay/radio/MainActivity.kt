package com.neoplay.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.neoplay.radio.ui.navigation.NavGraph
import com.neoplay.radio.ui.navigation.Screen
import com.neoplay.radio.ui.theme.NeoRadioTheme
import com.neoplay.radio.viewmodel.PlayerViewModel
import com.neoplay.radio.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeSetting by settingsViewModel.theme.collectAsState()
            val currentStation by playerViewModel.currentStation.collectAsState()

            NeoRadioTheme(themeSetting = themeSetting) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute != Screen.Player.route) {
                            NavigationBar {
                                val items = listOf(
                                    Screen.Home to (R.string.tab_home to Icons.Default.Home),
                                    Screen.Categories to (R.string.tab_categories to Icons.Default.Category),
                                    Screen.Search to (R.string.tab_search to Icons.Default.Search),
                                    Screen.Favorites to (R.string.tab_favorites to Icons.Default.Favorite),
                                    Screen.Settings to (R.string.tab_settings to Icons.Default.Settings)
                                )

                                items.forEach { (screen, info) ->
                                    val (titleRes, icon) = info
                                    NavigationBarItem(
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(Screen.Home.route) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = { Icon(imageVector = icon, contentDescription = null) },
                                        label = { Text(text = stringResource(id = titleRes)) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(
                            navController = navController,
                            onPlayStation = { station -> playerViewModel.playStation(station) },
                            currentStation = currentStation
                        )
                    }
                }
            }
        }
    }
}
