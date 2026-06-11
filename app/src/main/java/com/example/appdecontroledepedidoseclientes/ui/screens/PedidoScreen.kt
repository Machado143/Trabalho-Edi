package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            TopAppBar(
                title = { Text("Gestão de Pedidos", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    selectedCliente = null; selectedProduto = null; quantidade = ""
                    dataStr = ""; horaStr = ""
                    showDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Filled.Add, "Novo Pedido") }
        }
    ) { padding ->
        if (pedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhum pedido realizado", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos) { item ->
                    PedidoCard(
                        pedidoComDetalhes = item,
                        onDelete = { showDeleteConfirm = item.pedido }
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Novo Pedido") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clienteExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
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
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = produtoExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = produtoExpanded,
                                onDismissRequest = { produtoExpanded = false }
                            ) {
                                produtos.forEach { produto ->
                                    DropdownMenuItem(
                                        text = { Text("${produto.nome} (R$ ${produto.valor})") },
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
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dataStr,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Data") },
                                leadingIcon = { Icon(Icons.Default.DateRange, null) },
                                modifier = Modifier.weight(1f).clickable { showDatePicker = true },
                                shape = RoundedCornerShape(12.dp),
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            OutlinedTextField(
                                value = horaStr,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hora") },
                                leadingIcon = { Icon(Icons.Default.MoreVert, null) },
                                modifier = Modifier.weight(1f).clickable { showTimePicker = true },
                                shape = RoundedCornerShape(12.dp),
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
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
                    }) { Text("Confirmar") }
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
                title = { Text("Confirmar Exclusão") },
                text = { Text("Tem certeza que deseja cancelar este pedido?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.delete(showDeleteConfirm!!)
                            showDeleteConfirm = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Sim, excluir") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) { Text("Não") }
                }
            )
        }
    }
}

@Composable
fun PedidoCard(pedidoComDetalhes: PedidoComDetalhes, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = pedidoComDetalhes.cliente.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Realizado em: ${pedidoComDetalhes.pedido.data} às ${pedidoComDetalhes.pedido.hora}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Excluir", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = pedidoComDetalhes.produto.nome,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Quantidade: ${pedidoComDetalhes.pedido.quantidade} un.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = "R$ ${String.format("%.2f", pedidoComDetalhes.pedido.valorTotal)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
