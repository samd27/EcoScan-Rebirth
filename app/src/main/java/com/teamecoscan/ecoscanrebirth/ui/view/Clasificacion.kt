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
import androidx.compose.ui.unit.sp

// 1. Los 4 contenedores oficiales según la SEMARNAT
enum class TipoContenedor(
    val titulo: String,
    val colorUI: Color,
    val icono: ImageVector,
    val descripcion: String
) {
    ORGANICO(
        "Orgánicos",
        Color(0xFF8BC34A), // Verde
        Icons.Outlined.Spa,
        "Todo desecho de origen biológico que alguna vez estuvo vivo o fue parte de un ser vivo."
    ),
    INORGANICO_RECICLABLE(
        "Inorgánicos Reciclables",
        Color(0xFF9E9E9E), // Gris
        Icons.Filled.Recycling,
        "Desechos que no son biológicos pero son reutilizables y reciclables. ¡No los mezcles para no arruinarlos!"
    ),
    INORGANICO_NO_RECICLABLE(
        "Inorgánicos No Reciclables",
        Color(0xFFFF9800), // Naranja
        Icons.Filled.Delete,
        "Desechos inorgánicos difíciles de aprovechar (sanitarios, envolturas mixtas o unicel). Se convierten en basura."
    ),
    MANEJO_ESPECIAL(
        "Manejo Especial",
        Color(0xFFF44336), // Rojo
        Icons.Filled.Warning,
        "Residuos que requieren un plan de manejo específico. Llévalos a un centro de acopio autorizado."
    ),
    DESCONOCIDO(
        "Analizando...",
        Color(0xFF607D8B),
        Icons.Filled.Search,
        "Acerca la cámara al residuo."
    )
}

// 2. Estructura de datos para enviar a la UI
data class InfoResiduo(
    val nombreYolo: String,
    val contenedor: TipoContenedor,
    val consejoExtra: String = ""
)

// 3. El Cerebro: Clasificación exacta de tus 59 clases de YOLO
object ClasificadorEcoScan {

    fun obtenerInfoContenedor(etiqueta: String): InfoResiduo {
        return when (etiqueta) {
            // --- ORGÁNICOS ---
            "Residuo orgánico" ->
                InfoResiduo(etiqueta, TipoContenedor.ORGANICO, "¡Ideal para hacer composta en casa!")

            // --- INORGÁNICOS RECICLABLES ---
            "Botella de plástico transparente", "Otra botella de plástico", "Botella de vidrio",
            "Vaso de vidrio", "Frasco de vidrio", "Lata de bebida", "Lata de comida",
            "Cartón corrugado", "Envase Tetra Pak", "Cartón de huevos", "Caja de cartón (Comida)",
            "Otro cartón", "Papel de revista", "Papel normal", "Bolsa de papel",
            "Tubo de papel higiénico", "Caja de pizza", "Corcholata / Tapa de metal",
            "Tapadera de metal", "Anilla de lata", "Papel aluminio", "Chatarra de metal",
            "Tapa de plástico", "Tapadera de plástico", "Otro plástico" ->
                InfoResiduo(etiqueta, TipoContenedor.INORGANICO_RECICLABLE, "Asegúrate de que esté limpio y seco antes de tirarlo.")

            // --- RESIDUOS DE MANEJO ESPECIAL / PELIGROSOS ---
            "Pila / Batería", "Aerosol" ->
                InfoResiduo(etiqueta, TipoContenedor.MANEJO_ESPECIAL, "¡Peligro de contaminación! No lo tires en la basura normal.")

            // --- INORGÁNICOS NO RECICLABLES ---
            "Vaso de unicel", "Envase de unicel (Comida)", "Pedazo de unicel",
            "Bolsa de frituras", "Cigarro", "Pañuelos desechables", "Guantes de plástico",
            "Cubiertos de plástico", "Popote de plástico", "Popote de papel", "Vidrio roto",
            "Otra envoltura de plástico", "Blíster de aluminio", "Blíster de cartón",
            "Envase desechable (Comida)", "Vaso de plástico", "Otro vaso de plástico",
            "Otro envase de plástico", "Plástico para envolver (Playo)", "Bolsa de polipropileno",
            "Cuerda / Hilo", "Zapato", "Bolsa de plástico (Súper)", "Anillos de plástico (Six-pack)",
            "Tarrina / Envase de untable", "Tubo exprimible", "Tupperware / Refractario",
            "Basura sin clasificar", "Papel de regalo", "Vaso de papel", "Bolsa de basura" ->
                InfoResiduo(etiqueta, TipoContenedor.INORGANICO_NO_RECICLABLE, "Este material tarda cientos de años en degradarse en un relleno sanitario.")

            // Por seguridad, cualquier cosa no reconocida se va a No Reciclable
            else -> InfoResiduo(etiqueta, TipoContenedor.INORGANICO_NO_RECICLABLE)
        }
    }
}

// 4. El Componente Visual (UI) de Jetpack Compose
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
                Text(
                    text = "Detectado: ${info.nombreYolo}",
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = info.contenedor.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            if (info.consejoExtra.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = info.consejoExtra,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
