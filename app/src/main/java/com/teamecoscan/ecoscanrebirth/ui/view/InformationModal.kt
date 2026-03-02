package com.teamecoscan.ecoscanrebirth.ui.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class ResidueType(
    val name: String,
    val description: String,
    val imageName: String,
    val color: Color
)

@Composable
fun InformationModal(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color(0xFFF5F5F5)
    val textColor = MaterialTheme.colorScheme.onSurface
    val cardBackgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.surfaceVariant else Color.White

    val residueTypes = listOf(
        ResidueType(
            name = "Orgánico",
            description = "Residuos biodegradables como restos de comida, cáscaras de frutas y verduras, residuos de jardinería.",
            imageName = "verde.png",
            color = Color(0xFF4CAF50)
        ),
        ResidueType(
            name = "Inorgánico",
            description = "Residuos que no se pueden reciclar fácilmente o que no entran en las otras categorías específicas.",
            imageName = "gris.png",
            color = Color(0xFF9E9E9E)
        ),
        ResidueType(
            name = "Papel",
            description = "Hojas de papel, cuadernos, periódicos, revistas, cajas de cartón (desarmadas).",
            imageName = "amarillo.png",
            color = Color(0xFFFFC107)
        ),
        ResidueType(
            name = "Metal",
            description = "Latas de aluminio (refrescos), latas de conserva (atún, verduras), papel aluminio limpio.",
            imageName = "azul claro.png",
            color = Color(0xFF2196F3)
        ),
        ResidueType(
            name = "Vidrio",
            description = "Botellas de vidrio, frascos de mermelada o salsas. Sin tapas y sin romper.",
            imageName = "menta.png",
            color = Color(0xFF009688)
        ),
        ResidueType(
            name = "Plástico",
            description = "Botellas de PET, envases de detergente, bolsas de plástico limpias. Vacíos y aplastados.",
            imageName = "azul obsscuro.png",
            color = Color(0xFF1A237E)
        ),
        ResidueType(
            name = "Madera",
            description = "Restos de madera, palitos de paleta, lápices de madera, cajas de fruta de madera.",
            imageName = "cafe.png",
            color = Color(0xFF6D4C41)
        ),
        ResidueType(
            name = "Tela",
            description = "Ropa vieja, retazos de tela, trapos limpios.",
            imageName = "rosa.png",
            color = Color(0xFFE91E63)
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 40.dp)
                    .background(backgroundColor, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() },
                        indication = null) { }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header elegante
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Aprende sobre Residuos",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Cerrar",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Guía de Clasificación",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Conoce cómo separar correctamente cada tipo de residuo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(thickness = 1.dp, color = textColor.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(20.dp))

                        // Grid de tarjetas
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            for (i in residueTypes.indices step 2) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Max), // Importante para que midan lo mismo en la fila
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ResidueCard(
                                        residue = residueTypes[i],
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        cardBackgroundColor = cardBackgroundColor,
                                        textColor = textColor
                                    )

                                    if (i + 1 < residueTypes.size) {
                                        ResidueCard(
                                            residue = residueTypes[i + 1],
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight(),
                                            cardBackgroundColor = cardBackgroundColor,
                                            textColor = textColor
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ResidueCard(
    residue: ResidueType,
    modifier: Modifier = Modifier,
    cardBackgroundColor: Color = Color.White,
    textColor: Color = Color.Black
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val imageBitmap = remember(residue.imageName) {
                try {
                    context.assets.open("info_r/${residue.imageName}").use {
                        BitmapFactory.decodeStream(it)?.asImageBitmap()
                    }
                } catch (e: Exception) {
                    null
                }
            }

            // Contenedor del icono con diseño circular y borde
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(residue.color.copy(alpha = 0.1f), CircleShape)
                    .border(2.dp, residue.color.copy(alpha = 0.5f), CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = residue.name,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = residue.name.first().toString(),
                        color = residue.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = residue.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = residue.color, // Usamos el color del residuo para el nombre
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = residue.description,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}
