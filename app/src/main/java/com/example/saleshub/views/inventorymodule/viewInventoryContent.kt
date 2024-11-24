package com.example.saleshub.views.inventorymodule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saleshub.R
import com.example.saleshub.model.Product
import com.example.saleshub.viewmodel.ProductViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextAlign
import com.example.saleshub.model.Screen
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material.*
import androidx.compose.material.icons.filled.AddCircle
import com.example.saleshub.views.home.pieBotones

@Composable
fun ViewInventoryScreenContent(
    navController: NavController,
    productViewModel: ProductViewModel,
    modifier: Modifier = Modifier
) {
    val productList by productViewModel.productListState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Todo") } // Estado para el filtro

    val filteredProducts = when (selectedFilter) {
        "Adicional" -> productList.filter { it.type == "Adicional" }
        "Alimento" -> productList.filter { it.type == "Alimento" }
        else -> productList // Por defecto, muestra todos los productos
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding()
        ,
        topBar = {
            HeaderViewInventoryContent(navController)

        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .padding(bottom = 70.dp),
                onClick = {
                    navController.navigate(Screen.RegisterProduct.route)
                },
                backgroundColor = colorResource(id = R.color.orangeButton) // Color del FAB
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar nuevo producto",
                    tint = Color.White
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .background(Color.White)
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                iconInventory()
                ordenarProductos(selectedFilter, { filter ->
                    selectedFilter = filter // Actualiza el filtro seleccionado
                })
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 34.dp)
                        .padding(bottom = 90.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    ViewInventoryContent(
                        productList = filteredProducts,
                        onDelete = { productViewModel.deleteProduct(it.id) },
                        onEdit = { product ->
                            navController.navigate("edit_product/${product.id}")
                        },
                        onAddStock = { product ->
                            navController.navigate("update_stock/${product.id}")
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 1.dp)
                    )
                }
                pieBotones(navController)
            }
        }
    )
}

@Composable
fun ordenarProductos(selectedFilter: String, onFilterSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón "Todo"
            FilterButton("Todo", selectedFilter, onFilterSelected)
            Spacer(modifier = Modifier.width(8.dp))
            // Botón "Adicional"
            FilterButton("Adicional", selectedFilter, onFilterSelected)
            Spacer(modifier = Modifier.width(8.dp))
            // Botón "Alimento"
            FilterButton("Alimento", selectedFilter, onFilterSelected)
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
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = if (isSelected) Color.White else Color.DarkGray
            )
        }

    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewInventoryContent(
    productList: List<Product>,
    onDelete: (Product) -> Unit,
    onEdit: (Product) -> Unit,
    onAddStock: (Product) -> Unit,  // Nueva función para añadir stock
    modifier: Modifier = Modifier
) {

    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    if (productList.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp, 12.dp, 22.dp, 12.dp)
        ) {
            Text(
                text = "No hay productos registrados",
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
        ) {
            items(productList, key = { it.id }) { product ->
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        when (it) {
                            DismissValue.DismissedToStart -> {
                                productToDelete = product
                                showDialog = true
                            }
                            DismissValue.DismissedToEnd -> {
                                productToEdit = product
                                onEdit(productToEdit!!)
                            }
                            else -> false
                        }
                        false
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                    background = { SwipeBackground(dismissState.dismissDirection) },
                    dismissContent = {
                        ProductItem(
                            product = product,
                            onAddStockClick = { onAddStock(product) }  // Pasamos el producto al hacer clic
                        )
                    }
                )
            }
        }

        if (showDialog && productToDelete != null) {
            ConfirmDeleteDialog(
                productName = productToDelete!!.name,
                productDesc = productToDelete!!.description,
                onConfirmDelete = {
                    onDelete(productToDelete!!)
                    productToDelete = null
                    showDialog = false
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }
}


@Composable
fun SwipeBackground(dismissDirection: DismissDirection?) {
    val backgroundColor = when (dismissDirection) {
        DismissDirection.EndToStart -> Color.Red   // Fondo rojo para eliminar
        DismissDirection.StartToEnd -> colorResource(id = R.color.orangeButtonAdd)  // Fondo azul para editar
        else -> Color.Transparent
    }

    val icon = when (dismissDirection) {
        DismissDirection.EndToStart -> Icons.Default.Delete  // Icono de eliminar
        DismissDirection.StartToEnd -> Icons.Default.Edit    // Icono de editar
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
            .shadow(1.dp, shape = RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 20.dp),
        contentAlignment = if (dismissDirection == DismissDirection.EndToStart) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = if (dismissDirection == DismissDirection.EndToStart) "Eliminar producto" else "Editar producto",
                tint = Color.White
            )
        }
    }
}


@Composable
fun ProductItem(product: Product, onAddStockClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_buttons)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(2.dp, shape = RoundedCornerShape(10.dp)),
    ) {
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ){
            Row (
                modifier = Modifier
                    .width(70.dp)
                    .background(colorResource(id = R.color.topProduct))
                    .padding(vertical = 2.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ){
                if (product.type == "Adicional"){
                    Text("Adicional",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                }
                else{
                    Text("Alimento",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 2.dp)
                .padding(bottom = 12.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {

                Row {
                    Text("${product.name}", fontWeight = FontWeight.Bold)
                    Text(
                        "  $${product.price}",
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                if (product.stock ?: 0 < product.stockmin) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Stock bajo",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(0.dp)
                    )
                }
            }
            if (product.type == "Adicional") {
                Spacer(modifier = Modifier.height(6.dp))
            }else{
                Spacer(modifier = Modifier.height(12.dp))

            }

            Text(
                "${product.description}",
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Gray
            )
            if (product.type == "Adicional"){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Stock mínimo: ${product.stockmin}",
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Alinear verticalmente el ícono y el texto

                        ) {
                        Text(
                            "${product.stock ?: 0}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 4.dp),

                            )
                        IconButton(
                            onClick = onAddStockClick,
                            modifier = Modifier
                                    .size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "Añadir stock",
                                tint = colorResource(id = R.color.orangeButtonAdd),
                                modifier = Modifier.fillMaxSize() // Asegúrate de que el icono llene el botón
                            )
                        }


                    }
                }
            }

        }
    }
}



@Composable
fun ConfirmDeleteDialog(
    productName: String,
    productDesc: String,
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
                    text = "Eliminar producto",
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
                    text = "\"$productName : $productDesc\" ",
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


@Composable
fun pieBotonesInventario(navController: NavController, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                colorResource(id = R.color.light_gris),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomPieButtonInventario(
            text = "Inventario",
            iconResId = R.drawable.inventario,
            onClick = {  }
        )
        CustomPieButtonInventario(
            text = "Ventas",
            iconResId = R.drawable.ventas,
            onClick = { navController.navigate(Screen.SalesModule.route) }
        )
        CustomPieButtonInventario(
            text = "Cuentas",
            iconResId = R.drawable.cuentas,
            onClick = { navController.navigate(Screen.AccountsModule.route) }
        )
    }
}


@Composable
fun CustomPieButtonInventario(
    text: String,
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier.size(110.dp, 70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            androidx.compose.material3.Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
        }
    }
}



@Composable
fun HeaderViewInventoryContent(navController: NavController, modifier: Modifier = Modifier) {
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
                text = "Módulo inventario",
                fontSize = 18.sp,
                color = Color.DarkGray,
            )
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Volver",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)

    }

}

@Composable
fun iconInventory(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
                .padding(bottom = 8.dp)
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.inventario),
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp)
                    .height(1.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}
