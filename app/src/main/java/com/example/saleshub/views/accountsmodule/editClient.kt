import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.model.Client
import com.example.saleshub.model.Product
import com.example.saleshub.viewmodel.ClientViewModel
import com.example.saleshub.viewmodel.ProductViewModel
import kotlinx.coroutines.launch



@Composable
fun EditClientScreen(
    navController: NavController,
    AccountsViewModel: ClientViewModel,
    modifier: Modifier = Modifier,
    isFromSwipe: Boolean = false, // Indica si la navegación es desde swipe-to-edit
    clientId: String? = null // ID del cliente para rellenar automáticamente
) {
    val clientList by AccountsViewModel.clientListState.collectAsState()
    var selectedClient by remember { mutableStateOf<Client?>(null) }

    // Estado para el formulario del cliente
    var clientFormState by remember { mutableStateOf(ClientFormState()) }
    var showConfirmDialog by remember { mutableStateOf(false) } // Estado para mostrar el cuadro de confirmación

    // Estado del Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Inicializar el cliente si venimos de swipe
    if (isFromSwipe && clientId != null) {
        val foundClient = clientList.find { it.id == clientId.toInt() }

        if (foundClient != null && foundClient != selectedClient) {
            selectedClient = foundClient

            // Actualiza el estado del formulario con los datos del cliente encontrado
            clientFormState = ClientFormState(
                name = foundClient.name,
                phoneNumber = foundClient.phone
            )
        }
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            HeaderEditClient(navController, Modifier.fillMaxWidth())
        },
        snackbarHost = {
            MySnackbarHost(snackbarHostState)
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(paddingValues)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    iconEditClient()

                    selectedClient?.let {
                        ClientInfoCard(it)
                    }

                    EditClientForm(
                        clientName = clientFormState.name,
                        onClientNameChange = { clientFormState = clientFormState.copy(name = it) },
                        phoneNumber = clientFormState.phoneNumber,
                        onPhoneNumberChange = { clientFormState = clientFormState.copy(phoneNumber = it) },
                        nameError = validateClientForm(clientFormState).nameError,
                        phoneNumberError = validateClientForm(clientFormState).phoneNumberError
                    )
                }

                val validatedFormState = validateClientForm(clientFormState)
                FootUpdateButtonsClient(
                    onUpdateClicked = {
                        if (validatedFormState.isValid && isClientFormChanged(clientFormState, selectedClient)) {
                            showConfirmDialog = true
                        } else {
                            validatedFormState.errorMessages.forEach { errorMessage ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = errorMessage,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    },
                    onCancelClicked = { navController.popBackStack() },
                    isUpdateEnabled = validatedFormState.isValid && isClientFormChanged(clientFormState, selectedClient)
                )
            }
        }
    )

    if (showConfirmDialog) {
        ConfirmUpdateDialogClient(
            clientName = clientFormState.name,
            onConfirmUpdate = {
                selectedClient?.let { client ->
                    AccountsViewModel.updateClient(
                        Client(
                            id = client.id,
                            name = clientFormState.name,
                            phone = clientFormState.phoneNumber,
                            balance = client.balance,
                            maxAmount = client.maxAmount,
                            maxTerm = client.maxTerm
                        )
                    )
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Cliente actualizado con éxito",
                            duration = SnackbarDuration.Short
                        )
                    }
                    navController.popBackStack()
                }
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
}

// Verificar si el formulario ha cambiado
private fun isClientFormChanged(clientFormState: ClientFormState, selectedClient: Client?): Boolean {
    return selectedClient?.let {
        it.name != clientFormState.name || it.phone != clientFormState.phoneNumber
    } ?: false
}

// Formulario de edición de cliente
@Composable
fun EditClientForm(
    clientName: String,
    onClientNameChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    nameError: String?,
    phoneNumberError: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 40.dp)) {
        Spacer(modifier = Modifier.height(25.dp))
        Text(text = "Cliente")
        OutlinedTextField(
            value = clientName,
            onValueChange = onClientNameChange,
            label = { Text("Nombre del cliente") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            isError = nameError != null,
            singleLine = true
        )
        nameError?.let {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
        )  }
        Spacer(modifier = Modifier.height(25.dp))

        Text(text = "Teléfono")
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Número de teléfono") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            isError = phoneNumberError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        phoneNumberError?.let {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
        )  }
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun ClientInfoCard(client: Client) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Cambia el color de fondo aquí
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = client.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = client.phone, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

// Validar el formulario del cliente
private fun validateClientForm(clientFormState: ClientFormState): ClientFormValidationState {
    val errorMessages = mutableListOf<String>()
    var isValid = true

    val nameError = if (clientFormState.name.isBlank()) {
        isValid = false
        "Campo obligatorio"
    } else if (clientFormState.name.length > 30) {
        isValid = false
        "Máximo 30 caracteres"
    } else null

    val phoneNumberError = if (clientFormState.phoneNumber.isBlank()) {
        isValid = false
        "Campo obligatorio"
    } else if (!clientFormState.phoneNumber.matches(Regex("^\\+?\\d{10}$"))) {
        isValid = false
        "Número de teléfono no válido (10 dígitos)"
    } else null

    return ClientFormValidationState(
        isValid = isValid,
        nameError = nameError,
        phoneNumberError = phoneNumberError,
        errorMessages = errorMessages
    )
}

// Definir el estado del formulario y la validación para el cliente
data class ClientFormState(
    val name: String = "",
    val phoneNumber: String = ""
)

data class ClientFormValidationState(
    val isValid: Boolean,
    val nameError: String?,
    val phoneNumberError: String?,
    val errorMessages: List<String>
)


@Composable
fun ConfirmUpdateDialogClient(
    clientName: String,
    onConfirmUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Actualizar cliente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"$clientName\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "¿Desea actualizar los datos del cliente?",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(color = Color.LightGray, thickness = 1.dp)
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Botón de "Cancelar"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onDismiss() }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancelar",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                    // Línea divisoria entre los botones
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(45.dp)
                            .background(Color.LightGray)
                    )
                    // Botón de "Actualizar"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onConfirmUpdate()
                                onDismiss()  // Cierra el diálogo después de confirmar
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Actualizar",
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.purpleButton)
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}



@Composable
fun FootUpdateButtonsClient(
    onUpdateClicked: () -> Unit,
    onCancelClicked: () -> Unit, // Nueva propiedad para manejar la acción de cancelar
    isUpdateEnabled: Boolean, // Propiedad existente para habilitar/deshabilitar
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
            onClick = onCancelClicked, // Llama a la función onCancelClicked
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
            onClick = onUpdateClicked,
            enabled = isUpdateEnabled, // Deshabilitar el botón si no hay cambios
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.purpleButton)),
            modifier = Modifier
                .height(55.dp)
                .weight(1f)
                .padding(end = 10.dp)
                .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text("Actualizar", color = Color.White)
        }
    }
}




@Composable
fun HeaderEditClient(navController: NavController, modifier: Modifier = Modifier) {

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
                text = "Editar cliente",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(end = 22.dp)
            )

        }
        Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)

    }

}

@Composable
fun iconEditClient(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row (
            modifier = modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalArrangement = Arrangement.Start)
        {
            Image(
                painter = painterResource(id = R.drawable.cuentas),
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
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
