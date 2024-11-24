import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.model.Product
import com.example.saleshub.viewmodel.ProductViewModel


@Composable
fun UpdateStockScreen(navController: NavController, productViewModel: ProductViewModel, productId: Int) {
    val productList by productViewModel.productListState.collectAsState()
    val productToUpdate = productList.find { it.id == productId }

    var showDialog by remember { mutableStateOf(false) }
    var stockCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HeaderStockInventory(navController, Modifier.fillMaxWidth())
            iconInventory()

            productToUpdate?.let {
                ProductDetailsCard(product = it, modifier = Modifier)
            }
        }

        // Botón para añadir stock
        AddStockButton(stockCount, { newStock ->
            stockCount = newStock
        })

        // Pasamos el stockCount al botón de registrar para habilitar/deshabilitar
        FootStockButtons(
            stockCount = stockCount,
            onRegisterClick = {
                showDialog = true
            }
        )
    }

    if (showDialog && productToUpdate != null) {
        ConfirmUpdateDialog(
            productName = productToUpdate.name,
            stockCount = stockCount,
            onConfirmUpdate = {
                val currentStock = productToUpdate.stock ?: 0
                productViewModel.updateStock(productId, currentStock + stockCount)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}




@Composable
fun ProductDetailsCard(product: Product, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 26.dp),
        border = BorderStroke(1.dp, Color.Gray),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${product.name}", fontWeight = FontWeight.Bold)
                if (product.stock!! < product.stockmin) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Stock bajo",
                        tint = Color.Red
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Stock suficiente",
                        tint = colorResource(id = R.color.greenAlert)
                    )
                }
            }

            Text("${product.description}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stock mínimo: ${product.stockmin ?: 0}", fontSize = 14.sp, color = Color.Gray)

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ){

                    Text("${product.stock ?: 0}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ConfirmUpdateDialog(
    productName: String,
    stockCount: Int,
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
                    text = "Actualizar stock",
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
                    buildAnnotatedString {
                        append("¿Deseas añadir ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("$stockCount")
                        }
                        append(" al stock de ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(productName)
                        }
                        append("?")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
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

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(45.dp)
                            .background(Color.LightGray)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onConfirmUpdate()
                                onDismiss()
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Añadir",
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.orangeButton)
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
fun AddStockButton(
    stockCount: Int,
    onAddStock: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Obtenemos el color de recursos dentro del bloque composable
    val orangeButtonColor = colorResource(id = R.color.orangeButtonAdd)
    val defaultButtonColor = Color.LightGray

    // Variable de estado para el color del botón, inicialmente en gris claro
    var buttonColor by remember { mutableStateOf(defaultButtonColor) }

    // Actualizamos el color del botón según el valor de stockCount
    LaunchedEffect(stockCount) {
        buttonColor = if (stockCount > 0) {
            orangeButtonColor // Cambia a naranja si el stock es mayor que cero
        } else {
            defaultButtonColor // Vuelve al color por defecto si el stock es 0 o vacío
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(30.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text("Añadir stock", modifier = Modifier.padding(end = 52.dp, bottom = 8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Campo de texto para el stock
            OutlinedTextField(
                value = stockCount.toString(),
                onValueChange = { newValue -> onAddStock(newValue.toIntOrNull() ?: 0) }, // Controla los valores vacíos o no válidos
                singleLine = true,
                modifier = Modifier.width(80.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Botón que agrega al stock y cambia el color
            Button(
                onClick = {
                    onAddStock(stockCount + 1)
                },
                modifier = Modifier
                    .width(55.dp)
                    .height(55.dp)
                    .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Incrementar")
            }
        }
    }
}


@Composable
fun FootStockButtons(
    stockCount: Int, // Recibe el valor del stock
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultButtonColor = colorResource(id = R.color.grayButton)
    val enabledButtonColor = colorResource(id = R.color.orangeButton)

    // El botón de registrar solo está habilitado si el stock es mayor a 0
    val isRegisterEnabled = stockCount > 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // Botón de cancelar (siempre habilitado)
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = defaultButtonColor),
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

        // Botón de registrar (habilitado/deshabilitado según el stockCount)
        Button(
            onClick = { onRegisterClick() },
            enabled = isRegisterEnabled, // Habilitar solo si el stock es mayor que 0
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRegisterEnabled) enabledButtonColor else defaultButtonColor
            ),
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
fun HeaderStockInventory(navController: NavController, modifier: Modifier = Modifier) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                colorResource(id = R.color.light_gris),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .shadow(1.dp, shape = RoundedCornerShape(12.dp))
            .padding(top = 48.dp)
            ,
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
            text = "Actualizar stock",
            fontSize = 20.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

@Composable
fun iconInventory(modifier: Modifier = Modifier) {
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


