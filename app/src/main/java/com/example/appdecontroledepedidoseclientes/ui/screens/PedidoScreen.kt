package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.data.entity.Pedido
import com.example.appdecontroledepedidoseclientes.data.entity.Cliente
import com.example.appdecontroledepedidoseclientes.data.entity.Produto
import com.example.appdecontroledepedidoseclientes.data.entity.PedidoComDetalhes
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoScreen(navController: NavController, viewModel: PedidoViewModel) {
    val pedidos by viewModel.pedidosComDetalhes.collectAsState()
    val clientes by viewModel.clientes.collectAsState()
    val produtos by viewModel.produtos.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf<Pedido?>(null) }
    
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var selectedProduto by remember { mutableStateOf<Produto?>(null) }
    var quantidade by remember { mutableStateOf("") }
    var dataStr by remember { mutableStateOf("") }
    var horaStr by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Pedidos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    selectedCliente = null; selectedProduto = null; quantidade = ""
                    dataStr = ""; horaStr = ""
                    showDialog = true 
                },
                icon = { Icon(Icons.Filled.PostAdd, "Novo") },
                text = { Text("Novo Pedido") },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { padding ->
        if (pedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.ReceiptLong, 
                        null, 
                        modifier = Modifier.size(64.dp), 
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Nenhum pedido registrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(pedidos) { item ->
                    PedidoCard(
                        pedidoComDetalhes = item,
                        onDelete = { showDeleteConfirm = item.pedido }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Registrar Novo Pedido", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                        // Cliente Dropdown
                        var clienteExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = clienteExpanded,
                            onExpandedChange = { clienteExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCliente?.nome ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Cliente") },
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clienteExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = clienteExpanded,
                                onDismissRequest = { clienteExpanded = false }
                            ) {
                                clientes.forEach { cliente ->
                                    DropdownMenuItem(
                                        text = { Text(cliente.nome) },
                                        onClick = {
                                            selectedCliente = cliente
                                            clienteExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Produto Dropdown
                        var produtoExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = produtoExpanded,
                            onExpandedChange = { produtoExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedProduto?.nome ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Produto") },
                                leadingIcon = { Icon(Icons.Default.ShoppingCart, null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = produtoExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = produtoExpanded,
                                onDismissRequest = { produtoExpanded = false }
                            ) {
                                produtos.forEach { produto ->
                                    DropdownMenuItem(
                                        text = { Text("${produto.nome} (R$ ${String.format("%.2f", produto.valor)})") },
                                        onClick = {
                                            selectedProduto = produto
                                            produtoExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = quantidade, 
                            onValueChange = { quantidade = it }, 
                            label = { Text("Quantidade") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.Numbers, null) }
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dataStr,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Data") },
                                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                                modifier = Modifier.weight(1f).clickable { showDatePicker = true },
                                shape = RoundedCornerShape(16.dp),
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            OutlinedTextField(
                                value = horaStr,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hora") },
                                leadingIcon = { Icon(Icons.Default.AccessTime, null) },
                                modifier = Modifier.weight(1f).clickable { showTimePicker = true },
                                shape = RoundedCornerShape(16.dp),
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val qtd = quantidade.toIntOrNull() ?: 0
                            if (selectedCliente != null && selectedProduto != null && qtd > 0 && dataStr.isNotEmpty() && horaStr.isNotEmpty()) {
                                val ped = Pedido(
                                    clienteId = selectedCliente!!.id,
                                    produtoId = selectedProduto!!.id,
                                    quantidade = qtd,
                                    data = dataStr,
                                    hora = horaStr,
                                    valorTotal = selectedProduto!!.valor * qtd
                                )
                                viewModel.insert(ped)
                                showDialog = false
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Finalizar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val date = datePickerState.selectedDateMillis?.let {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            sdf.format(Date(it))
                        } ?: ""
                        dataStr = date
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        horaStr = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }

        if (showDeleteConfirm != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text("Cancelar Pedido", fontWeight = FontWeight.Bold) },
                text = { Text("Tem certeza que deseja remover este pedido do sistema?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.delete(showDeleteConfirm!!)
                            showDeleteConfirm = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) { Text("Voltar") }
                }
            )
        }
    }
}

@Composable
fun PedidoCard(pedidoComDetalhes: PedidoComDetalhes, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person, 
                                null, 
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = pedidoComDetalhes.cliente.nome,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${pedidoComDetalhes.pedido.data} • ${pedidoComDetalhes.pedido.hora}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, "Excluir", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pedidoComDetalhes.produto.nome,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Quantidade: ${pedidoComDetalhes.pedido.quantidade} un.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "R$ ${String.format("%.2f", pedidoComDetalhes.pedido.valorTotal)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
