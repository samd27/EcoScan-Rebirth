package com.teamecoscan.ecoscanrebirth.ui.view

import android.content.Context
import android.graphics.BitmapFactory
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
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
fun ListadoScreen(navController: NavController, innerPadding: PaddingValues) {
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

    // Hemos eliminado statusBarsPadding() para que puedas controlar la altura exacta manualmente
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 55.dp) // <--- AJUSTA ESTE VALOR para subirlo más (ej: 30.dp)
        .padding(bottom = innerPadding.calculateBottomPadding())
        .padding(horizontal = 16.dp)) {
        
        Text(
            text = "Catálogo de Residuos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Buscar residuo...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Todos", "Organico", "Inorganico").forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        selectedCategory = cat
                    },
                    label = { Text(if(cat == "Organico") "Orgánico" else if(cat == "Inorganico") "Inorgánico" else cat) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                view.playSoundEffect(SoundEffectConstants.CLICK)
                sortAscending = !sortAscending
            }) {
                Icon(Icons.Filled.SwapVert, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (sortAscending) "A-Z" else "Z-A", style = MaterialTheme.typography.labelLarge)
            }
            TextButton(onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                view.playSoundEffect(SoundEffectConstants.CLICK)
                showFilterDialog = true
            }) {
                Icon(Icons.Filled.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Filtros", style = MaterialTheme.typography.labelLarge)
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
            if (sortAscending) filteredResiduos.sortedBy { it.nombre } else filteredResiduos.sortedByDescending { it.nombre }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(sortedResiduos) { residuo ->
                ResiduoCard(residuo = residuo, onClick = { selectedResiduo = residuo })
            }
        }

        if (selectedResiduo != null) {
            Dialog(
                onDismissRequest = { selectedResiduo = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                ResiduoDetailCard(residuo = selectedResiduo!!, onDismiss = { selectedResiduo = null })
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
fun ResiduoCard(residuo: Residuo, onClick: () -> Unit) {
    val context = LocalContext.current
    var imageBitmap by remember(residuo.id) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(residuo.id) { mutableStateOf(true) }

    LaunchedEffect(residuo.id) {
        isLoading = true
        withContext(Dispatchers.IO) {
            val bitmap = try {
                context.assets.open("database/images/${residuo.id}.jpg").use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
            } catch (e: IOException) {
                try { context.assets.open("database/images/${residuo.id}.png").use { BitmapFactory.decodeStream(it)?.asImageBitmap() } } 
                catch (e: IOException) { null }
            }
            imageBitmap = bitmap
            isLoading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                else if (imageBitmap != null) {
                    Image(bitmap = imageBitmap!!, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.padding(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = residuo.nombre, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = residuo.categoria.uppercase(),
                color = if(residuo.categoria.contains("Organico", true)) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ResiduoDetailCard(residuo: Residuo, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color(0xFFF5F5F5)
    var imageBitmap by remember(residuo.id) { mutableStateOf<ImageBitmap?>(null) }
    
    LaunchedEffect(residuo.id) {
        withContext(Dispatchers.IO) {
            val bitmap = try { context.assets.open("database/images/${residuo.id}.jpg").use { BitmapFactory.decodeStream(it)?.asImageBitmap() } }
            catch (e: IOException) { try { context.assets.open("database/images/${residuo.id}.png").use { BitmapFactory.decodeStream(it)?.asImageBitmap() } } catch (e: IOException) { null } }
            imageBitmap = bitmap
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 40.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .background(backgroundColor, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null) { }
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)))
                            .padding(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Detalle del Residuo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                                Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(180.dp).clip(RoundedCornerShape(16.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                            if (imageBitmap != null) Image(bitmap = imageBitmap!!, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.padding(16.dp))
                            else CircularProgressIndicator()
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = residuo.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.height(IntrinsicSize.Max)) {
                                InfoBox(label = "Categoría", value = residuo.categoria, modifier = Modifier.weight(1f))
                                InfoBox(label = "Material", value = residuo.material, modifier = Modifier.weight(1f))
                            }
                            InfoBox(label = "Descripción", value = residuo.descripcion)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun FilterDialog(materials: List<String>, selectedMaterials: Set<String>, onDismissRequest: () -> Unit, onApplyFilters: (Set<String>) -> Unit) {
    var tempSelectedMaterials by remember { mutableStateOf(selectedMaterials) }
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.shadow(12.dp, RoundedCornerShape(24.dp))) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Filtrar por material", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(materials) { material ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {
                            val newSelection = tempSelectedMaterials.toMutableSet()
                            if (newSelection.contains(material)) newSelection.remove(material) else newSelection.add(material)
                            tempSelectedMaterials = newSelection
                        }.padding(vertical = 4.dp)) {
                            Checkbox(checked = tempSelectedMaterials.contains(material), onCheckedChange = { isChecked ->
                                val newSelection = tempSelectedMaterials.toMutableSet()
                                if (isChecked) newSelection.add(material) else newSelection.remove(material)
                                tempSelectedMaterials = newSelection
                            })
                            Text(material, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text("Cancelar") }
                    Button(onClick = { onApplyFilters(tempSelectedMaterials) }, shape = RoundedCornerShape(12.dp)) { Text("Aplicar") }
                }
            }
        }
    }
}

fun parseResiduos(context: Context): List<Residuo> {
    val jsonString = try { context.assets.open("database/residuos.json").bufferedReader().use { it.readText() } } catch (e: Exception) { return emptyList() }
    val jsonArray = JSONArray(jsonString)
    return List(jsonArray.length()) { i ->
        val obj = jsonArray.getJSONObject(i)
        Residuo(obj.getInt("id"), obj.getString("nombre"), obj.getString("categoria"), obj.getString("subcategoria"), obj.getString("material"), obj.getString("submaterial"), obj.getString("descripcion"), obj.getString("keywords"))
    }
}
