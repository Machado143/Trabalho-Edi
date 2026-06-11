package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.data.entity.Pedido
import com.example.appdecontroledepedidoseclientes.data.entity.Cliente
import com.example.appdecontroledepedidoseclientes.data.entity.Produto
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

    // Date/Time states
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pedidos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                selectedCliente = null; selectedProduto = null; quantidade = ""
                dataStr = ""; horaStr = ""
                showDialog = true 
            }) { Icon(Icons.Filled.Add, "Adicionar") }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(pedidos) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Cliente: ${item.cliente.nome}", style = MaterialTheme.typography.titleSmall)
                            Text("Produto: ${item.produto.nome} (x${item.pedido.quantidade})")
                            Text("Data: ${item.pedido.data} ${item.pedido.hora}")
                            Text("Total: R$ ${String.format("%.2f", item.pedido.valorTotal)}", color = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { showDeleteConfirm = item.pedido }) {
                            Icon(Icons.Filled.Delete, "Excluir")
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Novo Pedido") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Dropdown Cliente
                        var clienteExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = clienteExpanded,
                            onExpandedChange = { clienteExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCliente?.nome ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Selecionar Cliente") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clienteExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
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

                        // Dropdown Produto
                        var produtoExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = produtoExpanded,
                            onExpandedChange = { produtoExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedProduto?.nome ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Selecionar Produto") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = produtoExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = produtoExpanded,
                                onDismissRequest = { produtoExpanded = false }
                            ) {
                                produtos.forEach { produto ->
                                    DropdownMenuItem(
                                        text = { Text(produto.nome) },
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
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Date Picker Trigger
                        OutlinedTextField(
                            value = dataStr,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Data") },
                            trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, null) } },
                            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
                        )

                        // Time Picker Trigger
                        OutlinedTextField(
                            value = horaStr,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hora") },
                            trailingIcon = { IconButton(onClick = { showTimePicker = true }) { Icon(Icons.Default.MoreVert, null) } },
                            modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true }
                        )
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
                    }) { Text("Salvar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                }
            )
        }

        // DatePickerDialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val date = datePickerState.selectedDateMillis?.let {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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

        // TimePickerDialog
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
                text = { Text("Deseja realmente excluir este pedido?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.delete(showDeleteConfirm!!)
                        showDeleteConfirm = null
                    }) { Text("Sim") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) { Text("Não") }
                }
            )
        }
    }
}
