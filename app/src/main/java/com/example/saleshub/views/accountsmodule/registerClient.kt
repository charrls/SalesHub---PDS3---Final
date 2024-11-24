package com.example.saleshub.views.accountsmodule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saleshub.R
import com.example.saleshub.model.Client
import com.example.saleshub.viewmodel.ClientViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterClientScreen(
    navController: NavController,
    accountViewModel: ClientViewModel,
    modifier: Modifier = Modifier
) {
    // Observa los valores globales para creditMax y termMax
    val globalMaxAmount by accountViewModel.globalMaxAmount.collectAsState()
    val globalMaxTerm by accountViewModel.globalMaxTerm.collectAsState()

    // Estados para los campos
    var clientName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("0.0") }
    var creditMax by remember { mutableStateOf(globalMaxAmount?.toString() ?: "0.0") }
    var termMax by remember { mutableStateOf(globalMaxTerm?.toString() ?: "0") }

    // Actualizar los valores cuando cambien los valores globales
    LaunchedEffect(globalMaxAmount, globalMaxTerm) {
        creditMax = globalMaxAmount?.toString() ?: "0.0"
        termMax = globalMaxTerm?.toString() ?: "0"
    }

    // Validación de formulario
    val isFormValid by remember(clientName, phoneNumber, balance, creditMax, termMax) {
        derivedStateOf {
            clientName.isNotBlank() &&
                    phoneNumber.isNotBlank() &&
                    balance.toDoubleOrNull() != null &&
                    creditMax.toDoubleOrNull() != null &&
                    termMax.toIntOrNull() != null
        }
    }

    // Estado para el Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HeaderRegisterClient(navController, Modifier.fillMaxWidth())
            iconAInventory()
            RegisterClientForm(
                clientName = clientName,
                phoneNumber = phoneNumber,
                onClientNameChange = { clientName = it },
                onPhoneNumberChange = { phoneNumber = it },

            )
        }
        FootRClientBottons(
            onCancel = { navController.popBackStack() },
            onRegister = {
                if (isFormValid) {
                    accountViewModel.registerClient(
                        name = clientName,
                        num = phoneNumber,
                        balance = balance.toDouble()
                    )
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Cliente registrado con éxito",
                            duration = SnackbarDuration.Short
                        )
                    }
                    navController.popBackStack()
                }
            },
            isFormValid = isFormValid
        )
    }
}


@Composable
fun RegisterClientForm(
    clientName: String,
    phoneNumber: String,

    onClientNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,

    modifier: Modifier = Modifier
) {
    // Validaciones de los campos
    val clientNameError = clientName.trim().isEmpty()
    val phoneNumberError = phoneNumber.trim().isEmpty() || !phoneNumber.matches(Regex("^[0-9]{10}$"))


    // Validación global del formulario
    val isFormValid = !clientNameError && !phoneNumberError

    Column(modifier = modifier.padding(horizontal = 40.dp, vertical = 40.dp)) {
        // Nombre del cliente
        Text("Cliente")
        OutlinedTextField(
            value = clientName,
            onValueChange = onClientNameChange,
            label = { Text("Nombre del cliente") },
            modifier = Modifier.fillMaxWidth(),
            isError = clientNameError,
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
        if (clientName.trim().isEmpty()) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = "Campo obligatorio",
                color = Color.Gray, // Amarillo
                style = MaterialTheme.typography.bodySmall
            )
        }
         else if (clientName.length > 35) {
            Text(
                text = "Máximo 25 caracteres",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = modifier.height(30.dp))

        // Teléfono
        Text("Teléfono")
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Número de teléfono") },
            modifier = Modifier.fillMaxWidth(),
            isError = phoneNumberError,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        if (phoneNumber.trim().isEmpty()) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = "Campo obligatorio",
                color = Color.Gray, // Amarillo
                style = MaterialTheme.typography.bodySmall
            )
        } else if (phoneNumberError) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = "Número de teléfono no válido (10 dígitos)",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
fun FootRClientBottons(
    onCancel: () -> Unit,
    onRegister: () -> Unit,
    isFormValid: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.grayButton)),
            modifier = Modifier
                .height(55.dp)
                .weight(0.7f)
                .padding(start = 10.dp)
                .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text("Cancelar", color = Color.White)
        }

        Spacer(modifier = modifier.width(40.dp))
        Button(
            onClick = onRegister,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.purpleButton)),
            modifier = Modifier
                .height(55.dp)
                .weight(1f)
                .padding(end = 10.dp)
                .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            enabled = isFormValid
        ) {
            Text("Registrar", color = Color.White)
        }
    }
}

@Composable
fun HeaderRegisterClient(navController: NavController, modifier: Modifier = Modifier) {

    Column (
        verticalArrangement = Arrangement.SpaceBetween
    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(
                    colorResource(id = R.color.light_gris),
                )
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Registrar cliente",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(end = 22.dp)
            )

        }
        Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)

    }
}

@Composable
fun iconAInventory(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.cuentas),
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp)
                    .height(1.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }
}
