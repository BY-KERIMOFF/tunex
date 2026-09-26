package com.neoplay.radio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.ui.phone.CategoryScreen
import com.neoplay.radio.ui.phone.FavoritesScreen
import com.neoplay.radio.ui.phone.HomeScreen
import com.neoplay.radio.ui.phone.PlayerScreen
import com.neoplay.radio.ui.phone.SearchScreen
import com.neoplay.radio.ui.phone.SettingsScreen
import com.neoplay.radio.ui.phone.StationsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Categories : Screen("categories")
    object Stations : Screen("stations/{category}") {
        fun createRoute(category: String) = "stations/$category"
    }
    object Player : Screen("player")
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    onPlayStation: (Station) -> Unit,
    currentStation: Station?
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { category ->
                    navController.navigate(Screen.Stations.createRoute(category))
                },
                onStationClick = { station ->
                    onPlayStation(station)
                    navController.navigate(Screen.Player.route)
                }
            )
        }

        composable(Screen.Categories.route) {
            CategoryScreen(
                onCategoryClick = { category ->
                    navController.navigate(Screen.Stations.createRoute(category))
                }
            )
        }

        composable(
            route = Screen.Stations.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Popular"
            StationsScreen(
                categoryName = category,
                onBackClick = { navController.popBackStack() },
                onStationClick = { station ->
                    onPlayStation(station)
                    navController.navigate(Screen.Player.route)
                }
            )
        }

        composable(Screen.Player.route) {
            PlayerScreen(
                station = currentStation,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onStationClick = { station ->
                    onPlayStation(station)
                    navController.navigate(Screen.Player.route)
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onStationClick = { station ->
                    onPlayStation(station)
                    navController.navigate(Screen.Player.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
