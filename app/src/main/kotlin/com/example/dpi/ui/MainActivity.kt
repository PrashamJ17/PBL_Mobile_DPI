package com.example.dpi.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dpi.ui.navigation.Screen
import com.example.dpi.ui.navigation.bottomNavItems
import com.example.dpi.ui.screens.*
import com.example.dpi.ui.theme.MobileDpiHubTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the app
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.startCapture()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MobileDpiHubTheme {
                DpiApp(
                    viewModel = viewModel,
                    onStartCapture = ::handleStartCapture
                )
            }
        }
    }
    
    private fun handleStartCapture() {
        val prepareIntent = viewModel.prepareVpn()
        if (prepareIntent != null) {
            // Need VPN permission
            vpnPermissionLauncher.launch(prepareIntent)
        } else {
            // Already have permission
            viewModel.startCapture()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DpiApp(
    viewModel: MainViewModel,
    onStartCapture: () -> Unit
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            Screen.Home.route -> "Mobile DPI Hub"
                            Screen.Packets.route -> "Packet Monitor"
                            Screen.Threats.route -> "Threat Dashboard"
                            Screen.Flows.route -> "Network Flows"
                            Screen.Settings.route -> "Settings"
                            else -> "Mobile DPI Hub"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onStartCapture = onStartCapture
                )
            }
            
            composable(Screen.Packets.route) {
                PacketMonitorScreen(viewModel = viewModel)
            }
            
            composable(Screen.Threats.route) {
                ThreatDashboardScreen(viewModel = viewModel)
            }
            
            composable(Screen.Flows.route) {
                FlowAnalysisScreen(viewModel = viewModel)
            }
        }
    }
}
