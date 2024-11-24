package com.example.saleshub.views.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.model.Screen



@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // HeaderContent ocupa un 35% de la pantalla
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f), // 35% de la pantalla
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            HeaderContent(navController, Modifier.fillMaxWidth())
        }

        // Content ocupa el resto (65% de la pantalla)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f), // 65% de la pantalla
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Content(navController)
        }

        // pieBotones al final
        pieBotones(navController)
    }
}




@Composable
fun HeaderContent(navController: NavController, modifier: Modifier = Modifier) {

    Column (
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(
                colorResource(id = R.color.lightNavigation),
                shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
            )
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
            )
            .padding(top = 45.dp)



    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.store),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
            Column (
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Gestor de ventas",
                    fontSize = 20.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text(
                    text = "Tiendita unison",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 10.dp)

                )
            }
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "HOY",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "$ 0.0",
                fontSize = 24.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "En 0 ventas completadas",
                fontSize = 16.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { navController.navigate(Screen.RegisterSale.route) }, // Trigger the confirmation dialog
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.greenButton)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 25.dp)
                    .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Registrar venta", color = Color.White, fontWeight = FontWeight.Bold,
                )
            }
        }
    }

}



@Composable
fun Content(navController: NavController, modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(50.dp))
    Column (
        modifier = Modifier.fillMaxWidth()
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .padding(horizontal = 20.dp)
                .border(
                    BorderStroke(1.dp, colorResource(id = R.color.topProduct)),
                    shape = RoundedCornerShape(10.dp)
                ),
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .padding(horizontal = 10.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(text = "Stock", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Stock bajo",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(0.dp)
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(40.dp)) // Aquí defines el redondeo
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(colorResource(id = R.color.lightNavigation))
                    .padding(10.dp)

                ,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Usa el mismo ancho para cada elemento
                val commonModifier = Modifier.weight(1f)

                Text(
                    text = "Productos agotados",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold,
                    modifier = commonModifier,
                    textAlign = TextAlign.Center
                )

                ProductCardInfo(
                    producto = "Producto 1",
                    descripcion = "Desc",
                    stock = 0,
                    onClick = {
                        navController.navigate(Screen.viewInventoryContent.route)
                    },
                    modifier = commonModifier
                )
                ProductCardInfo(
                    producto = "Producto 2",
                    descripcion = "Desc",
                    stock = 0,
                    onClick = {
                        navController.navigate(Screen.viewInventoryContent.route)
                    },
                    modifier = commonModifier
                )
                ProductCardInfo(
                    producto = "Producto 3",
                    descripcion = "Desc",
                    stock = 0,
                    onClick = {
                        navController.navigate(Screen.viewInventoryContent.route)
                    },
                    modifier = commonModifier
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .padding(horizontal = 20.dp)
                .border(
                    BorderStroke(1.dp, colorResource(id = R.color.topProduct)),
                    shape = RoundedCornerShape(10.dp)
                ),
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .padding(horizontal = 10.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(text = "Clientes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Stock bajo",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(0.dp)
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(40.dp)) // Aquí defines el redondeo
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(colorResource(id = R.color.lightNavigation))
                    .padding(10.dp)

                ,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Usa el mismo ancho para cada elemento
                val commonModifier = Modifier.weight(1f)

                Text(
                    text = "Clientes a pagar",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold,
                    modifier = commonModifier,
                    textAlign = TextAlign.Center
                )

                ClientCardInfo(
                    nombre = "Cliente 1",
                    telefono = "Telefono",
                    balance = 99.0,
                    onClick = {
                        navController.navigate(Screen.AccountsModule.route)
                    },
                    modifier = commonModifier
                )
                ClientCardInfo(
                    nombre = "Cliente 2",
                    telefono = "Telefono",
                    balance = 99.0,
                    onClick = {
                        navController.navigate(Screen.AccountsModule.route)
                    },
                    modifier = commonModifier
                )
                ClientCardInfo(
                    nombre = "Cliente 3",
                    telefono = "Telefono",
                    balance = 99.0,
                    onClick = {
                        navController.navigate(Screen.AccountsModule.route)
                    },
                    modifier = commonModifier
                )
            }
        }
    }

}

@Composable
fun ProductCardInfo(
    producto: String,
    descripcion: String,
    stock: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth() // Asegura que tome todo el ancho proporcionado por el `weight`
            .aspectRatio(1f), // Hace que el ancho y alto sean iguales (cuadrado)
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, colorResource(id = R.color.topProduct)),
        onClick = onClick
    ) {
        Column(

            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(8.dp),

            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Text(
                text = producto,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = descripcion,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = Color.DarkGray
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text ="Stock: ",
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.DarkGray
                )
                Text(
                    text =stock.toString(),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }

        }
    }
}

@Composable
fun ClientCardInfo(
    nombre: String,
    telefono: String,
    balance: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth() // Asegura que tome todo el ancho proporcionado por el `weight`
            .aspectRatio(1f), // Hace que el ancho y alto sean iguales (cuadrado)
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, colorResource(id = R.color.topProduct)),
        onClick = onClick
    ) {
        Column(

            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(8.dp),

            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Text(
                text = nombre,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = telefono,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = Color.DarkGray
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text ="$ " + balance.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,

                )
            }

        }
    }
}





@Composable
fun pieBotones(navController: NavController, modifier: Modifier = Modifier) {
    // Obtener la entrada actual del back stack
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .background(
                colorResource(id = R.color.light_gris),
            )
            .shadow(0.5.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomPieButton(
                text = "Inicio",
                iconResId = R.drawable.home,
                isSelected = currentRoute == Screen.Home.route, // Compara la ruta actual
                onClick = {
                    navController.navigate(Screen.Home.route)
                }
            )
            CustomPieButton(
                text = "Ventas",
                iconResId = R.drawable.ventas,
                isSelected = currentRoute == Screen.SalesModule.route, // Compara la ruta actual
                onClick = {
                    navController.navigate(Screen.SalesModule.route)
                }
            )
            CustomPieButton(
                text = "Inventario",
                iconResId = R.drawable.inventario,
                isSelected = currentRoute == Screen.viewInventoryContent.route, // Compara la ruta actual
                onClick = {
                    navController.navigate(Screen.viewInventoryContent.route)
                }
            )
            CustomPieButton(
                text = "Cuentas",
                iconResId = R.drawable.cuentas,
                isSelected = currentRoute == Screen.AccountsModule.route, // Compara la ruta actual
                onClick = {
                    navController.navigate(Screen.AccountsModule.route)
                }
            )
        }
    }
}

@Composable
fun CustomPieButton(
    text: String,
    iconResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(90.dp, 70.dp)
            .then(modifier),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.LightGray else Color.Transparent, // Fondo según el estado
            contentColor = if (isSelected) Color.White else Color.DarkGray // Texto e ícono según el estado
        )

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(if (isSelected) 31.dp else 28.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = if (isSelected) 12.sp else 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
            )
        }
    }
}




@Preview
@Composable
private fun homeScreenPrev() {
    HomeScreen(rememberNavController())
}