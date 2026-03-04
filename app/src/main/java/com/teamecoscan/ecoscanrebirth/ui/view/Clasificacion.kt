package com.teamecoscan.ecoscanrebirth.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class TipoContenedor(
    val titulo: String,
    val colorUI: Color,
    val icono: ImageVector
) {
    ORGANICO(
        "Orgánicos",
        Color(0xFF8BC34A), // Verde
        Icons.Outlined.Spa
    ),
    INORGANICO_RECICLABLE(
        "Inorgánicos Reciclables",
        Color(0xFF9E9E9E), // Gris
        Icons.Filled.Recycling
    ),
    INORGANICO_NO_RECICLABLE(
        "Inorgánicos No Reciclables",
        Color(0xFFFF9800), // Naranja
        Icons.Filled.Delete
    ),
    MANEJO_ESPECIAL(
        "Manejo Especial",
        Color(0xFFF44336), // Rojo
        Icons.Filled.Warning
    ),
    DESCONOCIDO(
        "Analizando...",
        Color(0xFF607D8B),
        Icons.Filled.Search
    )
}

data class InfoResiduo(
    val nombreTraducido: String,
    val contenedor: TipoContenedor,
    val consejoExtra: String = "",
    val confidence: Float = 0f
)

object ClasificadorEcoScan {

    fun obtenerInfoContenedor(etiqueta: String): InfoResiduo {
        return when (etiqueta.lowercase()) {
            "biodegradable" -> InfoResiduo("Residuo Orgánico", TipoContenedor.ORGANICO, "Desechar en contenedor de orgánicos. Retira cualquier etiqueta adhesiva y escurre el exceso de líquido.")
            "glass" -> InfoResiduo("Vidrio", TipoContenedor.INORGANICO_RECICLABLE, "Desechar en contenedor para vidrio o inorgánicos. Retira tapas y corchos, y enjuaga el interior.")
            "metal" -> InfoResiduo("Metal / Lata", TipoContenedor.INORGANICO_RECICLABLE, "Desechar en contenedor para metales. Vacía el contenido, enjuaga ligeramente y aplasta la lata.")
            "paper" -> InfoResiduo("Papel / Cartón", TipoContenedor.INORGANICO_RECICLABLE, "Desechar en contenedor para papel. Asegúrate de que esté seco, sin grasa y retira cualquier cinta adhesiva o grapas.")
            "plastic" -> InfoResiduo("Plástico", TipoContenedor.INORGANICO_RECICLABLE, "Desechar en contenedor para plásticos PET. Vacía el líquido, aplasta la botella y deposita la tapa por separado.")
            "textil" -> InfoResiduo("Textil / Ropa", TipoContenedor.MANEJO_ESPECIAL, "Si está en buen estado, considera donarla. Si es retacería o trapos viejos, llévalos a puntos de reciclaje textil específicos.")
            "wood" -> InfoResiduo("Madera", TipoContenedor.ORGANICO, "La madera natural es biodegradable. Si está barnizada, pintada o tratada químicamente, debe ir a centros de manejo especial o escombro.")
            else -> InfoResiduo("Basura General", TipoContenedor.INORGANICO_NO_RECICLABLE, "Residuo mixto o no reciclable. Se irá al relleno sanitario.")
        }
    }
}

@Composable
fun TarjetaInstruccion(info: InfoResiduo, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (info.contenedor != TipoContenedor.DESCONOCIDO) {
                val confidencePercentage = (info.confidence * 100).toInt()
                Text(
                    text = "Detectado: ${info.nombreTraducido} ($confidencePercentage%)",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Surface(
                color = info.contenedor.colorUI.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = info.contenedor.icono,
                        contentDescription = info.contenedor.titulo,
                        modifier = Modifier.size(32.dp),
                        tint = info.contenedor.colorUI
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = info.contenedor.titulo,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = info.contenedor.colorUI
                    )
                }
            }

            if (info.consejoExtra.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = info.consejoExtra,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
