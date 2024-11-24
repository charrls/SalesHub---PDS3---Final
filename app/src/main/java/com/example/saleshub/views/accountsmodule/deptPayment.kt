package com.example.saleshub.views.accountsmodule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.model.Client
import com.example.saleshub.viewmodel.ClientViewModel

@Composable
fun DeptPaymentScreen(navController: NavController, clientViewModel: ClientViewModel, clientId: String?) {
    val clientIdInt = clientId?.toIntOrNull()
    var paymentAmount by remember { mutableStateOf("") }
    var isPaymentValid by remember { mutableStateOf(true) }
    var showErrorMessage by remember { mutableStateOf(false) }

    // Obtener datos del cliente desde el ViewModel
    val client = clientIdInt?.let { id ->
        clientViewModel.clientListState.value.find { it.id == id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HeaderPaymentClient(navController, Modifier.fillMaxWidth())
            iconDPayment()
            client?.let {
                ClientDetails(client = it) // Mostrar detalles del cliente
            }
            PaymentForm(
                paymentAmount = paymentAmount,
                onPaymentAmountChange = {
                    val newValue = it.toDoubleOrNull()
                    if (newValue != null && client != null) {
                        isPaymentValid = newValue > 0 && newValue <= client.balance!!
                        showErrorMessage = !isPaymentValid
                    } else {
                        isPaymentValid = false
                        showErrorMessage = true
                    }
                    paymentAmount = it
                },
                showErrorMessage = showErrorMessage,
                isEnabled = client?.balance != 0.0, // Deshabilitar si la deuda es 0
                clientBalance = client?.balance ?: 0.0 // Pasar el balance del cliente
            )
        }
        FootDPaymentBottons(
            navController,
            paymentAmount = paymentAmount,
            onRegisterPayment = {
                if (isPaymentValid && clientIdInt != null) {
                    clientViewModel.processPayment(clientIdInt, paymentAmount.toDouble())
                    navController.popBackStack()
                }
            }
        )
    }
}

@Composable
fun PaymentForm(
    paymentAmount: String,
    onPaymentAmountChange: (String) -> Unit,
    showErrorMessage: Boolean,
    isEnabled: Boolean, // Nueva propiedad para habilitar/deshabilitar el campo
    clientBalance: Double // Nuevo argumento para el balance del cliente
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Pago/abono")
        OutlinedTextField(
            value = paymentAmount,
            onValueChange = onPaymentAmountChange,
            label = { Text("$0.0") },
            modifier = Modifier.width(150.dp),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = showErrorMessage,
            enabled = isEnabled // Usar la nueva propiedad para controlar si está habilitado
        )
        if (showErrorMessage) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = when {
                    paymentAmount.isEmpty() -> "Campo obligatorio"
                    paymentAmount.toDoubleOrNull() == null || paymentAmount.toDouble() <= 0 -> "Dato no válido"
                    paymentAmount.toDoubleOrNull() != null && paymentAmount.toDouble() > clientBalance -> "Monto supera la deuda"
                    else -> ""
                },
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}



@Composable
fun FootDPaymentBottons(navController: NavController, paymentAmount: String, onRegisterPayment: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = { navController.popBackStack() },
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
            onClick = onRegisterPayment,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.purpleButton)),
            modifier = Modifier
                .height(55.dp)
                .weight(1f)
                .padding(end = 10.dp)
                .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            enabled = paymentAmount.isNotEmpty() && paymentAmount.toDoubleOrNull() != null && paymentAmount.toDouble() > 0
        ) {
            Text("Registrar", color = Color.White)
        }
    }
}

@Composable
fun ClientDetails(modifier: Modifier = Modifier, client: Client) {
    Column(modifier = modifier.padding(horizontal = 40.dp, vertical = 20.dp)) {
        Divider(
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
        )
        Spacer(modifier = modifier.height(30.dp))
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Cliente: ${client.name}")
            Spacer(modifier = modifier.height(20.dp))
            Text(text = "Telefono: ${client.phone}")
            Spacer(modifier = modifier.height(30.dp))
            Text(
                text = "Deuda: ${client.balance}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = modifier.height(30.dp))
        Divider(
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
        )
    }
}


@Composable
fun HeaderPaymentClient(navController: NavController, modifier: Modifier = Modifier) {

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
                text = "Pago de deuda",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(end = 22.dp)
            )

        }
        Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)

    }

}

@Composable
fun iconDPayment(modifier: Modifier = Modifier) {
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
