package com.teamecoscan.ecoscanrebirth.ui.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.teamecoscan.ecoscanrebirth.data.PreferencesManager
import com.teamecoscan.ecoscanrebirth.ui.theme.EcoScanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, showWelcomeModal: androidx.compose.runtime.MutableState<Boolean>? = null) {
    val activity = LocalContext.current as? Activity
    val context = LocalContext.current
    val view = LocalView.current

    val preferencesManager = remember(Unit) { PreferencesManager(context) }
    val showInformationModal = remember { mutableStateOf(false) }
    val welcomeModalState = showWelcomeModal ?: remember(Unit) { mutableStateOf(preferencesManager.shouldShowWelcome()) }
    
    // Easter Egg States
    var clickCount by remember { mutableIntStateOf(0) }
    var showEasterEgg by remember { mutableStateOf(false) }

    val imageBitmap = remember(Unit) {
        try {
            context.assets.open("app_icon/Icono_.png").use {
                BitmapFactory.decodeStream(it).asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    windowInsets = TopAppBarDefaults.windowInsets.exclude(WindowInsets.safeDrawing),
                    title = {
                        Box(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    clickCount++
                                    if (clickCount == 3) {
                                        Toast.makeText(context, "¡Advertencia! Estás intentando despertar a un Dios...", Toast.LENGTH_SHORT).show()
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    } else if (clickCount >= 5) {
                                        showEasterEgg = true
                                        clickCount = 0
                                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                    } else {
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    }
                                }
                        ) {
                            if (imageBitmap != null) {
                                Image(
                                    bitmap = imageBitmap,
                                    contentDescription = "Logo",
                                    modifier = Modifier.height(100.dp),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                )
                            } else {
                                Text("EcoScan", fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            activity?.finish()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Salir",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Hola,",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "¡Cuidemos el planeta!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                InformationCard(onOpenModal = { showInformationModal.value = true })
                
                Spacer(modifier = Modifier.weight(1f))
                
                ScanSection { navController.navigate("camera") }
                
                Spacer(modifier = Modifier.weight(1.2f))
            }
        }

        if (showInformationModal.value) {
            InformationModal(onDismiss = { showInformationModal.value = false })
        }

        if (welcomeModalState.value) {
            WelcomeModal(
                onDismiss = { welcomeModalState.value = false },
                onDontShowAgain = {
                    preferencesManager.setWelcomeShown()
                    welcomeModalState.value = false
                }
            )
        }
        
        if (showEasterEgg) {
            EasterEggModal(onDismiss = { showEasterEgg = false })
        }
    }
}

@Composable
fun EasterEggModal(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uriHandler = LocalUriHandler.current
    val gameUrl = "https://dragon-chess-production.up.railway.app/"
    
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    // CONTROL TOTAL DEL HARDWARE DE VIBRACIÓN
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    val timings = longArrayOf(0, 150, 100)
                    val amplitudes = intArrayOf(0, 255, 0)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, 0))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(timings, 0)
                    }
                }
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> {
                    // SILENCIAR AL SALIR O MINIMIZAR
                    vibrator.cancel()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            vibrator.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Animación de borde "eléctrico" rotativo
    val infiniteTransition = rememberInfiniteTransition(label = "electric")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val electricBrush = Brush.sweepGradient(
        listOf(
            Color(0xFFFF6D00), // Naranja
            Color(0xFF9C27B0), // Morado
            Color(0xFFFF6D00)
        )
    )

    val billsBitmap = remember {
        try {
            context.assets.open("Bills_muychistoso.webp").use {
                BitmapFactory.decodeStream(it)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF1A1225))
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = electricBrush,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6.dp.toPx())
                        )
                    }
                    .border(3.dp, Color(0xFFFF6D00).copy(alpha = 0.5f), RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Outlined.Close, "Cerrar", tint = Color.White)
                        }
                    }

                    Text(
                        text = "DRAGON",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "CHESS",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF6D00),
                        letterSpacing = 4.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (billsBitmap != null) {
                        Image(
                            bitmap = billsBitmap,
                            contentDescription = "Bills",
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { 
                                    vibrator.cancel()
                                    onDismiss() 
                                    uriHandler.openUri(gameUrl) 
                                },
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "¡Juega Dragon Chess Ya!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "El campo de batalla definitivo te espera.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { 
                            vibrator.cancel()
                            onDismiss()
                            uriHandler.openUri(gameUrl) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFFF6D00)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ENTRAR A BATALLA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp).rotate(45f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InformationCard(onOpenModal: () -> Unit) {
    val view = LocalView.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onOpenModal()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
            
            Column(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)) {
                Text(
                    text = "Centro de Información",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Aprende a clasificar tus residuos de forma experta.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Arrow",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ScanSection(onScanClick: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onScanClick()
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Toca para escanear",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(195.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
            )
            
            FloatingActionButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) -> {
                            onScanClick()
                        }
                        else -> {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(160.dp)
                    .shadow(16.dp, CircleShape),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "Scan",
                    modifier = Modifier.size(80.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Usa la IA para identificar materiales",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    EcoScanTheme {
        HomeScreen(rememberNavController())
    }
}
