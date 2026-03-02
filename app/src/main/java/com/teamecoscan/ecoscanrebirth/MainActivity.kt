package com.teamecoscan.ecoscanrebirth

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.teamecoscan.ecoscanrebirth.ui.theme.EcoScanTheme
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
                val preferencesManagerGlobal = remember { com.teamecoscan.ecoscanrebirth.data.PreferencesManager(this) }
                val showWelcomeModal = remember { mutableStateOf(preferencesManagerGlobal.shouldShowWelcome()) }

                Scaffold(
                    bottomBar = {
                        BottomBar(navController = navController)
                    }
                ) { innerPadding ->
                    // Eliminamos el padding(innerPadding) de aquí para que las pantallas
                    // tengan control total sobre su posición (Edge-to-Edge).
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("home") { 
                            // Aplicamos el padding solo a las pantallas que lo necesiten
                            Box(modifier = Modifier.padding(innerPadding)) {
                                HomeScreen(navController = navController, showWelcomeModal = showWelcomeModal)
                            }
                        }
                        composable("listado") { 
                            // ListadoScreen manejará su propio padding para subir el título
                            ListadoScreen(navController = navController, innerPadding = innerPadding)
                        }
                        composable("camera") { 
                            // La cámara suele querer ser pantalla completa real
                            DetectionScreen(onBack = { navController.popBackStack() }) 
                        }
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .navigationBarsPadding()
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier.height(80.dp),
                windowInsets = WindowInsets(0, 0, 0, 0)
            ) {
                val homeInteractionSource = remember { MutableInteractionSource() }
                val isHomeSelected = currentDestination?.hierarchy?.any { it.route == "home" } == true
                
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", style = MaterialTheme.typography.labelMedium) },
                    selected = isHomeSelected,
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
                        selectedIconColor = Color.White,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )

                val listadoInteractionSource = remember { MutableInteractionSource() }
                val isListadoSelected = currentDestination?.hierarchy?.any { it.route == "listado" } == true

                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Listado") },
                    label = { Text("Listado", style = MaterialTheme.typography.labelMedium) },
                    selected = isListadoSelected,
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
                        selectedIconColor = Color.White,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}
