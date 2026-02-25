package com.teamecoscan.ecoscanrebirth.ui.view

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    val subcategoria: String,
    val material: String,
    val submaterial: String,
    val descripcion: String,
    val keywords: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoScreen(navController: NavController) {
    val context = LocalContext.current
    val view = LocalView.current
    val residuos = remember { parseResiduos(context) }
    val materials = remember { residuos.map { it.material }.distinct().sorted() }
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var sortAscending by remember { mutableStateOf(true) }
    var selectedResiduo by remember { mutableStateOf<Residuo?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedMaterials by remember { mutableStateOf<Set<String>>(emptySet()) }

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
                selected = selectedCategory == "Organico",
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    selectedCategory = if (selectedCategory == "Organico") "Todos" else "Organico"
                },
                label = { Text("Orgánico") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = selectedCategory == "Todos",
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    selectedCategory = "Todos"
                },
                label = { Text("Todos") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = selectedCategory == "Inorganico",
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    selectedCategory = if (selectedCategory == "Inorganico") "Todos" else "Inorganico"
                },
                label = { Text("Inorgánico") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50)
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                view.playSoundEffect(SoundEffectConstants.CLICK)
                sortAscending = !sortAscending
            }) {
                Icon(Icons.Filled.SwapVert, contentDescription = "Ordenar A-Z")
                Text(if (sortAscending) "A-Z" else "Z-A")
            }
            TextButton(onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                view.playSoundEffect(SoundEffectConstants.CLICK)
                showFilterDialog = true
            }) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filtros")
                Text("Filtros")
            }
        }

        val filteredResiduos = remember(selectedCategory, searchText, selectedMaterials) {
            residuos.filter {
                (selectedCategory == "Todos" || it.categoria.trim().equals(selectedCategory, ignoreCase = true)) &&
                        (searchText.isBlank() || it.nombre.contains(searchText, ignoreCase = true)) &&
                        (selectedMaterials.isEmpty() || selectedMaterials.contains(it.material))
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
            Dialog(
                onDismissRequest = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    selectedResiduo = null
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                ResiduoDetailCard(residuo = selectedResiduo!!)
            }
        }

        if (showFilterDialog) {
            FilterDialog(
                materials = materials,
                selectedMaterials = selectedMaterials,
                onDismissRequest = { showFilterDialog = false },
                onApplyFilters = {
                    selectedMaterials = it
                    showFilterDialog = false
                }
            )
        }
    }
}

@Composable
fun FilterDialog(
    materials: List<String>,
    selectedMaterials: Set<String>,
    onDismissRequest: () -> Unit,
    onApplyFilters: (Set<String>) -> Unit
) {
    var tempSelectedMaterials by remember { mutableStateOf(selectedMaterials) }
    val view = LocalView.current

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Filtrar por material", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1.0f)) {
                    items(materials) { material ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Toggle selection */
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    val newSelection = tempSelectedMaterials.toMutableSet()
                                    if (newSelection.contains(material)) {
                                        newSelection.remove(material)
                                    } else {
                                        newSelection.add(material)
                                    }
                                    tempSelectedMaterials = newSelection
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = tempSelectedMaterials.contains(material),
                                onCheckedChange = { isChecked ->
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    val newSelection = tempSelectedMaterials.toMutableSet()
                                    if (isChecked) {
                                        newSelection.add(material)
                                    } else {
                                        newSelection.remove(material)
                                    }
                                    tempSelectedMaterials = newSelection
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(material)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        onDismissRequest()
                    }) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        onApplyFilters(tempSelectedMaterials)
                    }) {
                        Text("Aplicar")
                    }
                }
            }
        }
    }
}


@Composable
fun ResiduoCard(residuo: Residuo, onClick: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
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
            .clickable(onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onClick()
            }),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
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
                        contentScale = ContentScale.Fit,
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
            val categoryColor = if (residuo.categoria.trim().equals("Organico", ignoreCase = true) || residuo.categoria.trim().equals("Inorganico", ignoreCase = true)) {
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
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
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
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = residuo.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bentobox layout
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBox(label = "Categoría", value = residuo.categoria, modifier = Modifier.weight(1f))
                        InfoBox(label = "Subcategoría", value = residuo.subcategoria, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBox(label = "Material", value = residuo.material, modifier = Modifier.weight(1f))
                        if (residuo.submaterial.isNotBlank()) {
                            InfoBox(label = "Submaterial", value = residuo.submaterial, modifier = Modifier.weight(1f))
                        }
                    }
                    InfoBox(label = "Descripción", value = residuo.descripcion)
                }
            }
        }
    }
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
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
                categoria = jsonObject.getString("categoria"),
                subcategoria = jsonObject.getString("subcategoria"),
                material = jsonObject.getString("material"),
                submaterial = jsonObject.getString("submaterial"),
                descripcion = jsonObject.getString("descripcion"),
                keywords = jsonObject.getString("keywords")
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
