package com.example.appdecontroledepedidoseclientes.ui.screens

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
import java.text.SimpleDateFormat
import java.util.*

/**
 * --- O QUE É ESTA TELA? ---
 * Esta e a tela de Pedidos. Ela e a mais complexa porque relaciona Clientes e Produtos.
 * Aqui registramos as vendas realizadas no sistema.
 */

// @Composable: Avisa ao Android que esta funcao cria elementos visuais.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoScreen(
    navController: NavController, // navController: Gerencia a navegacao entre telas.
    viewModel: PedidoViewModel    // viewModel: Contem a logica de pedidos e dados de clientes/produtos.
) {
    // collectAsState: Transforma os dados do banco em estados que o Compose "vigia".
    val pedidos by viewModel.pedidosComDetalhes.collectAsState()
    val clientes by viewModel.clientes.collectAsState()
    val produtos by viewModel.produtos.collectAsState()

    // remember { mutableStateOf(...) }: Cria variaveis que a tela "lembra" o valor.
    var showDialog by remember { mutableStateOf(false) } // Controla o pop-up de novo pedido.
    var showDeleteConfirm by remember { mutableStateOf<Pedido?>(null) } // Controla o pop-up de exclusao.
    
    // Variaveis para guardar as escolhas do usuario no formulario.
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var selectedProduto by remember { mutableStateOf<Produto?>(null) }
    var quantidade by remember { mutableStateOf("") }
    var dataStr by remember { mutableStateOf("") }
    var horaStr by remember { mutableStateOf("") }

    // Estados para os seletores de Data e Hora (componentes prontos do Android).
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    // Scaffold: Estrutura basica da tela (Barra superior e corpo).
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.title_orders), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_desc_back))
                    }
                }
            )
        },
        floatingActionButton = {
            // Botao flutuante para abrir o formulario de novo pedido.
            ExtendedFloatingActionButton(
                onClick = { 
                    selectedCliente = null; selectedProduto = null; quantidade = ""
                    dataStr = ""; horaStr = ""
                    showDialog = true 
                },
                icon = { Icon(Icons.Filled.PostAdd, stringResource(R.string.content_desc_add)) },
                text = { Text(stringResource(R.string.btn_new_order)) },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { padding -> // padding: Espaco interno para o conteudo nao bater na barra superior.
        
        // Se nao houver pedidos, mostra um icone e texto de lista vazia.
        if (pedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ReceiptLong, 
                        null, 
                        modifier = Modifier.size(64.dp), 
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.empty_orders), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            // LazyColumn: Lista que so carrega o que aparece na tela (eficiente).
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // items: Cria um PedidoCard para cada pedido na lista.
                items(pedidos) { item ->
                    PedidoCard(
                        pedidoComDetalhes = item,
                        onDelete = { showDeleteConfirm = item.pedido }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // AlertDialog: Pop-up para registrar um novo pedido.
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.dialog_new_order), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                        
                        // Menu Dropdown para selecionar o Cliente.
                        var clienteExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = clienteExpanded,
                            onExpandedChange = { clienteExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCliente?.nome ?: "",
                                onValueChange = {},
                                readOnly = true, // Usuario so pode escolher da lista, nao digitar.
                                label = { Text(stringResource(R.string.label_client)) },
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clienteExpanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
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

                        // Menu Dropdown para selecionar o Produto.
                        var produtoExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = produtoExpanded,
                            onExpandedChange = { produtoExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedProduto?.nome ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.label_product)) },
                                leadingIcon = { Icon(Icons.Default.ShoppingCart, null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = produtoExpanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = produtoExpanded,
                                onDismissRequest = { produtoExpanded = false }
                            ) {
                                produtos.forEach { produto ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.label_product_with_price, produto.nome, produto.valor)) },
                                        onClick = {
                                            selectedProduto = produto
                                            produtoExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Campo de texto para a Quantidade.
                        OutlinedTextField(
                            value = quantidade, 
                            onValueChange = { quantidade = it }, 
                            label = { Text(stringResource(R.string.label_quantity)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.Numbers, null) }
                        )

                        // Linha com botoes que abrem seletores de Data e Hora.
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dataStr,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.label_date)) },
                                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                                modifier = Modifier.weight(1f).clickable { showDatePicker = true },
                                shape = RoundedCornerShape(16.dp),
                                enabled = false, // Desabilitado para digitar, usuario deve usar o calendario.
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
                                label = { Text(stringResource(R.string.label_time)) },
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
                            // Valida se todos os campos foram preenchidos.
                            if (selectedCliente != null && selectedProduto != null && qtd > 0 && dataStr.isNotEmpty() && horaStr.isNotEmpty()) {
                                val ped = Pedido(
                                    clienteId = selectedCliente!!.id,
                                    produtoId = selectedProduto!!.id,
                                    quantidade = qtd,
                                    data = dataStr,
                                    hora = horaStr,
                                    valorTotal = selectedProduto!!.valor * qtd
                                )
                                viewModel.insert(ped) // Salva no banco de dados.
                                showDialog = false
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(stringResource(R.string.btn_finish)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
                }
            )
        }

        // DatePickerDialog: O calendario nativo do Android.
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
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.btn_cancel)) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Pop-up para selecionar a Hora.
        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        horaStr = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }) { Text(stringResource(R.string.btn_ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.btn_cancel)) }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }

        // Pop-up de confirmacao para excluir um pedido.
        if (showDeleteConfirm != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text(stringResource(R.string.dialog_delete_order_title), fontWeight = FontWeight.Bold) },
                text = { Text(stringResource(R.string.dialog_delete_order_text)) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.delete(showDeleteConfirm!!)
                            showDeleteConfirm = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(stringResource(R.string.btn_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) { Text(stringResource(R.string.content_desc_back)) }
                }
            )
        }
    }
}

/**
 * --- O QUE É ISTO? ---
 * Componente que desenha o cartao visual de cada pedido na lista.
 */
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
                    // Avatar circular para o icone de pessoa.
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
                            text = stringResource(R.string.order_date_time, pedidoComDetalhes.pedido.data, pedidoComDetalhes.pedido.hora),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Botao de excluir (lixeirinha).
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, stringResource(R.string.content_desc_delete), tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Area com fundo diferente para destacar os itens e o valor total.
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
                            text = stringResource(R.string.order_quantity_label, pedidoComDetalhes.pedido.quantidade),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // Valor total do pedido formatado.
                    Text(
                        text = stringResource(R.string.currency_format, pedidoComDetalhes.pedido.valorTotal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
