package com.teamecoscan.ecoscanrebirth.ui.view

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeModal(onDismiss: () -> Unit, onDontShowAgain: () -> Unit) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color.White
    val textColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary

    // Cargar imágenes una sola vez y cachearlas
    val logoBitmap = remember(isDarkTheme) {
        try {
            val imageName = if (isDarkTheme) "bienvenida/Icono_PrimerPlano_obscuro.png" else "bienvenida/Icono_PrimerPlano_claro.png"
            context.assets.open(imageName).use {
                BitmapFactory.decodeStream(it)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    val escudoBitmap = remember(Unit) {
        try {
            context.assets.open("bienvenida/Escudo Positivo.png").use {
                BitmapFactory.decodeStream(it)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = null) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(backgroundColor, RoundedCornerShape(20.dp))
                .padding(24.dp)
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null) { }
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón de cerrar en la esquina superior derecha
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier.size(24.dp),
                        tint = textColor
                    )
                }
            }

            // Título de bienvenida
            Text(
                text = "¡Bienvenido a\nEcoScan!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Logo principal - MÁS GRANDE

            if (logoBitmap != null) {
                Image(
                    bitmap = logoBitmap,
                    contentDescription = "EcoScan Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 10.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción principal
            Text(
                text = "¡Hola! Estás a punto de usar EcoScan, una herramienta revolucionaria que combina inteligencia artificial y visión por computadora para transformar la forma en que clasificamos nuestros residuos.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Descripción secundaria
            Text(
                text = "Mediante tecnología de visión artificial, nuestra aplicación identifica automáticamente el tipo de residuo y te proporciona las mejores prácticas para su separación correcta. Aprende, escanea y mejora nuestro entorno juntos.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Información del proyecto
            Text(
                text = "Un proyecto hecho con orgullo por estudiantes de la Facultad de Ciencias de la Computación, BUAP.",
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp),
                fontSize = 12.sp
            )

            // Escudo de la Universidad - MÁS GRANDE

            if (escudoBitmap != null) {
                Image(
                    bitmap = escudoBitmap,
                    contentDescription = "BUAP Logo",
                    modifier = Modifier
                        .size(190.dp)
                        .padding(bottom = 20.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón "No mostrar de nuevo"
                TextButton(
                    onClick = onDontShowAgain,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = primaryColor
                    )
                ) {
                    Text(
                        text = "No mostrar\nde nuevo",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    )
                }

                // Botón "Entendido"
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Entendido",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}



