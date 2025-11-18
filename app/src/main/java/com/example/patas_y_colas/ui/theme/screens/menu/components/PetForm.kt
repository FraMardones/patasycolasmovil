package com.example.patas_y_colas.ui.theme.screens.menu.components

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast // <--- IMPORTANTE: Necesario para el mensaje
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.patas_y_colas.data.network.TokenManager // <--- IMPORTANTE: Para verificar la sesión
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.model.VaccineRecord
import com.example.patas_y_colas.ui.theme.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Composable
fun PetForm(pet: Pet?, onSave: (Pet) -> Unit, onDelete: (Pet) -> Unit) {
    var name by remember(pet) { mutableStateOf(pet?.name ?: "") }
    var breed by remember(pet) { mutableStateOf(pet?.breed ?: "") }
    var age by remember(pet) { mutableStateOf(pet?.age ?: "") }
    var weight by remember(pet) { mutableStateOf(pet?.weight?.filter { it.isDigit() } ?: "") }
    var imageUri by remember(pet) { mutableStateOf(pet?.imageUri) }

    val initialSpecies = pet?.species
    var speciesOption by remember(pet) { mutableStateOf(if (initialSpecies != null && initialSpecies in listOf("Perro", "Gato")) initialSpecies else "Otro") }
    var otherSpecies by remember(pet) { mutableStateOf(if (initialSpecies != null && initialSpecies !in listOf("Perro", "Gato")) initialSpecies else "") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var breedError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }
    var otherSpeciesError by remember { mutableStateOf<String?>(null) }

    var showVaccineDialog by remember { mutableStateOf(false) }

    // Carga directa desde la lista del objeto Pet
    var currentVaccines by remember(pet) {
        mutableStateOf(pet?.vaccines ?: emptyList())
    }

    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imageUri = copyUriToInternalStorage(context, it)?.toString() }
    }
    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
        if (success) { imageUri = tempImageUri?.toString() }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            val uri = context.createImageUri()
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        nameError = if (name.isBlank() || name.any { it.isDigit() }) "Nombre inválido (solo letras)" else null
        breedError = if (breed.any { it.isDigit() }) "Raza inválida (solo letras)" else null
        ageError = if (age.isBlank() || !age.all { it.isDigit() }) "Edad inválida (solo números)" else null
        weightError = if (weight.isBlank() || !weight.all { it.isDigit() }) "Peso inválido (solo números)" else null
        otherSpeciesError = if (speciesOption == "Otro" && otherSpecies.isBlank()) "Especifique la especie" else null
        return nameError == null && breedError == null && ageError == null && weightError == null && otherSpeciesError == null
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = if (pet == null) "Nueva Mascota" else "Editar a ${pet.name}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally), color = PetTextDark)
            PetTextField(label = "Nombre", value = name, onValueChange = { name = it }, error = nameError)
            Column {
                Text("Especie", color = PetTextLight, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("Perro", "Gato", "Otro").forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = speciesOption == option, onClick = { speciesOption = option }, colors = RadioButtonDefaults.colors(selectedColor = PetTerracotta, unselectedColor = PetTextLight))
                            Text(option, color = PetTextDark)
                        }
                    }
                }
            }
            AnimatedVisibility(visible = speciesOption == "Otro") {
                PetTextField(label = "Especie (Otro)", value = otherSpecies, onValueChange = { otherSpecies = it }, error = otherSpeciesError)
            }
            PetTextField(label = "Raza", value = breed, onValueChange = { breed = it }, error = breedError)
            PetTextField(label = "Edad", value = age, onValueChange = { age = it }, error = ageError, keyboardType = KeyboardType.Number)
            PetTextField(label = "Peso", value = weight, onValueChange = { weight = it }, error = weightError, keyboardType = KeyboardType.Number, suffix = "Kg")

            Button(onClick = { showImageSourceDialog = true }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PetOchre), elevation = null, border = BorderStroke(1.dp, PetOchre)) { Text("Seleccionar Foto") }

            Button(onClick = { showVaccineDialog = true }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PetOchre), elevation = null, border = BorderStroke(1.dp, PetOchre)) { Text("Registro de Vacunas") }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (pet != null) {
                    Button(onClick = { onDelete(pet) }, modifier = Modifier.weight(1f).height(50.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PetRed), border = BorderStroke(1.dp, PetRed)) { Text(text = "Eliminar") }
                }
                Button(
                    onClick = {
                        // --- VALIDACIÓN DE SESIÓN ---
                        if (TokenManager.getAccessToken(context) == null) {
                            Toast.makeText(context, "Debes iniciar sesión para guardar", Toast.LENGTH_LONG).show()
                        } else {
                            // Si hay sesión, procedemos normalmente
                            if (validate()) {
                                val finalSpecies = if (speciesOption == "Otro") otherSpecies else speciesOption

                                // --- CORRECCIÓN 1: Enviar 'null' si la mascota es nueva ---
                                val petToSave = Pet(
                                    id = pet?.id, // <- No uses '?: 0'
                                    name = name,
                                    species = finalSpecies,
                                    breed = breed,
                                    age = age,
                                    weight = "$weight Kg",
                                    imageUri = imageUri,
                                    vaccines = currentVaccines
                                )
                                onSave(petToSave)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = PetTerracotta)
                ) {
                    Text(text = "Guardar", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showVaccineDialog) {
        VaccineRegistrationDialog(initialVaccines = currentVaccines, onDismiss = { showVaccineDialog = false }) { updatedVaccines ->
            currentVaccines = updatedVaccines
            showVaccineDialog = false
        }
    }

    if(showImageSourceDialog) {
        AlertDialog(onDismissRequest = { showImageSourceDialog = false }, title = { Text("Seleccionar Imagen") }, text = { Text("¿Desde dónde quieres obtener la imagen?") }, confirmButton = { Button(onClick = { showImageSourceDialog = false; cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Cámara") } }, dismissButton = { Button(onClick = { showImageSourceDialog = false; imagePickerLauncher.launch("image/*") }) { Text("Galería") } })
    }
}

@Composable
fun VaccineRegistrationDialog(initialVaccines: List<VaccineRecord>, onDismiss: () -> Unit, onSave: (List<VaccineRecord>) -> Unit) {
    val vaccines = remember { initialVaccines.toMutableStateList() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registro de Vacunas", color = PetTextDark) },
        containerColor = PetBackground,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    itemsIndexed(vaccines) { index, vaccine ->
                        var vaccineName by remember { mutableStateOf(vaccine.vaccineName) }
                        var date by remember { mutableStateOf(vaccine.date) }
                        val context = LocalContext.current
                        val calendar = Calendar.getInstance()
                        val datePickerDialog = DatePickerDialog(context, { _, year, month, dayOfMonth ->
                            val newDate = "$dayOfMonth/${month + 1}/$year"
                            date = newDate
                            vaccines[index] = vaccines[index].copy(date = newDate)
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        datePickerDialog.datePicker.minDate = calendar.timeInMillis

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = vaccineName ?: "",
                                onValueChange = { vaccineName = it; vaccines[index] = vaccines[index].copy(vaccineName = it) },
                                label = { Text("Vacuna") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(cursorColor = PetTerracotta, focusedBorderColor = PetTerracotta, unfocusedBorderColor = PetTextLight)
                            )
                            IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.CalendarToday, contentDescription = "Fecha", tint = PetTextLight) }
                            IconButton(onClick = { vaccines.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar Vacuna", tint = PetRed)
                            }
                        }
                        if (!date.isNullOrEmpty()){ Text(date!!, color = PetTextLight, modifier = Modifier.padding(start = 16.dp)) }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // --- CORRECCIÓN 2: Añadir vacuna con 'id = null' explícito ---
                Button(onClick = { vaccines.add(VaccineRecord(id = null, vaccineName = "", date = "")) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PetSand)) { Text("Añadir Vacuna", color = PetTextDark) }
            }
        },
        confirmButton = { Button(onClick = { onSave(vaccines.toList()) }, colors = ButtonDefaults.buttonColors(containerColor = PetTerracotta)) { Text("Guardar", color = Color.White) } },
        dismissButton = { Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PetTextLight)) { Text("Cancelar") } }
    )
}

@Composable
private fun PetTextField(label: String, value: String, onValueChange: (String) -> Unit, error: String? = null, keyboardType: KeyboardType = KeyboardType.Text, suffix: String? = null) {
    Column {
        TextField(
            value = value, onValueChange = onValueChange, label = { Text(label) }, isError = error != null,
            modifier = Modifier.fillMaxWidth(), singleLine = true, shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            trailingIcon = { if(suffix != null) Text(suffix, color = PetTextLight) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = PetBackground, unfocusedContainerColor = PetBackground,
                focusedTextColor = PetTextDark, unfocusedTextColor = PetTextDark,
                cursorColor = PetTerracotta,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent, errorIndicatorColor = Color.Transparent,
                focusedLabelColor = PetTerracotta, unfocusedLabelColor = PetTextLight, errorLabelColor = PetRed
            )
        )
        if (error != null) {
            Text(text = error, color = PetRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }
    }
}

private fun copyUriToInternalStorage(context: Context, sourceUri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
        val file = File(context.filesDir, "images/pet_image_${System.currentTimeMillis()}.jpg")
        file.parentFile?.mkdirs()
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        Uri.fromFile(file)
    } catch (e: Exception) { e.printStackTrace(); null }
}

fun Context.createImageUri(): Uri {
    val file = File(cacheDir, "images/pet_image_${System.currentTimeMillis()}.jpg")
    file.parentFile?.mkdirs()
    return FileProvider.getUriForFile(Objects.requireNonNull(this), "com.example.patas_y_colas.provider", file)
}