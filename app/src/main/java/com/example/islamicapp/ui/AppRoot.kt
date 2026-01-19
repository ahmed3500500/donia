package com.example.islamicapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.islamicapp.ui.screens.AzkarScreen
import com.example.islamicapp.ui.screens.HomeScreen
import com.example.islamicapp.ui.screens.MoreScreen
import com.example.islamicapp.ui.screens.NamesScreen
import com.example.islamicapp.ui.screens.PrayerTimesScreen
import com.example.islamicapp.ui.screens.QiblaScreen
import com.example.islamicapp.ui.screens.QuranScreen
import com.example.islamicapp.ui.screens.SettingsScreen
import com.example.islamicapp.ui.screens.TasbeehScreen

object Routes {
    const val Home = "home"
    const val Quran = "quran"
    const val More = "more"

    const val PrayerTimes = "prayer_times"
    const val Tasbeeh = "tasbeeh"
    const val Azkar = "azkar"
    const val Qibla = "qibla"
    const val Names = "names"
    const val Settings = "settings"
}

enum class BottomDestination(val route: String, val label: String, val icon: ImageVector) {
    Home(Routes.Home, "الرئيسية", Icons.Filled.Home),
    Quran(Routes.Quran, "القرآن", Icons.Filled.MenuBook),
    More(Routes.More, "المزيد", Icons.Filled.MoreHoriz)
}

@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomRoutes = remember { BottomDestination.values().map { it.route }.toSet() }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (currentRoute in bottomRoutes) {
                NavigationBar {
                    BottomDestination.values().forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.Home) {
                HomeScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(Routes.Quran) { QuranScreen() }
            composable(Routes.More) {
                MoreScreen(onNavigate = { route -> navController.navigate(route) })
            }

            composable(Routes.PrayerTimes) { PrayerTimesScreen() }
            composable(Routes.Tasbeeh) { TasbeehScreen() }
            composable(Routes.Azkar) { AzkarScreen() }
            composable(Routes.Qibla) { QiblaScreen() }
            composable(Routes.Names) { NamesScreen() }
            composable(Routes.Settings) { SettingsScreen() }
        }
    }
}
