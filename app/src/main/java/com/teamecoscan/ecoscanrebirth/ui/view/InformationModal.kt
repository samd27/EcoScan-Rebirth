package com.teamecoscan.ecoscanrebirth.ui.view

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

    // Colores adaptativos según el tema
    val backgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color(0xFFE8E8E8)
    val textColor = MaterialTheme.colorScheme.onSurface
    val cardBackgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.surfaceVariant else Color.White

    val residueTypes = listOf(
        ResidueType(
            name = "orgánico",
            description = "Residuos biodegradables como restos de comida, cáscaras de frutas y verduras, residuos de jardinería.",
            imageName = "verde.png",
            color = Color(0xFF4CAF50)
        ),
        ResidueType(
            name = "inorgánico",
            description = "Residuos que no se pueden reciclar fácilmente o que no entran en las otras categorías específicas.",
            imageName = "gris.png",
            color = Color(0xFF9E9E9E)
        ),
        ResidueType(
            name = "papel",
            description = "Hojas de papel, cuadernos, periódicos, revistas, cajas de cartón (desarmadas).",
            imageName = "amarillo.png",
            color = Color(0xFFFFC107)
        ),
        ResidueType(
            name = "metal",
            description = "Latas de aluminio (refrescos), latas de conserva (atún, verduras), papel aluminio limpio.",
            imageName = "azul claro.png",
            color = Color(0xFF2196F3)
        ),
        ResidueType(
            name = "vidrio",
            description = "Botellas de vidrio, frascos de mermelada o salsas. Sin tapas y sin romper.",
            imageName = "menta.png",
            color = Color(0xFF009688)
        ),
        ResidueType(
            name = "plástico",
            description = "Botellas de PET, envases de detergente, bolsas de plástico limpias. Vacíos y aplastados.",
            imageName = "azul obsscuro.png",
            color = Color(0xFF1A237E)
        ),
        ResidueType(
            name = "madera",
            description = "Restos de madera, palitos de paleta, lápices de madera, cajas de fruta de madera.",
            imageName = "cafe.png",
            color = Color(0xFF6D4C41)
        ),
        ResidueType(
            name = "tela",
            description = "Ropa vieja, retazos de tela, trapos limpios.",
            imageName = "rosa.png",
            color = Color(0xFFE91E63)
        )
    )

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
                .background(backgroundColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null) { }
                .verticalScroll(rememberScrollState())
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aprende sobre Residuos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier.size(24.dp),
                        tint = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title section
            Text(
                text = "Tipos de Contenedores: Colores y su Significado",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of residue types
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in residueTypes.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // First item
                        ResidueCard(
                            residue = residueTypes[i],
                            modifier = Modifier.weight(1f),
                            cardBackgroundColor = cardBackgroundColor,
                            textColor = textColor
                        )

                        // Second item (if exists)
                        if (i + 1 < residueTypes.size) {
                            ResidueCard(
                                residue = residueTypes[i + 1],
                                modifier = Modifier.weight(1f),
                                cardBackgroundColor = cardBackgroundColor,
                                textColor = textColor
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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

    Column(
        modifier = modifier
            .background(cardBackgroundColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Image icon with error handling
        val imageBitmap = remember(residue.imageName) {
            try {
                context.assets.open("info_r/${residue.imageName}").use {
                    BitmapFactory.decodeStream(it)?.asImageBitmap()
                }
            } catch (e: Exception) {
                android.util.Log.e("ResidueCard", "Error loading image ${residue.imageName}: ${e.message}")
                null
            }
        }

        Box(
            modifier = Modifier
                .size(70.dp)
                .background(residue.color, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = residue.name,
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                // Fallback: muestra un cuadrado vacío si la imagen no carga
                Text(
                    text = residue.name.first().uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Name
        Text(
            text = residue.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = residue.description,
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}














