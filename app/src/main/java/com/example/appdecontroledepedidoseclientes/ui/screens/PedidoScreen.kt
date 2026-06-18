package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.R
import com.example.appdecontroledepedidoseclientes.data.entity.Pedido
import com.example.appdecontroledepedidoseclientes.data.entity.Cliente
import com.example.appdecontroledepedidoseclientes.data.entity.Produto
import com.example.appdecontroledepedidoseclientes.data.entity.PedidoComDetalhes
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.PedidoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * --- TELA DE PEDIDOS ATUALIZADA ---
 * Inclui: Status Chips, Filtro, Validação, Estoque Automático, Desfazer e Animações.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoScreen(navController: NavController, viewModel: PedidoViewModel) {
    val pedidos by viewModel.pedidosComDetalhes.collectAsState()
    val clientes by viewModel.clientes.collectAsState()
    val produtos by viewModel.produtos.collectAsState()

    // Filtro de Status (Funcionalidade 7)
    var statusFilter by remember { mutableStateOf<String?>(null) }
    val filteredPedidos = remember(pedidos, statusFilter) {
        if (statusFilter == null) pedidos else pedidos.filter { it.pedido.status == statusFilter }
    }

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf<Pedido?>(null) }
    
    // Formulário
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var selectedProduto by remember { mutableStateOf<Produto?>(null) }
    var quantidade by remember { mutableStateOf("") }
    var dataStr by remember { mutableStateOf("") }
    var horaStr by remember { mutableStateOf("") }

    // Validação (UX 4)
    var clienteError by remember { mutableStateOf(false) }
    var produtoError by remember { mutableStateOf(false) }
    var qtdError by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.title_orders), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_desc_back))
                    }
                },
                actions = {
                    // Menu de Filtro (Funcionalidade 7)
                    var filterMenuExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { filterMenuExpanded = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                        }
                        DropdownMenu(expanded = filterMenuExpanded, onDismissRequest = { filterMenuExpanded = false }) {
                            DropdownMenuItem(text = { Text(stringResource(R.string.filter_status)) }, onClick = { statusFilter = null; filterMenuExpanded = false })
                            listOf("Pendente", "Em preparo", "Entregue", "Cancelado").forEach { status ->
                                DropdownMenuItem(text = { Text(status) }, onClick = { statusFilter = status; filterMenuExpanded = false })
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    selectedCliente = null; selectedProduto = null; quantidade = ""
                    dataStr = ""; horaStr = ""
                    clienteError = false; produtoError = false; qtdError = false
                    showDialog = true 
                },
                icon = { Icon(Icons.Filled.PostAdd, stringResource(R.string.content_desc_add)) },
                text = { Text(stringResource(R.string.btn_new_order)) },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { padding ->
        if (filteredPedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(if (statusFilter == null) stringResource(R.string.empty_orders) else "Nenhum pedido com status $statusFilter")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredPedidos, key = { it.pedido.id }) { item ->
                    // Swipe to Delete (Melhoria Técnica 11)
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                showDeleteConfirm = item.pedido
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent
                            Box(
                                Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)).background(color).padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                            }
                        }
                    ) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500)) { it / 2 }
                        ) {
                            PedidoCard(
                                pedidoComDetalhes = item,
                                onDelete = { showDeleteConfirm = item.pedido },
                                onStatusChange = { viewModel.updateStatus(item.pedido, it) }
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.dialog_new_order), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Cliente Dropdown
                        var clienteExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = clienteExpanded, onExpandedChange = { clienteExpanded = it }) {
                            OutlinedTextField(
                                value = selectedCliente?.nome ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.label_client)) },
                                isError = clienteError,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clienteExpanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = clienteExpanded, onDismissRequest = { clienteExpanded = false }) {
                                clientes.forEach { cliente ->
                                    DropdownMenuItem(text = { Text(cliente.nome) }, onClick = { selectedCliente = cliente; clienteExpanded = false; clienteError = false })
                                }
                            }
                        }

                        // Produto Dropdown
                        var produtoExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = produtoExpanded, onExpandedChange = { produtoExpanded = it }) {
                            OutlinedTextField(
                                value = selectedProduto?.nome ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.label_product)) },
                                isError = produtoError,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = produtoExpanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = produtoExpanded, onDismissRequest = { produtoExpanded = false }) {
                                produtos.forEach { produto ->
                                    DropdownMenuItem(text = { Text("${produto.nome} (Estoque: ${produto.quantidade})") }, onClick = { selectedProduto = produto; produtoExpanded = false; produtoError = false })
                                }
                            }
                        }

                        OutlinedTextField(
                            value = quantidade, 
                            onValueChange = { quantidade = it; qtdError = false }, 
                            label = { Text(stringResource(R.string.label_quantity)) },
                            isError = qtdError,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = dataStr, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.label_date)) }, modifier = Modifier.weight(1f).clickable { showDatePicker = true }, enabled = false)
                            OutlinedTextField(value = horaStr, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.label_time)) }, modifier = Modifier.weight(1f).clickable { showTimePicker = true }, enabled = false)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val qtd = quantidade.toIntOrNull() ?: 0
                        if (selectedCliente == null) clienteError = true
                        if (selectedProduto == null) produtoError = true
                        if (qtd <= 0 || (selectedProduto != null && qtd > selectedProduto!!.quantidade)) qtdError = true
                        
                        if (!clienteError && !produtoError && !qtdError && dataStr.isNotEmpty() && horaStr.isNotEmpty()) {
                            val ped = Pedido(
                                clienteId = selectedCliente!!.id,
                                produtoId = selectedProduto!!.id,
                                quantidade = qtd,
                                data = dataStr,
                                hora = horaStr,
                                valorTotal = selectedProduto!!.valor * qtd,
                                status = "Pendente"
                            )
                            viewModel.insert(ped)
                            showDialog = false
                        }
                    }) { Text(stringResource(R.string.btn_finish)) }
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
                    }) { Text(stringResource(R.string.btn_ok)) }
                }
            ) { DatePicker(state = datePickerState) }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        horaStr = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }) { Text(stringResource(R.string.btn_ok)) }
                },
                text = { TimePicker(state = timePickerState) }
            )
        }

        if (showDeleteConfirm != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text(stringResource(R.string.dialog_delete_order_title)) },
                text = { Text(stringResource(R.string.dialog_delete_order_text)) },
                confirmButton = {
                    Button(
                        onClick = {
                            val target = showDeleteConfirm!!
                            viewModel.delete(target)
                            showDeleteConfirm = null
                            scope.launch {
                                val result = snackbarHostState.showSnackbar("Pedido removido", "Desfazer")
                                if (result == SnackbarResult.ActionPerformed) viewModel.undoDelete()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text(stringResource(R.string.btn_confirm)) }
                }
            )
        }
    }
}

@Composable
fun PedidoCard(pedidoComDetalhes: PedidoComDetalhes, onDelete: () -> Unit, onStatusChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.tertiaryContainer) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp)) }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = pedidoComDetalhes.cliente.nome, fontWeight = FontWeight.Bold)
                        Text(text = stringResource(R.string.order_date_time, pedidoComDetalhes.pedido.data, pedidoComDetalhes.pedido.hora), style = MaterialTheme.typography.labelSmall)
                    }
                }
                
                // Status Chip (Funcionalidade 7)
                StatusChip(status = pedidoComDetalhes.pedido.status, onClick = {
                    val nextStatus = when(pedidoComDetalhes.pedido.status) {
                        "Pendente" -> "Em preparo"
                        "Em preparo" -> "Entregue"
                        "Entregue" -> "Cancelado"
                        else -> "Pendente"
                    }
                    onStatusChange(nextStatus)
                })
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = pedidoComDetalhes.produto?.nome ?: "", fontWeight = FontWeight.Bold)
                        Text(text = stringResource(R.string.order_quantity_label, pedidoComDetalhes.pedido.quantidade ?: 0), style = MaterialTheme.typography.bodySmall)
                    }
                    Text(text = stringResource(R.string.currency_format, pedidoComDetalhes.pedido.valorTotal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.End)) { Icon(Icons.Default.DeleteOutline, null, tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
fun StatusChip(status: String, onClick: () -> Unit) {
    val color = when(status) {
        "Pendente" -> Color(0xFFFFA000)
        "Em preparo" -> Color(0xFF1976D2)
        "Entregue" -> Color(0xFF388E3C)
        "Cancelado" -> Color(0xFFD32F2F)
        else -> MaterialTheme.colorScheme.outline
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(text = status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}
