package com.teamecoscan.ecoscanrebirth

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.teamecoscan.ecoscanrebirth.ui.theme.EcoScanTheme
import com.teamecoscan.ecoscanrebirth.ui.theme.Green
import com.teamecoscan.ecoscanrebirth.ui.theme.LightGreen
import com.teamecoscan.ecoscanrebirth.ui.view.DetectionScreen
import com.teamecoscan.ecoscanrebirth.ui.view.HomeScreen
import com.teamecoscan.ecoscanrebirth.ui.view.ListadoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoScanTheme {
                val navController = rememberNavController()
                // Estado global para el modal de bienvenida - persiste en toda la app
                val preferencesManagerGlobal = remember { com.teamecoscan.ecoscanrebirth.data.PreferencesManager(this) }
                val showWelcomeModal = remember { mutableStateOf(preferencesManagerGlobal.shouldShowWelcome()) }

                Scaffold(
                    bottomBar = {
                        BottomBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen(navController = navController, showWelcomeModal = showWelcomeModal) }
                        composable("listado") { ListadoScreen(navController = navController) }
                        composable("camera") { DetectionScreen(onBack = { navController.popBackStack() }) }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in listOf("home", "listado")
    val view = LocalView.current

    if (showBottomBar) {
        NavigationBar {
            val homeInteractionSource = remember { MutableInteractionSource() }
            val isHomePressed by homeInteractionSource.collectIsPressedAsState()
            val homeIndicatorColor = if (isHomePressed) LightGreen else Green

            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                label = { Text("Inicio") },
                selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    navController.navigate("home") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                interactionSource = homeInteractionSource,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = homeIndicatorColor
                )
            )

            val listadoInteractionSource = remember { MutableInteractionSource() }
            val isListadoPressed by listadoInteractionSource.collectIsPressedAsState()
            val listadoIndicatorColor = if (isListadoPressed) LightGreen else Green

            NavigationBarItem(
                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Listado") },
                label = { Text("Listado") },
                selected = currentDestination?.hierarchy?.any { it.route == "listado" } == true,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    navController.navigate("listado") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                interactionSource = listadoInteractionSource,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = listadoIndicatorColor
                )
            )
        }
    }
}
