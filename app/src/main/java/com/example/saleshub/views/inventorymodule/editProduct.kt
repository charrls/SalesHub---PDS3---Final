import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.R
import com.example.saleshub.model.Product
import com.example.saleshub.viewmodel.ProductViewModel
import kotlinx.coroutines.launch


@Composable
fun EditProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    modifier: Modifier = Modifier,
    isFromSwipe: Boolean = false, // Indica si la navegación es desde swipe-to-edit
    productId: String? = null // ID del producto para rellenar automáticamente
) {
    val productList by productViewModel.productListState.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    // Usar un data class para manejar el estado del formulario
    var productFormState by remember { mutableStateOf(ProductFormState()) }
    var showConfirmDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo de confirmación

    // Estado del Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Inicializar el producto si venimos de swipe
    if (isFromSwipe && productId != null) {
        val foundProduct = productList.find { it.id == productId.toInt() }

        // Solo actualizar si hay un producto encontrado y es diferente del actual
        if (foundProduct != null && foundProduct != selectedProduct) {
            selectedProduct = foundProduct

            // Actualiza el estado del formulario con el producto encontrado
            productFormState = ProductFormState(
                name = foundProduct.name,
                description = foundProduct.description,
                price = foundProduct.price.toString(),
                stock = foundProduct.stock?.toString() ?: "",
                stockMin = foundProduct.stockmin.toString(),
                type = foundProduct.type
            )
        }
    }
    // Usar Scaffold para la estructura básica
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            HeaderEditInventory(navController, Modifier.fillMaxWidth())
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
                    iconEInventory()

                    // Card con la información del producto que se está editando
                    selectedProduct?.let {
                        ProductInfoCard(it)
                    }
                    // Formulario para editar el producto
                    EditProductForm(
                        productName = productFormState.name,
                        onProductNameChange = { productFormState = productFormState.copy(name = it) },
                        productDescription = productFormState.description,
                        onProductDescriptionChange = { productFormState = productFormState.copy(description = it) },
                        productPrice = productFormState.price,
                        onProductPriceChange = { productFormState = productFormState.copy(price = it) },
                        productStock = productFormState.stock,
                        onProductStockChange = { productFormState = productFormState.copy(stock = it) },
                        productStockMin = productFormState.stockMin,
                        onProductStockMinChange = { productFormState = productFormState.copy(stockMin = it) },
                        productType = productFormState.type,
                        stockError = validateProductForm(productFormState).stockError, // Mensaje de error de stock
                        stockMinError = validateProductForm(productFormState).stockMinError, // Mensaje de error de stock mínimo
                        nameError = validateProductForm(productFormState).nameError, // Mensaje de error de nombre
                        priceError = validateProductForm(productFormState).priceError // Mensaje de error de precio
                    )
                }
                // Botón de actualizar
                val validatedFormState = validateProductForm(productFormState)
                FootUpdateButtons(
                    onUpdateClicked = {
                        if (validatedFormState.isValid && isFormChanged(productFormState, selectedProduct)) {
                            showConfirmDialog = true
                        } else {
                            validatedFormState.errorMessages.forEach { errorMessage ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = errorMessage,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    },
                    onCancelClicked = { navController.popBackStack() },
                    isUpdateEnabled = validatedFormState.isValid && isFormChanged(productFormState, selectedProduct)
                )
            }
        }
    )
    // AlertDialog de confirmación para actualizar el producto
    if (showConfirmDialog) {
        ConfirmUpdateDialog(
            productName = productFormState.name,
            onConfirmUpdate = {
                selectedProduct?.let { product ->
                    productViewModel.updateProduct(
                        Product(
                            id = product.id,
                            name = productFormState.name,
                            description = productFormState.description,
                            price = productFormState.price.toDoubleOrNull() ?: 0.0,
                            stock = productFormState.stock.toIntOrNull()?: 0,
                            stockmin = productFormState.stockMin.toIntOrNull() ?: 0,
                            type = productFormState.type
                        )
                    )
                    // Mostrar el Snackbar al actualizar el producto
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Producto actualizado con éxito",
                            duration = SnackbarDuration.Short
                        )
                    }
                    navController.popBackStack() // Regresar a la pantalla anterior
                }
                showConfirmDialog = false // Ocultar el diálogo de confirmación
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
}

// Función para verificar si el formulario ha cambiado
private fun isFormChanged(productFormState: ProductFormState, selectedProduct: Product?): Boolean {
    return selectedProduct?.let {
        it.name != productFormState.name ||
                it.description != productFormState.description ||
                it.price.toString() != productFormState.price ||
                it.stock.toString() != productFormState.stock ||
                it.stockmin.toString() != productFormState.stockMin
    } ?: false
}

@Composable
fun ProductInfoCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Cambia el color de fondo aquí
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

// Otras funciones no cambiaron...
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
fun EditProductForm(
    productName: String,
    onProductNameChange: (String) -> Unit,
    productDescription: String,
    onProductDescriptionChange: (String) -> Unit,
    productPrice: String,
    onProductPriceChange: (String) -> Unit,
    productStock: String,
    onProductStockChange: (String) -> Unit,
    productStockMin: String,
    onProductStockMinChange: (String) -> Unit,
    productType: String,
    stockError: String?, // Mensaje de error de stock
    stockMinError: String?, // Mensaje de error de stock mínimo
    nameError: String?, // Mensaje de error de nombre
    priceError: String?, // Mensaje de error de precio
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 40.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Producto")
        OutlinedTextField(
            value = productName,
            onValueChange = onProductNameChange,
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            isError = nameError != null,
            singleLine = true
        )
        nameError?.let {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(25.dp))

        Text(text = "Descripción")
        OutlinedTextField(
            value = productDescription,
            onValueChange = onProductDescriptionChange,
            label = { Text("Descripción del producto") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(25.dp))

        if (productType == "Adicional") {
            Text(text = "Stock mínimo")
            OutlinedTextField(
                value = productStockMin,
                onValueChange = onProductStockMinChange,
                label = { Text("Stock mínimo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = stockMinError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            stockMinError?.let {
                Text(
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
            )  }
            Spacer(modifier = Modifier.height(25.dp))
        }

        Text(text = "Precio")
        OutlinedTextField(
            value = productPrice,
            onValueChange = onProductPriceChange,
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            isError = priceError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        priceError?.let {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
    }
}

private fun validateProductForm(productFormState: ProductFormState): ProductFormValidationState {
    val errorMessages = mutableListOf<String>()
    var isValid = true
    // Validar el nombre del producto
    val nameError = if (productFormState.name.isBlank()) {
        isValid = false
        "Campo obligatorio"
    } else if (productFormState.name.length > 30) {
        isValid = false
        "Maximo 30 caracteres"
    } else {
        null
    }
    // Validar el precio
    val price = productFormState.price.toDoubleOrNull()
    val priceError = if (productFormState.price.isBlank()) {
        isValid = false
        "Campo obligatorio"
    }else if (price == null || price <= 0) {
        isValid = false
        "Dato no válido"
    } else {
        null
    }
    // Validar el stock (solo si es un tipo 'Adicional')
    val stock = productFormState.stock.toIntOrNull()
    val stockError = if (productFormState.type == "Adicional" && (stock == null || stock < 0)) {
        isValid = false
        "Dato no valido"
    } else {
        null
    }
    // Validar el stock mínimo (solo si es un tipo 'Adicional')
    val stockMin = productFormState.stockMin.toIntOrNull()
    val stockMinError = if (productFormState.type == "Adicional" && (productFormState.stockMin.isBlank())){
        isValid = false
        "Campo obligatorio"
    } else if (productFormState.type == "Adicional" && (stockMin == null || stockMin < 0)) {
        isValid = false
        "Dato no valido"
    } else {
        null
    }
    return ProductFormValidationState(
        isValid = isValid,
        stockError = stockError,
        stockMinError = stockMinError,
        nameError = nameError, // Asegúrate de incluir este campo
        priceError = priceError, // Asegúrate de incluir este campo
        errorMessages = errorMessages
    )
}

data class ProductFormState(
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val stock: String = "",
    val stockMin: String = "",
    val type: String = ""
)

data class ProductFormValidationState(
    val isValid: Boolean,
    val stockError: String?,
    val stockMinError: String?,
    val nameError: String?, // Agregar este campo
    val priceError: String?, // Agregar este campo
    val errorMessages: List<String>
)

@Composable
fun FootUpdateButtons(
    onUpdateClicked: () -> Unit,
    onCancelClicked: () -> Unit, // Nueva propiedad para manejar la acción de cancelar
    isUpdateEnabled: Boolean, // Propiedad existente para habilitar/deshabilitar
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
            onClick = onCancelClicked, // Llama a la función onCancelClicked
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
            onClick = onUpdateClicked,
            enabled = isUpdateEnabled, // Deshabilitar el botón si no hay cambios
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.orangeButton)),
            modifier = Modifier
                .height(55.dp)
                .weight(1f)
                .padding(end = 10.dp)
                .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text("Actualizar", color = Color.White)
        }
    }
}

@Composable
fun ConfirmUpdateDialog(
    productName: String,
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
                    text = "Actualizar producto",
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
                    text = "\"$productName\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "¿Desea actualizar los datos del producto?",
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
                    // Botón de "Actualizar"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onConfirmUpdate()
                                onDismiss()  // Cierra el diálogo después de confirmar
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Actualizar",
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
fun HeaderEditInventory(navController: NavController, modifier: Modifier = Modifier) {

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
                text = "Editar producto",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(end = 22.dp)
            )

        }
        Divider(modifier = Modifier.padding(0.dp), thickness = 1.dp, color = Color.LightGray)

    }

}

@Composable
fun iconEInventory(modifier: Modifier = Modifier) {
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
