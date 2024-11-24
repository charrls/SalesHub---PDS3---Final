package com.example.saleshub.views.salesmodule

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.model.Client
import com.example.saleshub.model.Sale
import com.example.saleshub.viewmodel.ClientViewModel
import com.example.saleshub.viewmodel.ProductViewModel
import com.example.saleshub.viewmodel.SalesViewModel
import com.example.saleshub.views.inventorymodule.ordenarProductos
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalesHistoryScreen(navController: NavController, salesViewModel: SalesViewModel, clientViewModel: ClientViewModel, productViewModel: ProductViewModel) {
    val filteredSales by salesViewModel.filteredSalesListState.collectAsState()

    var selectedFilter by remember { mutableStateOf("Todo") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HeaderHistory(navController, Modifier.fillMaxWidth())
            Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(30.dp))
            salesIconH()
            // Mostrar botones de filtrado
            ordenarProductosSale(selectedFilter) { filter ->
                selectedFilter = filter
                salesViewModel.filterSalesByDate(filter) // Filtra las ventas según el período
            }

            // Mostrar historial de ventas
            Column(modifier = Modifier.height(500.dp)) {
                ViewHistory(filteredSales, clientViewModel, salesViewModel, productViewModel)

            }
        }
        TotalSales(filteredSales, modifier = Modifier.padding(16.dp))
    }
}
@Composable
fun ViewHistory(sales: List<Sale>, clientViewModel: ClientViewModel, salesViewModel: SalesViewModel, productViewModel: ProductViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo
    var selectedSaleId by remember { mutableStateOf<Int?>(null) } // ID de la venta seleccionada
    var selectedSaleDate by remember { mutableStateOf<String>("") } // Fecha de la venta seleccionada

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 32.dp)
    ) {
        items(sales) { sale ->
            val productCount = sale.productos.groupBy { it }
            val maxVisibleProducts = 2
            var isExpanded by remember { mutableStateOf(false) }
            var clientState by remember { mutableStateOf<Client?>(null) }

            LaunchedEffect(sale.idCliente) {
                if (sale.idCliente != null) {
                    clientViewModel.getClientById(sale.idCliente)
                    clientViewModel.selectedClientState.collect { client ->
                        clientState = client
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                border = BorderStroke(0.5.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_gris))
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Productos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${formatDate(sale.fecha)}",
                                fontSize = 12.sp
                            )
                            IconButton(onClick = {
                                selectedSaleId = sale.id // Establecer ID de la venta
                                selectedSaleDate = formatDate(sale.fecha) // Establecer fecha de la venta
                                showDeleteDialog = true // Mostrar el cuadro de diálogo
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar venta",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Mostrar productos en la venta
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        val productsToShow = if (isExpanded) productCount.keys else productCount.keys.take(maxVisibleProducts)

                        productsToShow.forEach { productName ->
                            val count = productCount[productName]?.size ?: 0
                            val product = productViewModel.productListState.value.find { it.name == productName }
                            val unitPrice = product?.price ?: 0.0
                            val totalPrice = unitPrice * count

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$count x $productName",
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${"%.2f".format(totalPrice)}",
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                            }
                            Divider()
                        }



                        if (productCount.keys.size > maxVisibleProducts) {
                            Text(
                                text = if (isExpanded) "Ver menos" else "Ver más",
                                fontSize = 12.sp,
                                color = colorResource(id = R.color.greenButton),
                                modifier = Modifier
                                    .clickable { isExpanded = !isExpanded }
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cliente: ${clientState?.name ?: "N/A"}",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Total: $${"%.2f".format(sale.precioTotal)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Mostrar el cuadro de diálogo de confirmación
    if (showDeleteDialog && selectedSaleId != null) {
        ConfirmDeleteSaleDialog(
            saleId = selectedSaleId!!,
            saleDate = selectedSaleDate,
            onConfirmDelete = {
                salesViewModel.deleteSaleById(selectedSaleId!!) // Lógica para eliminar la venta
                showDeleteDialog = false // Ocultar el cuadro de diálogo
            },
            onDismiss = {
                showDeleteDialog = false // Ocultar el cuadro de diálogo
            }
        )
    }
}


@Composable
fun ConfirmDeleteSaleDialog(
    saleId: Int,
    saleDate: String,
    onConfirmDelete: () -> Unit,
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
                    text = "Eliminar registro",
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
                    text = "\"$saleId : $saleDate\" ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esta acción no se puede deshacer.",
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

                    // Botón de "Eliminar"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onConfirmDelete()
                                onDismiss()  // Cierra el diálogo después de confirmar
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Eliminar",
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}



// Función para formatear la fecha en un formato legible
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) // Añadido HH:mm para hora y minutos
    return sdf.format(Date(timestamp))
}

@Composable
fun TotalSales(sales: List<Sale>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No. de ventas: ${sales.size}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Total $: ${sales.sumOf { it.precioTotal }}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}




@Composable
fun ordenarProductosSale(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón "Todo"
            FilterButton("Hoy", selectedFilter, onFilterSelected)
            Spacer(modifier = Modifier.width(8.dp))
            // Botón "Adicional"
            FilterButton("Semana", selectedFilter, onFilterSelected)
            Spacer(modifier = Modifier.width(8.dp))
            // Botón "Alimento"
            FilterButton("Quincena", selectedFilter, onFilterSelected)
        }
    }
}

@Composable
fun FilterButton(label: String, selectedFilter: String, onFilterSelected: (String) -> Unit) {
    val isSelected = selectedFilter == label

    androidx.compose.material3.Button(
        onClick = { onFilterSelected(label) },
        modifier = Modifier
            .border(0.5.dp, Color.LightGray, RoundedCornerShape(10.dp))
            .height(26.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.Gray else colorResource(id = R.color.light_buttons)
        )
    ) {
        Column (
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            androidx.compose.material.Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = if (isSelected) Color.White else Color.DarkGray
            )
        }

    }
}


@Composable
fun HeaderHistory(navController: NavController, modifier: Modifier = Modifier) {
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
            text = "Historial de ventas",
            fontSize = 18.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(end = 22.dp)
        )
    }
}



@Composable
fun salesIconH(modifier: Modifier = Modifier) {
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
