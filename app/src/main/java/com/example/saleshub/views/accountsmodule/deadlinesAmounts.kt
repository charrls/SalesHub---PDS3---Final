package com.example.saleshub.views.accountsmodule

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.viewmodel.ClientViewModel


@Composable
fun DeadlinesScreen(navController: NavController, clientViewModel: ClientViewModel, modifier: Modifier = Modifier) {
    var maxTerm by remember { mutableStateOf("") }
    var maxAmount by remember { mutableStateOf("") }
    var isMaxTermValid by remember { mutableStateOf(true) }
    var isMaxAmountValid by remember { mutableStateOf(true) }
    var showMaxTermError by remember { mutableStateOf(false) }
    var showMaxAmountError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HeaderDeadlines(navController, Modifier.fillMaxWidth())
            iconDeadlines()
            DeadlinesForm(
                maxTerm = maxTerm,
                onMaxTermChange = {
                    maxTerm = it
                    isMaxTermValid = it.isNotEmpty() && it.toIntOrNull() != null && it.toInt() > 0
                    showMaxTermError = it.isEmpty() || !isMaxTermValid
                },
                maxAmount = maxAmount,
                onMaxAmountChange = {
                    maxAmount = it
                    isMaxAmountValid = it.isNotEmpty() && it.toDoubleOrNull() != null && it.toDouble() > 0
                    showMaxAmountError = it.isEmpty() || !isMaxAmountValid
                },
                showMaxTermError = showMaxTermError,
                showMaxAmountError = showMaxAmountError
            )
        }
        FootDeadlinesBottons(
            onCancel = { navController.popBackStack() },
            onRegister = {
                if (isMaxTermValid && isMaxAmountValid) {
                    val maxTermValue = maxTerm.toIntOrNull()
                    val maxAmountValue = maxAmount.toDoubleOrNull()
                    if (maxTermValue != null && maxAmountValue != null) {
                        clientViewModel.updateAllMaxAmountAndTerm(maxAmountValue, maxTermValue)
                        navController.popBackStack() // Vuelve a la pantalla anterior
                    }
                }
            },
            maxTerm = maxTerm,
            maxAmount = maxAmount
        )
    }
}

@Composable
fun FootDeadlinesBottons(
    onCancel: () -> Unit,
    onRegister: () -> Unit,
    maxTerm: String,
    maxAmount: String,
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
            enabled = maxTerm.isNotEmpty() && maxTerm.toIntOrNull() != null && maxTerm.toInt() > 0 &&
                    maxAmount.isNotEmpty() && maxAmount.toDoubleOrNull() != null && maxAmount.toDouble() > 0
        ) {
            Text("Registrar", color = Color.White)
        }
    }
}



@Composable
fun DeadlinesForm(
    maxTerm: String,
    onMaxTermChange: (String) -> Unit,
    maxAmount: String,
    onMaxAmountChange: (String) -> Unit,
    showMaxTermError: Boolean,
    showMaxAmountError: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 40.dp, vertical = 40.dp)) {
        Text(text = "Plazo máximo (días)")
        OutlinedTextField(
            value = maxTerm,
            onValueChange = onMaxTermChange,
            label = { Text("Plazo máximo para pagar la deuda") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showMaxTermError // Indica si el campo debe mostrarse con error
        )
        if (showMaxTermError) {
            Text(
                text = when {
                    maxTerm.isEmpty() -> "Campo obligatorio"
                    maxTerm.toIntOrNull() == null || maxTerm.toInt() <= 0 -> "Dato no válido"
                    else -> ""
                },
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = modifier.height(30.dp))

        Text(text = "Monto máximo (MXN)")
        OutlinedTextField(
            value = maxAmount,
            onValueChange = onMaxAmountChange,
            label = { Text("Monto máximo de deuda permitida") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showMaxAmountError // Indica si el campo debe mostrarse con error
        )
        if (showMaxAmountError) {
            Text(
                text = when {
                    maxAmount.isEmpty() -> "El campo no puede estar vacío"
                    maxAmount.toDoubleOrNull() == null || maxAmount.toDouble() <= 0 -> "Ingrese un valor numérico positivo"
                    else -> ""
                },
                color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}



@Composable
fun HeaderDeadlines(navController: NavController, modifier: Modifier = Modifier) {

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
                text = "Plazos y montos",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(end = 22.dp)
            )

        }
        Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)

    }

}

@Composable
fun iconDeadlines(modifier: Modifier = Modifier) {
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



