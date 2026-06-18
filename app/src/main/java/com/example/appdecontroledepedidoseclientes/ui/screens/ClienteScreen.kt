package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.appdecontroledepedidoseclientes.data.entity.Cliente
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.ClienteViewModel
import kotlinx.coroutines.launch

/**
 * --- TELA DE CLIENTES FINALIZADA ---
 * Melhorias implementadas: Busca, Validação, Confirmação, Desfazer, Animações,
 * Ordenação (A-Z/Z-A) e Swipe to Delete.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteScreen(
    navController: NavController,
    viewModel: ClienteViewModel
) {
    val clientes by viewModel.clientes.collectAsState()
    
    // Estados para Busca e Ordenação (UX 3 e 12)
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isAscending by remember { mutableStateOf(true) }

    // Filtragem e Ordenação em tempo real (UX 3 e 12)
    val filteredClientes = remember(clientes, searchQuery, isAscending) {
        val filtered = clientes.filter { 
            it.nome.contains(searchQuery, ignoreCase = true) || 
            it.cidade.contains(searchQuery, ignoreCase = true) 
        }
        if (isAscending) filtered.sortedBy { it.nome } else filtered.sortedByDescending { it.nome }
    }

    // Estados do Formulário e Validação
    var showDialog by remember { mutableStateOf(false) }
    var editingCliente by remember { mutableStateOf<Cliente?>(null) }
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var nomeError by remember { mutableStateOf(false) }
    var telefoneError by remember { mutableStateOf(false) }

    // Estado para Confirmação de Exclusão
    var clienteParaExcluir by remember { mutableStateOf<Cliente?>(null) }

    // Snackbar para o "Desfazer"
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (isSearchActive) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text(stringResource(R.string.search_hint)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { isSearchActive = false; searchQuery = "" }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                )
            } else {
                MediumTopAppBar(
                    title = { Text(stringResource(R.string.title_clients), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_desc_back))
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.content_desc_search))
                        }
                        // Botão de Ordenação (UX 12)
                        IconButton(onClick = { isAscending = !isAscending }) {
                            Icon(
                                imageVector = if (isAscending) Icons.Default.SortByAlpha else Icons.Default.Sort,
                                contentDescription = "Ordenar"
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    editingCliente = null; nome = ""; telefone = ""; email = ""; cidade = ""
                    nomeError = false; telefoneError = false; showDialog = true 
                },
                icon = { Icon(Icons.Filled.PersonAdd, stringResource(R.string.content_desc_add)) },
                text = { Text(stringResource(R.string.btn_new_client)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { padding ->
        if (filteredClientes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(if (searchQuery.isEmpty()) stringResource(R.string.empty_clients) else "Nenhum resultado encontrado")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredClientes, key = { it.id }) { cliente ->
                    // Swipe to Delete (Melhoria Técnica 11)
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                clienteParaExcluir = cliente
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
                            ClienteCard(
                                cliente = cliente,
                                onEdit = {
                                    editingCliente = cliente; nome = cliente.nome; telefone = cliente.telefone
                                    email = cliente.email; cidade = cliente.cidade
                                    nomeError = false; telefoneError = false; showDialog = true
                                },
                                onDelete = { clienteParaExcluir = cliente },
                                onClick = { navController.navigate("cliente_detalhe/${cliente.id}") }
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // Dialog de Confirmação de Exclusão
        if (clienteParaExcluir != null) {
            AlertDialog(
                onDismissRequest = { clienteParaExcluir = null },
                title = { Text(stringResource(R.string.delete_confirm_title)) },
                text = { Text(stringResource(R.string.delete_confirm_msg)) },
                confirmButton = {
                    Button(
                        onClick = {
                            val target = clienteParaExcluir!!
                            viewModel.delete(target)
                            clienteParaExcluir = null
                            scope.launch {
                                val result = snackbarHostState.showSnackbar("Cliente removido", "Desfazer", duration = SnackbarDuration.Short)
                                if (result == SnackbarResult.ActionPerformed) viewModel.undoDelete()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text(stringResource(R.string.btn_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { clienteParaExcluir = null }) { Text(stringResource(R.string.btn_cancel)) }
                }
            )
        }

        // Dialog de Cadastro com Validação
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (editingCliente == null) stringResource(R.string.dialog_new_client) else stringResource(R.string.dialog_edit_client)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = nome, onValueChange = { nome = it; nomeError = false }, 
                            label = { Text(stringResource(R.string.label_full_name)) },
                            isError = nomeError,
                            supportingText = { if (nomeError) Text(stringResource(R.string.error_field_required)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = telefone, onValueChange = { telefone = it; telefoneError = false }, 
                            label = { Text(stringResource(R.string.label_phone)) },
                            isError = telefoneError,
                            supportingText = { if (telefoneError) Text(stringResource(R.string.error_field_required)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.label_email)) }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = cidade, onValueChange = { cidade = it }, label = { Text(stringResource(R.string.label_city)) }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (nome.isBlank()) nomeError = true
                        if (telefone.isBlank()) telefoneError = true
                        if (!nomeError && !telefoneError) {
                            val c = Cliente(editingCliente?.id ?: 0, nome, telefone, email, cidade)
                            if (editingCliente == null) viewModel.insert(c) else viewModel.update(c)
                            showDialog = false
                        }
                    }) { Text(stringResource(R.string.btn_save)) }
                }
            )
        }
    }
}

@Composable
fun ClienteCard(cliente: Cliente, onEdit: () -> Unit, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = cliente.nome.take(1).uppercase(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cliente.nome, fontWeight = FontWeight.Bold)
                InfoRow(Icons.Default.LocalPhone, cliente.telefone)
                InfoRow(Icons.Default.Place, cliente.cidade)
            }
            Column {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, null, tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}
