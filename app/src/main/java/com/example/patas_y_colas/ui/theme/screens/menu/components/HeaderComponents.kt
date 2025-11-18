package com.example.patas_y_colas.ui.screens.menu.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.HelpOutline // <-- 1. AÑADIR ESTE IMPORT
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.ui.theme.*

@Composable
fun HeaderTopBar(
    onLogoutClicked: () -> Unit,
    onShowFactClicked: () -> Unit, // <-- 2. AÑADIR PARÁMETRO
    userName: String?
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tu lógica de "Hola, [Usuario]"
        Text(
            text = "Hola, ${userName ?: "Bienvenido"}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Box {
            // Tu icono de tuerca (Settings)
            IconButton(
                onClick = { showMenu = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Tu menú desplegable
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color.White)
            ) {
                // --- 3. AÑADIR NUEVO BOTÓN DE MENÚ (CON TU ESTILO) ---
                DropdownMenuItem(
                    text = { Text("Dato Curioso", color = PetTextDark) }, // Usa tu color
                    onClick = {
                        onShowFactClicked() // Llama a la función
                        showMenu = false    // Cierra el menú
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.HelpOutline,
                            contentDescription = "Dato Curioso",
                            tint = PetTextDark // Usa tu color
                        )
                    }
                )

                // --- Tu botón de Cerrar Sesión (existente, sin cambios) ---
                DropdownMenuItem(
                    text = { Text("Cerrar Sesión", color = PetRed) },
                    onClick = onLogoutClicked,
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = PetRed
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onShowFactClicked: () -> Unit, // <-- 4. AÑADIR PARÁMETRO
    userName: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
            .background(PetSageGreen) // Tu color de cabecera
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 24.dp)
        ) {

            // --- 5. PASAR EL PARÁMETRO ---
            HeaderTopBar(
                onLogoutClicked = onLogoutClicked,
                onShowFactClicked = onShowFactClicked, // <-- Pasarlo aquí
                userName = userName
            )

            // Tu subtítulo (sin cambios)
            Text(
                text = "Gestiona a tus mascotas",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Tu selector de mascotas (sin cambios)
            PetSelector(
                pets = pets,
                selectedPet = selectedPet,
                onPetSelected = onPetSelected,
                onAddPetClicked = onAddPetClicked
            )
        }
    }
}

// --- NINGÚN CAMBIO DE AQUÍ EN ADELANTE (Tu estilo original) ---

@Composable
fun PetSelector(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        // Tu lógica de selección original
        item { AddPetCircle(isSelected = selectedPet == null && pets.isNotEmpty(), onClick = onAddPetClicked) }
        items(pets) { pet -> PetCircle(pet = pet, isSelected = pet == selectedPet, onClick = { onPetSelected(pet) }) }
    }
}

@Composable
fun AddPetCircle(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PetHighlightBlue else Color.White.copy(alpha = 0.2f)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .border(3.dp, borderColor, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Mascota", modifier = Modifier.size(40.dp), tint = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Agregar", color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PetCircle(
    pet: Pet,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (pet.species.lowercase()) {
        "perro" -> Icons.Filled.Pets
        "gato" -> Icons.Filled.Favorite
        else -> Icons.Filled.Star
    }
    val borderColor = if (isSelected) PetHighlightBlue else Color.White.copy(alpha = 0.5f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(PetBackground)
                .border(3.dp, borderColor, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (pet.imageUri != null) {
                AsyncImage(
                    model = Uri.parse(pet.imageUri),
                    contentDescription = pet.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(imageVector = icon, contentDescription = pet.name, modifier = Modifier.size(40.dp), tint = PetOchre)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = pet.name,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}