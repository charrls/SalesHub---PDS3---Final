package com.example.saleshub.views.accountsmodule

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.model.Client
import com.example.saleshub.model.Screen
import com.example.saleshub.viewmodel.ClientViewModel
import com.example.saleshub.views.home.pieBotones
import com.example.saleshub.views.inventorymodule.FilterButton
import com.example.saleshub.views.inventorymodule.SwipeBackground


@Composable
fun ViewAccountsScreenContent(
    navController: NavController,
    clientViewModel: ClientViewModel,
    modifier: Modifier = Modifier
) {
    val clientList by clientViewModel.clientListState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Todo") } // Estado para el filtro

    val filteredClients = when (selectedFilter) {
        "Con deuda" -> clientList.filter { it.balance!! > 0 }
        "Sin deuda" -> clientList.filter { it.balance!! <= 0 }
        else -> clientList // Por defecto, muestra todos los clientes
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            encabezadoModuloCuentas(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .padding(bottom = 70.dp),
                onClick = {
                    navController.navigate(Screen.RegisterClient.route)
                },
                backgroundColor = colorResource(id = R.color.purpleButton) // Color del FAB
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar nuevo cliente",
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
                iconAccounts()
                ordenarClientes(navController,selectedFilter, { filter ->
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
                    ViewAccountsContent(
                        clientList = filteredClients,
                        onDelete = { clientViewModel.deleteClient(it.id) },
                        onEdit = { client ->
                            navController.navigate("edit_client/${client.id}")
                        },
                        onAddPayment = { client ->
                            navController.navigate("add_payment/${client.id}")
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
fun encabezadoModuloCuentas(navController: NavController, modifier: Modifier = Modifier) {
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
                text = "Módulo clientes",
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
fun ordenarClientes(navController: NavController,selectedFilter: String, onFilterSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
        Row {
            FilterButton("Todo", selectedFilter, onFilterSelected)
            Spacer(modifier = Modifier.width(8.dp))
            FilterButton("Con deuda", selectedFilter, onFilterSelected)
            Spacer(modifier = Modifier.width(8.dp))
            FilterButton("Sin deuda", selectedFilter, onFilterSelected)
        }
            Row {
                IconButton(
                    onClick = { navController.navigate(Screen.Deadlines.route) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Registrar pago",
                        tint = colorResource(id = R.color.grayButton),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
    }}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewAccountsContent(
    clientList: List<Client>,
    onDelete: (Client) -> Unit,
    onEdit: (Client) -> Unit,
    onAddPayment: (Client) -> Unit,
    modifier: Modifier = Modifier
) {

    var clientToDelete by remember { mutableStateOf<Client?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    if (clientList.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp, 12.dp, 22.dp, 12.dp)
        ) {
            Text(
                text = "No hay clientes registrados",
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
        ) {
            items(clientList, key = { it.id }) { client ->
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        when (it) {
                            DismissValue.DismissedToStart -> {
                                clientToDelete = client
                                showDialog = true
                            }
                            DismissValue.DismissedToEnd -> {
                                onEdit(client)
                            }
                            else -> false
                        }
                        false
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                    background = { SwipeBackgroundC(dismissState.dismissDirection) },
                    dismissContent = {
                        ClientItem(
                            client = client,
                            onAddPaymentClick = { onAddPayment(client) }
                        )
                    }
                )
            }
        }

        if (showDialog && clientToDelete != null) {
            ConfirmDeleteDialog(
                clientName = clientToDelete!!.name,
                clientDesc = clientToDelete!!.phone,
                onConfirmDelete = {
                    onDelete(clientToDelete!!)
                    clientToDelete = null
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
fun SwipeBackgroundC(dismissDirection: DismissDirection?) {
    val backgroundColor = when (dismissDirection) {
        DismissDirection.EndToStart -> Color.Red   // Fondo rojo para eliminar
        DismissDirection.StartToEnd -> colorResource(id = R.color.purpleButton)  // Fondo azul para editar
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
            androidx.compose.material.Icon(
                imageVector = it,
                contentDescription = if (dismissDirection == DismissDirection.EndToStart) "Eliminar producto" else "Editar producto",
                tint = Color.White
            )
        }
    }
}
@Composable
fun ConfirmDeleteDialog(
    clientName: String,
    clientDesc: String,
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
                androidx.compose.material.Text(
                    text = "Eliminar cliente",
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
                androidx.compose.material.Text(
                    text = "\"$clientName - $clientDesc\" ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material.Text(
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
                androidx.compose.material.Divider(color = Color.LightGray, thickness = 1.dp)
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
                        androidx.compose.material.Text(
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
                        androidx.compose.material.Text(
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
fun ClientItem(client: Client, onAddPaymentClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(2.dp, shape = RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_buttons))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row {
                    Text("${client.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                if (client.balance!! > client.maxAmount!!) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Límite de crédito superado",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(0.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "${client.phone}",
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                    Text(
                        "Saldo: $${client.balance}",
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    IconButton(
                        onClick = onAddPaymentClick,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Registrar pago",
                            tint = colorResource(id = R.color.purpleButton),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }


@Composable
fun pieBotonesCuentas(navController: NavController, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                colorResource(id = R.color.light_gris),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomPieButtonCuentas(
            text = "Inventario",
            iconResId = R.drawable.inventario,
            onClick = { navController.navigate(Screen.viewInventoryContent.route) }
        )
        CustomPieButtonCuentas(
            text = "Ventas",
            iconResId = R.drawable.ventas,
            onClick = { navController.navigate(Screen.SalesModule.route) }
        )
        CustomPieButtonCuentas(
            text = "Cuentas",
            iconResId = R.drawable.cuentas,
            onClick = {  }
        )
    }
}

@Composable
fun CustomPieButtonCuentas(
    text: String,
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
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
            Text(
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
fun iconAccounts(modifier: Modifier = Modifier) {
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
                painter = painterResource(id = R.drawable.cuentas),
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
            androidx.compose.material.Divider(
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



