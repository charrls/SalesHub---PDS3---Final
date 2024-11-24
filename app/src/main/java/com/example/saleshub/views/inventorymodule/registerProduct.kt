package com.example.saleshub.views.inventorymodule

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.viewmodel.ProductViewModel

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun RegisterProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    modifier: Modifier = Modifier
) {
    var productType by remember { mutableStateOf("Alimento") }

    // Estado para el Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Estado para validar si el formulario es válido
    var isFormValid by remember { mutableStateOf(false) }

    // Usar Scaffold para la estructura básica
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            HeaderRegisterInventory(navController, Modifier.fillMaxWidth())
        },
        snackbarHost = {
            MySnackbarHost(snackbarHostState) // Usamos nuestra función personalizada
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
                    iconRInventory()
                    SelectTypeProduct { selectedType -> productType = selectedType }

                    // Llamamos al formulario pasando una lambda para actualizar el estado de validez del formulario
                    ProductForm(
                        productType = productType,
                        productViewModel = productViewModel,
                        onFormValidChange = { isValid -> isFormValid = isValid } // Actualizamos el estado de validez del formulario
                    )
                }

                FootRegisterButtons(
                    productViewModel = productViewModel,
                    productType = productType,
                    isFormValid = isFormValid, // Pasamos el estado de validez del formulario
                    onRegisterSuccess = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Producto registrado con éxito",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    navController
                )
            }
        }
    )
}


@Composable
fun MySnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(snackbarHostState) { snackbarData: SnackbarData ->
        Snackbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 120.dp),
            snackbarData = snackbarData,
            shape = RoundedCornerShape(8.dp) // Forma opcional
        )
    }
}



@Composable
fun ProductForm(
    productType: String,
    productViewModel: ProductViewModel,
    onFormValidChange: (Boolean) -> Unit, // Callback para actualizar la validez del formulario
    modifier: Modifier = Modifier
) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productStock by remember { mutableStateOf("") }
    var productStockMin by remember { mutableStateOf("") }

    // Validaciones de errores
    val nameError = productName.trim().isEmpty() || productName.length > 25
    val descriptionError = productDescription.length > 40
    val priceError = when {
        productPrice.trim().isEmpty() -> true // Campo vacío
        !productPrice.matches(Regex("^[0-9]+(\\.[0-9]{1,2})?$")) -> true // Formato inválido
        productPrice.toDoubleOrNull()?.let { it <= 0.0 } == true -> true // Precio negativo o cero
        else -> false
    }
    val stockError = productType == "Adicional" && (productStock.trim().isEmpty() || !productStock.matches(Regex("^[0-9]+$")))
    val stockMinError = productType == "Adicional" && (productStockMin.trim().isEmpty() || !productStockMin.matches(Regex("^[0-9]+$")))

    // Validación global del formulario
    val isFormValid = !nameError && !descriptionError && !priceError &&
            (!stockError || productType == "Alimento") &&
            (!stockMinError || productType == "Alimento")

    // Actualizamos la validez del formulario en cada cambio
    onFormValidChange(isFormValid)

    Column(
        modifier = modifier.padding(horizontal = 40.dp)
    ) {
        // Campo Nombre del producto
        Text("Producto")
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth(),
            isError = nameError, // Indicar error en el campo
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
        if (productName.trim().isEmpty()) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = "Campo obligatorio",
                color = Color.Gray, // Amarillo
                style = MaterialTheme.typography.bodySmall
            )
        } else if (productName.length > 25) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = "Máximo 25 caracteres",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = modifier.height(25.dp))

        // Campo Descripción del producto (opcional)
        Text("Descripción")
        OutlinedTextField(
            value = productDescription,
            onValueChange = { productDescription = it },
            label = { Text("Descripción del producto") },
            modifier = Modifier.fillMaxWidth(),
            isError = descriptionError,
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
        if (descriptionError) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = "Máximo 40 caracteres",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = modifier.height(25.dp))

        if (productType == "Adicional") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Campo Stock inicial
                    Text("Stock")
                    OutlinedTextField(
                        value = productStock,
                        onValueChange = { productStock = it },
                        label = { Text("Stock inicial") },
                        modifier = Modifier.width(150.dp),
                        isError = stockError,
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    if (productStock.trim().isEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                            text = "Campo obligatorio",
                            color = Color.Gray, // Amarillo
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (!productStock.matches(Regex("^[0-9]+$"))) {
                        Text(
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                            text = "Dato no valido",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Column {
                    // Campo Stock mínimo
                    Text("Stock minimo")
                    OutlinedTextField(
                        value = productStockMin,
                        onValueChange = { productStockMin = it },
                        label = { Text("Stock mínimo") },
                        modifier = Modifier.width(150.dp),
                        isError = stockMinError,
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    if (productStockMin.trim().isEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                            text = "Campo obligatorio",
                            color = Color.Gray, // Amarillo
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (!productStockMin.matches(Regex("^[0-9]+$"))) {
                        Text(
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                            text = "Dato no valido",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(modifier = modifier.height(25.dp))

        Text("Precio")
        OutlinedTextField(
            value = productPrice,
            onValueChange = { productPrice = it },
            label = { Text("$ 0.0") },
            modifier = Modifier.width(150.dp),
            isError = priceError,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (productPrice.trim().isEmpty()) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = "Campo obligatorio",
                color = Color.Gray, // Amarillo
                style = MaterialTheme.typography.bodySmall
            )
        } else if (!productPrice.matches(Regex("^[0-9]+(\\.[0-9]{1,2})?$")) || productPrice.toDoubleOrNull()?.let { it <= 0.0 } == true) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = "Dato no valido",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Guardar los datos en el ViewModel si es válido
        if (isFormValid) {
            productViewModel.updateProductFields(
                name = productName,
                description = productDescription,
                price = productPrice.toDoubleOrNull() ?: 0.0,
                stock = if (productType == "Adicional") productStock.toInt() else 0,
                stockmin = if (productStockMin.isNotEmpty()) productStockMin.toInt() else 0,
                type = productType
            )
        }
    }
}



@Composable
fun FootRegisterButtons(
    productViewModel: ProductViewModel,
    productType: String,
    isFormValid: Boolean, // Este parámetro indica si el formulario es válido
    onRegisterSuccess: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 35.dp)
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
            onClick = {
                productViewModel.registerProduct()
                onRegisterSuccess()
                navController.popBackStack()
            },
            enabled = isFormValid, // Habilitado solo si el formulario es válido
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.orangeButton)),
            modifier = Modifier
                .height(55.dp)
                .weight(1f)
                .padding(end = 10.dp)
                .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text("Registrar", color = Color.White)
        }
    }
}



@Composable
fun SelectTypeProduct(modifier: Modifier = Modifier, onTypeSelected: (String) -> Unit) {
    val productList = listOf("Alimento", "Adicional")
    var expanded by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf(productList[0]) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .padding(horizontal = 30.dp)
    ) {
        Box {
            OutlinedButton(onClick = { expanded = !expanded }) {
                Text(
                    selectedProduct,
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .padding(end = 12.dp)
                )
                Icon(imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Desplegar",
                    tint = Color.DarkGray
                )
            }
            DropdownMenu(
                modifier = Modifier.background(Color.White),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                productList.forEach { product ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .width(145.dp)
                            .background(Color.White),
                        text = {
                            Text(
                                text = product,
                                fontSize = 14.sp, // Tamaño de fuente personalizado
                                color = Color.DarkGray, // Color del texto
                                modifier = Modifier.padding(8.dp) // Padding interno
                            ) },
                        onClick = {
                            selectedProduct = product
                            expanded = false
                            onTypeSelected(product)
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
    }
}








@Composable
fun HeaderRegisterInventory(navController: NavController, modifier: Modifier = Modifier) {

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
                text = "Registrar producto",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(end = 22.dp)
            )

        }
        Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)

    }

}

@Composable
fun iconRInventory(modifier: Modifier = Modifier) {
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
                painter = painterResource(id = R.drawable.inventario),
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
