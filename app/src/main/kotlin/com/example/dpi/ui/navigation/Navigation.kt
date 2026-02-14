package com.example.dpi.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation destinations
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Packets : Screen("packets", "Packets", Icons.Default.List)
    object Threats : Screen("threats", "Threats", Icons.Default.Warning)
    object Flows : Screen("flows", "Flows", Icons.Default.AccountTree)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

/**
 * List of bottom navigation items
 */
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Packets,
    Screen.Threats,
    Screen.Flows
)
