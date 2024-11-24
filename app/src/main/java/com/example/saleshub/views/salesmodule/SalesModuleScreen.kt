package com.example.saleshub.views.salesmodule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.model.Screen
import com.example.saleshub.views.home.pieBotones


@Composable
fun SalesModuleScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            encabezadoModuloVentas(navController, Modifier.fillMaxWidth())
            Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(30.dp))

            contenidoModuloVentas(navController)
        }
        pieBotones(navController)
    }
}


@Composable
fun encabezadoModuloVentas(navController: NavController, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .background( colorResource(id = R.color.light_gris))
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
            text = "Módulo ventas",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
        )
        IconButton(
            onClick = {  },
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Volver",
                tint = Color.DarkGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun contenidoModuloVentas(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row (
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp),
            horizontalArrangement = Arrangement.Start)
        {
            Image(
                painter = painterResource(id = R.drawable.ventas),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
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

        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { navController.navigate(Screen.RegisterSale.route) },
            modifier = Modifier
                .width(200.dp)
                .height(60.dp)
                .shadow(3.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.light_buttons))
        ) {
            Text(
                text = "Registrar ventas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.DarkGray
            )
        }

        Button(
            onClick = { navController.navigate(Screen.SalesHistory.route)},
            modifier = Modifier
                .width(200.dp)
                .height(60.dp)
                .shadow(3.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.light_buttons))
        ) {
            Text(
                text = "Historial de ventas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.DarkGray
            )
        }
    }
}


@Composable
fun pieBotonesVentas(navController: NavController, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                colorResource(id = R.color.light_gris),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomPieButtonVentas(
            text = "Inventario",
            iconResId = R.drawable.inventario,
            onClick = { navController.navigate(Screen.viewInventoryContent.route) }
        )
        CustomPieButtonVentas(
            text = "Ventas",
            iconResId = R.drawable.ventas,
            onClick = {  }
        )
        CustomPieButtonVentas(
            text = "Cuentas",
            iconResId = R.drawable.cuentas,
            onClick = { navController.navigate(Screen.AccountsModule.route) }
        )
    }
}


@Composable
fun CustomPieButtonVentas(
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


@Preview
@Composable
private fun salesmoduleprev() {
    val navController = rememberNavController()
    SalesModuleScreen(navController = navController)
}