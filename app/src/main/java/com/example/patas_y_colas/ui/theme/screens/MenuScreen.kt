package com.example.patas_y_colas.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
// --- Imports Añadidos ---
import androidx.navigation.NavGraph.Companion.findStartDestination // <-- Importado para el logout
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
// --- Fin Imports ---
import com.example.patas_y_colas.PetApplication
import com.example.patas_y_colas.data.network.TokenManager // <-- IMPORTADO PARA EL NOMBRE
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.model.VaccineRecord // Importa VaccineRecord
import com.example.patas_y_colas.ui.screens.menu.components.HeaderSection
import com.example.patas_y_colas.ui.theme.screens.menu.components.PetForm
import com.example.patas_y_colas.ui.theme.*
import com.example.patas_y_colas.ui.utils.rememberWindowSizeClass
import com.example.patas_y_colas.viewmodel.MenuViewModel
import com.example.patas_y_colas.viewmodel.MenuViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MenuScreen(
    // Recibe el NavController
    navController: NavHostController
) {
    val application = LocalContext.current.applicationContext as PetApplication
    val viewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(application.repository, application))
    val pets by viewModel.allPets.collectAsState()

    // --- AÑADIDO: Recoger el estado del dato curioso ---
    val catFact by viewModel.catFact.collectAsState()

    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var isFormVisible by remember { mutableStateOf(false) }

    val windowSizeClass = rememberWindowSizeClass()

    // --- ¡NUEVO! OBTENER NOMBRE DE USUARIO ---
    val context = LocalContext.current
    val userName by remember {
        mutableStateOf(TokenManager.getUserName(context))
    }
    // --- FIN ---

    if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
        isFormVisible = true
        if (selectedPet == null && pets.isNotEmpty()) {
            selectedPet = pets.first()
        }
    }

    // --- LÓGICA DE LOGOUT (Corregida) ---
    val repository = (application as PetApplication).repository
    val scope = rememberCoroutineScope()

    val onLogoutClicked: () -> Unit = {
        scope.launch {
            // 1. Llama a la función del repositorio que borra los tokens
            repository.logout()

            // 2. Navega al login y limpia el historial
            navController.navigate("login") { // Usa la ruta "login"
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
    // --- FIN DE LÓGICA DE LOGOUT ---


    Surface(modifier = Modifier.fillMaxSize(), color = PetBackground) {

        // --- AÑADIDO: Diálogo para el dato curioso ---
        if (catFact != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearFunFact() },
                title = { Text("Dato Curioso Felino 🐱") },
                text = { Text(catFact!!) },
                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearFunFact() }) {
                        Text("¡Genial!")
                    }
                }
            )
        }
        // ------------------------------------------

        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                MenuScreenCompact(
                    pets = pets,
                    selectedPet = selectedPet,
                    isFormVisible = isFormVisible,
                    onPetSelected = { pet ->
                        if (selectedPet == pet) isFormVisible = !isFormVisible
                        else { selectedPet = pet; isFormVisible = true }
                    },
                    onAddPetClicked = {
                        if (selectedPet == null && isFormVisible) {
                            isFormVisible = false
                        } else {
                            selectedPet = null
                            isFormVisible = true
                        }
                    },
                    onFormAction = { action ->
                        when(action) {
                            // Corrección para guardar mascota (id null o 0)
                            is FormAction.Save -> {
                                if (action.pet.id == null || action.pet.id == 0) {
                                    viewModel.insert(action.pet)
                                } else {
                                    viewModel.update(action.pet)
                                }
                            }
                            is FormAction.Delete -> viewModel.delete(action.pet)
                        }
                        isFormVisible = false
                    },
                    // --- Pasamos la función y el controller ---
                    onLogoutClicked = onLogoutClicked,
                    navController = navController,
                    userName = userName, // <-- PASAR NOMBRE
                    onShowFactClicked = { viewModel.loadFunFact() } // <-- AÑADIDO
                )
            }
            else -> {
                MenuScreenExpanded(
                    pets = pets,
                    selectedPet = selectedPet,
                    onPetSelected = { pet -> selectedPet = pet },
                    onAddPetClicked = { selectedPet = null },
                    onFormAction = { action ->
                        when(action) {
                            // Corrección para guardar mascota (id null o 0)
                            is FormAction.Save -> {
                                if (action.pet.id == null || action.pet.id == 0) {
                                    viewModel.insert(action.pet)
                                } else {
                                    viewModel.update(action.pet)
                                }
                            }
                            is FormAction.Delete -> viewModel.delete(action.pet)
                        }
                        selectedPet = null
                    },
                    // --- Pasamos la función y el controller ---
                    onLogoutClicked = onLogoutClicked,
                    navController = navController,
                    userName = userName, // <-- PASAR NOMBRE
                    onShowFactClicked = { viewModel.loadFunFact() } // <-- AÑADIDO
                )
            }
        }
    }
}

@Composable
fun MenuScreenCompact(
    pets: List<Pet>,
    selectedPet: Pet?,
    isFormVisible: Boolean,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit,
    onFormAction: (FormAction) -> Unit,
    // --- Añadimos los parámetros ---
    onLogoutClicked: () -> Unit,
    navController: NavHostController,
    userName: String?, // <-- RECIBIR NOMBRE
    onShowFactClicked: () -> Unit // <-- AÑADIDO
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HeaderSection(
            pets = pets,
            selectedPet = selectedPet,
            onPetSelected = onPetSelected,
            onAddPetClicked = onAddPetClicked,
            onLogoutClicked = onLogoutClicked,
            userName = userName, // <-- PASAR NOMBRE
            onShowFactClicked = onShowFactClicked // <-- AÑADIDO
        )
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isFormVisible,
            enter = expandVertically(expandFrom = Alignment.Top, animationSpec = tween(500)),
            exit = shrinkVertically(shrinkTowards = Alignment.Top, animationSpec = tween(500))
        ) {
            PetForm(
                pet = selectedPet,
                onSave = { onFormAction(FormAction.Save(it)) },
                onDelete = { onFormAction(FormAction.Delete(it)) }
            )
        }

        AnimatedVisibility(
            visible = !isFormVisible,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            ReminderSection(pets = pets)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun MenuScreenExpanded(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit,
    onFormAction: (FormAction) -> Unit,
    // --- Añadimos los parámetros ---
    onLogoutClicked: () -> Unit,
    navController: NavHostController,
    userName: String?, // <-- RECIBIR NOMBRE
    onShowFactClicked: () -> Unit // <-- AÑADIDO
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderSection(
                pets = pets,
                selectedPet = selectedPet,
                onPetSelected = onPetSelected,
                onAddPetClicked = onAddPetClicked,
                // --- Lo pasamos al Header ---
                onLogoutClicked = onLogoutClicked,
                userName = userName, // <-- PASAR NOMBRE
                onShowFactClicked = onShowFactClicked // <-- AÑADIDO
            )
            Spacer(modifier = Modifier.height(24.dp))
            ReminderSection(pets = pets)
        }
        Column(
            modifier = Modifier
                .weight(1.5f)
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp, bottom = 16.dp, end = 24.dp, start = 8.dp)
        ) {
            PetForm(
                pet = selectedPet,
                onSave = { onFormAction(FormAction.Save(it)) },
                onDelete = { onFormAction(FormAction.Delete(it)) }
            )
        }
    }
}

sealed class FormAction {
    data class Save(val pet: Pet) : FormAction()
    data class Delete(val pet: Pet) : FormAction()
}


@Composable
private fun ReminderSection(pets: List<Pet>) {
    val reminders = remember(pets) {
        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        pets.flatMap { pet ->
            // Usamos la lista directa del modelo
            val vaccineList: List<VaccineRecord> = pet.vaccines ?: emptyList() // Aseguramos que no sea null

            vaccineList.filter { vaccine ->
                if (vaccine.vaccineName.isNotBlank() &&
                    vaccine.date.isNotBlank()) { // No es necesario chequear null si no son nulos
                    try {
                        val vaccineDate = dateFormat.parse(vaccine.date)
                        vaccineDate != null && !vaccineDate.before(today)
                    } catch (e: Exception) {
                        false
                    }
                } else {
                    false
                }
            }.map { vaccine -> pet.name to vaccine }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Recordatorios de Vacunas", style = MaterialTheme.typography.titleLarge, color = PetTextDark)

        if (reminders.isEmpty()) {
            Text("No hay vacunas programadas.", color = PetTextLight)
        } else {
            reminders.forEach { (petName, vaccine) ->
                ReminderCard(
                    petName = petName,
                    vaccineName = vaccine.vaccineName,
                    date = vaccine.date
                )
            }
        }
    }
}

@Composable
private fun ReminderCard(petName: String, vaccineName: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Recordatorio", tint = PetTextLight)
            Column {
                Text(text = "$petName - $vaccineName", color = PetTextDark, fontWeight = FontWeight.Bold)
                Text(text = "Fecha: $date", color = PetTextLight)
            }
        }
    }
}