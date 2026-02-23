package com.teamecoscan.ecoscanrebirth.ui.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.teamecoscan.ecoscanrebirth.ui.theme.EcoScanTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException

data class Residuo(
    val id: Int,
    val nombre: String,
    val categoria: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoScreen(navController: NavController) {
    val context = LocalContext.current
    val residuos = remember { parseResiduos(context) }
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var sortAscending by remember { mutableStateOf(true) }
    var selectedResiduo by remember { mutableStateOf<Residuo?>(null) }

    Column(modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Buscar") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = selectedCategory == "Orgánico",
                onClick = { selectedCategory = if (selectedCategory == "Orgánico") "Todos" else "Orgánico" },
                label = { Text("Orgánico") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = selectedCategory == "Todos",
                onClick = { selectedCategory = "Todos" },
                label = { Text("Todos") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = selectedCategory == "Inorgánico",
                onClick = { selectedCategory = if (selectedCategory == "Inorgánico") "Todos" else "Inorgánico" },
                label = { Text("Inorgánico") }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { sortAscending = !sortAscending }) {
                Icon(Icons.Filled.SwapVert, contentDescription = "Ordenar A-Z")
                Text(if (sortAscending) "A-Z" else "Z-A")
            }
            TextButton(onClick = {}) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filtros")
                Text("Filtros")
            }
        }

        val filteredResiduos = remember(selectedCategory, searchText) {
            residuos.filter {
                (selectedCategory == "Todos" || it.categoria.trim().equals(selectedCategory, ignoreCase = true)) &&
                        (searchText.isBlank() || it.nombre.contains(searchText, ignoreCase = true))
            }
        }

        val sortedResiduos = remember(filteredResiduos, sortAscending) {
            if (sortAscending) {
                filteredResiduos.sortedBy { it.nombre }
            } else {
                filteredResiduos.sortedByDescending { it.nombre }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sortedResiduos, key = { residuo -> residuo.id }) { residuo -> // Added key for performance
                ResiduoCard(residuo = residuo, onClick = { selectedResiduo = residuo })
            }
        }

        if (selectedResiduo != null) {
            Dialog(onDismissRequest = { selectedResiduo = null }) {
                ResiduoDetailCard(residuo = selectedResiduo!!)
            }
        }
    }
}

@Composable
fun ResiduoCard(residuo: Residuo, onClick: () -> Unit) {
    val context = LocalContext.current
    var imageBitmap by remember(residuo.id) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(residuo.id) { mutableStateOf(true) }

    // Load image asynchronously to avoid UI lag
    LaunchedEffect(residuo.id) {
        isLoading = true
        withContext(Dispatchers.IO) {
            val bitmap = try {
                context.assets.open("database/images/${residuo.id}.jpg").use {
                    android.graphics.BitmapFactory.decodeStream(it)?.asImageBitmap()
                }
            } catch (e: IOException) {
                try {
                    context.assets.open("database/images/${residuo.id}.png").use {
                        android.graphics.BitmapFactory.decodeStream(it)?.asImageBitmap()
                    }
                } catch (e: IOException) {
                    null
                }
            }
            imageBitmap = bitmap
            isLoading = false
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = residuo.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = residuo.nombre,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            val categoryColor = if (residuo.categoria.trim().equals("Orgánico", ignoreCase = true) || residuo.categoria.trim().equals("Inorgánico", ignoreCase = true)) {
                Color(0xFF4CAF50) // Specific green color
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = residuo.categoria.uppercase(),
                color = categoryColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ResiduoDetailCard(residuo: Residuo) {
    val context = LocalContext.current
    var imageBitmap by remember(residuo.id) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(residuo.id) { mutableStateOf(true) }

    LaunchedEffect(residuo.id) {
        isLoading = true
        withContext(Dispatchers.IO) {
            val bitmap = try {
                context.assets.open("database/images/${residuo.id}.jpg").use {
                    android.graphics.BitmapFactory.decodeStream(it)?.asImageBitmap()
                }
            } catch (e: IOException) {
                try {
                    context.assets.open("database/images/${residuo.id}.png").use {
                        android.graphics.BitmapFactory.decodeStream(it)?.asImageBitmap()
                    }
                } catch (e: IOException) {
                    null
                }
            }
            imageBitmap = bitmap
            isLoading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = residuo.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = residuo.nombre,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            val categoryColor = if (residuo.categoria.trim().equals("Orgánico", ignoreCase = true) || residuo.categoria.trim().equals("Inorgánico", ignoreCase = true)) {
                Color(0xFF4CAF50) // Specific green color
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = residuo.categoria.uppercase(),
                color = categoryColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            // Add more details here if needed
        }
    }
}

fun parseResiduos(context: Context): List<Residuo> {
    val jsonString: String
    try {
        jsonString = context.assets.open("database/residuos.json").bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return emptyList()
    }
    val jsonArray = JSONArray(jsonString)
    val residuos = mutableListOf<Residuo>()
    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        residuos.add(
            Residuo(
                id = jsonObject.getInt("id"),
                nombre = jsonObject.getString("nombre"),
                categoria = jsonObject.getString("categoria")
            )
        )
    }
    return residuos
}

@Preview(showBackground = true)
@Composable
fun ListadoScreenPreview() {
    EcoScanTheme {
        ListadoScreen(rememberNavController())
    }
}
