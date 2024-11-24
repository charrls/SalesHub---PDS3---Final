package com.example.saleshub.views.salesmodule

import android.app.Activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.model.Product
import com.example.saleshub.model.Screen
import com.example.saleshub.viewmodel.ClientViewModel
import com.example.saleshub.viewmodel.ProductViewModel
import com.example.saleshub.viewmodel.SalesViewModel


@Composable
fun registerSaleScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    clientViewModel: ClientViewModel,
    salesViewModel: SalesViewModel
) {
    var isButtonEnabled by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val selectedProducts = remember { mutableStateListOf<Product>() }

    // Obtenemos la lista de clientes desde el ClientViewModel
    val clientList by clientViewModel.clientListState.collectAsState()

    // Aquí mantenemos el precio total de la venta
    var totalPrice by remember { mutableStateOf(0.0) }

    // Para calcular el precio total con los productos seleccionados
    val calculateTotalPrice = { productos: List<Product>, cantidades: List<Int> ->
        totalPrice = productos.zip(cantidades).sumOf { (product, cantidad) ->
            product.price * cantidad
        }
    }
    LaunchedEffect(selectedProducts) {
        isButtonEnabled = selectedProducts.isNotEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            salesHeader(navController, Modifier.fillMaxWidth())
            Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(30.dp))
            salesIcon()

            // Llamamos a la función selectProduct pasando selectedProducts
            selectProduct(productViewModel, selectedProducts)
            // Calculamos el precio total al seleccionar productos
            calculateTotalPrice(selectedProducts, selectedProducts.map { 1 }) // Asumimos que por ahora es 1 por producto
            // Llamamos a viewProducts, pasando los productos seleccionados
            viewProducts(selectedProducts) { producto ->
                selectedProducts.remove(producto)
                calculateTotalPrice(selectedProducts, selectedProducts.map { 1 })
            }
        }

        SalesButtons(selectedProducts, navController, onRegisterSale = {
            // Mostrar el diálogo de confirmación cuando el usuario desea registrar la venta
            showConfirmDialog = true
        })
    }

    // Mostrar el diálogo de confirmación cuando la bandera sea verdadera
    if (showConfirmDialog) {
        ConfirmSaleDialog(
            clientName = "Seleccionar cliente",  // Puedes mostrar el nombre del cliente si es necesario
            clients = clientList.map { it.name },
            onConfirmUpdate = { selectedClient ->
                // Acción para confirmar la venta
                val clientId = if (selectedClient != "Ninguno") {
                    clientList.firstOrNull { it.name == selectedClient }?.id
                } else {
                    null
                }

                salesViewModel.registerSale(
                    productos = selectedProducts.map { it.name },
                    cantidades = selectedProducts.map { 1 },
                    precioTotal = totalPrice,
                    esFiada = if (clientId != null) true else false,
                    idCliente = clientId
                )

                showConfirmDialog = false
            },
            onDismiss = {
                // Acción para descartar el diálogo
                showConfirmDialog = false
            }
        )
    }
}


@Composable
fun viewProducts(
    selectedProducts: MutableList<Product>,
    onRemoveProduct: (Product) -> Unit,
) {
    // Agrupar los productos seleccionados por nombre para contar cuántos se han agregado
    val productCount = selectedProducts.groupBy { it.name }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp)
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp)),
            color = Color.White,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.lightNavigation))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Carrito",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.DarkGray
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp)) // Color con esquinas redondeadas
                        .clip(RoundedCornerShape(8.dp)) // Asegura que el contenido también respete las esquinas redondeadas
                        .padding(8.dp)
                        .heightIn(140.dp, max = 140.dp)
                ){
                    items(productCount.keys.toList()) { productName ->
                        val product = selectedProducts.first { it.name == productName }
                        val count = productCount[productName]?.size ?: 0
                        val totalPrice = product.price * count

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                IconButton(
                                    onClick = {
                                        // Eliminar todas las ocurrencias de este producto
                                        selectedProducts.removeAll { it.name == productName }
                                        onRemoveProduct(product)
                                    },
                                    modifier = Modifier
                                        .size(18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar producto",
                                        tint = colorResource(id = R.color.redButton),
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "x$count", // Muestra la cantidad de productos seleccionados
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = product.name,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                            Row {
                                Text(
                                    text = "$${product.price}", // Precio individual
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "$${totalPrice}", // Precio total del conjunto
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                        Divider()
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${selectedProducts.sumOf { it.price }}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}






@Composable
fun selectProduct(productViewModel: ProductViewModel, selectedProducts: MutableList<Product>, modifier: Modifier = Modifier) {
    val productList by productViewModel.productListState.collectAsState()

    // Filtrar los productos por tipo
    val alimentos = productList.filter { it.type == "Alimento" }
    val adicionales = productList.filter { it.type == "Adicional" }

    // Divide adicionales en dos filas
    val halfSize = (adicionales.size + 1) / 2
    val topRowItems = adicionales.take(halfSize)
    val bottomRowItems = adicionales.drop(halfSize)

    Column(modifier = Modifier.padding(22.dp)) {
        Text(text = "Alimentos", Modifier.padding(6.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(alimentos.size) { index ->
                val product = alimentos[index]
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .width((LocalConfiguration.current.screenWidthDp.dp - 64.dp) / 3) // Ancho fijo para tres productos
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clickable {
                            selectedProducts.add(product)
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_buttons)),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = product.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp),
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$ " + product.price.toString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 2.dp),
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(text = "Adicionales", Modifier.padding(6.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Primera fila de Adicionales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        topRowItems.forEach { product ->
                            Card(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .width((LocalConfiguration.current.screenWidthDp.dp - 64.dp) / 3) // Ancho fijo para tres productos
                                    .shadow(2.dp, RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedProducts.add(product)
                                    },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_buttons)),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = product.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 4.dp),
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "$ " + product.price.toString(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(bottom = 2.dp),
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Segunda fila de Adicionales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        bottomRowItems.forEach { product ->
                            Card(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .width((LocalConfiguration.current.screenWidthDp.dp - 64.dp) / 3) // Ancho fijo para tres productos
                                    .shadow(2.dp, RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedProducts.add(product)
                                    },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_buttons)),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = product.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 4.dp),
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "$ " + product.price.toString(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(bottom = 2.dp),
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SalesButtons(lista: List<Product>, navController: NavController, onRegisterSale: () -> Unit, modifier: Modifier = Modifier) {
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
            onClick = { onRegisterSale() }, // Trigger the confirmation dialog
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.greenButton)),
            enabled = lista.isNotEmpty(),
            modifier = Modifier
                .height(55.dp)
                .weight(1f)
                .padding(end = 10.dp)
                .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text("Registrar venta", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmSaleDialog(
    clientName: String,
    clients: List<String>,  // List of clients for dropdown
    onConfirmUpdate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var isCreditSale by remember { mutableStateOf(false) }
    var selectedClient by remember { mutableStateOf(clientName) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registrar venta",
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = isCreditSale,
                        onCheckedChange = { isCreditSale = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(id = R.color.greenButton),
                            )
                    )
                    Text(
                        text = "Venta fiada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }

                // Dropdown for selecting client if credit sale is checked
                if (isCreditSale) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Seleccionar cliente:")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            TextField(
                                value = selectedClient,
                                onValueChange = { selectedClient = it },
                                readOnly = true,
                                label = { Text("Cliente") },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                                },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                clients.forEach { client ->
                                    DropdownMenuItem(
                                        text = { Text(text = client) },
                                        onClick = {
                                            selectedClient = client
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
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
                    // "Cancel" button
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
                    // Divider between buttons
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(45.dp)
                            .background(Color.LightGray)
                    )
                    // "Confirm" button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onConfirmUpdate(selectedClient)  // Pasamos el cliente seleccionado
                                onDismiss()  // Close the dialog after confirmation
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Registrar",
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.greenButton)
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
fun salesHeader(navController: NavController, modifier: Modifier = Modifier) {
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
        IconButton(
            onClick = { navController.popBackStack() },
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.DarkGray,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "Registrar ventas",
            fontSize = 18.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(end = 22.dp)
        )
    }
}

@Composable
fun salesIcon(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween // Cambiado a SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.ventas),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .align(alignment = Alignment.CenterVertically) // Alinear verticalmente
            )
            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .height(1.dp)
                    .align(alignment = Alignment.CenterVertically)
                    .padding(end = 2.dp)
            )

        }
    }
}


