@file:Suppress("DEPRECATION")
package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.R
import com.example.appdecontroledepedidoseclientes.data.entity.Produto
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.ProdutoViewModel
import kotlinx.coroutines.launch

/**
 * --- O QUE É @Composable? ---
 * É a anotação básica do Jetpack Compose. Ela diz que esta função
 * não apenas executa código, mas "desenha" algo na tela do celular.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoScreen(
    navController: NavController, // O "GPS": Controla a navegação entre telas.
    viewModel: ProdutoViewModel    // O "Gerente": Cuida dos dados dos produtos.
) {
    // collectAsState: Faz a tela "vigiar" a lista de produtos. Se mudar no banco, a tela atualiza.
    val produtos by viewModel.produtos.collectAsState()
    
    // Estados para Busca e Ordenação (Requisitos 3 e 12)
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isAscending by remember { mutableStateOf(true) }

    // Filtragem e Ordenação em tempo real
    val filteredProdutos = remember(produtos, searchQuery, isAscending) {
        val filtered = produtos.filter { 
            it.nome.contains(searchQuery, ignoreCase = true) || 
            it.descricao.contains(searchQuery, ignoreCase = true) 
        }
        if (isAscending) filtered.sortedBy { it.nome } else filtered.sortedByDescending { it.nome }
    }

    // Variáveis de memória (remember) para o formulário
    var showDialog by remember { mutableStateOf(false) }
    var editingProduto by remember { mutableStateOf<Produto?>(null) }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    
    // Estados de Erro para Validação (Requisito 4)
    var nomeError by remember { mutableStateOf(false) }
    var valorError by remember { mutableStateOf(false) }
    var qtdError by remember { mutableStateOf(false) }

    // Estado para Confirmação de Exclusão (Requisito 1)
    var produtoParaExcluir by remember { mutableStateOf<Produto?>(null) }

    // Snackbar e Scope: Para mostrar a mensagem de "Desfazer" (Requisito 5)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Scaffold: A estrutura de esqueleto da tela (TopBar + Conteúdo + Botão Flutuante)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (isSearchActive) {
                // Barra de Busca Ativa
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
                    title = { Text(stringResource(R.string.title_products), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_desc_back))
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.content_desc_search))
                        }
                        // Botão de Ordenação (Requisito 12)
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
                    editingProduto = null
                    nome = ""; descricao = ""; valor = ""; quantidade = ""
                    nomeError = false; valorError = false; qtdError = false
                    showDialog = true 
                },
                icon = { Icon(Icons.Filled.AddShoppingCart, stringResource(R.string.content_desc_add)) },
                text = { Text(stringResource(R.string.btn_new_product)) },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { padding ->
        if (filteredProdutos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(if (searchQuery.isEmpty()) stringResource(R.string.empty_stock) else "Nenhum produto encontrado")
            }
        } else {
            // LazyColumn: Lista que economiza bateria, desenha só o que aparece na tela.
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredProdutos, key = { it.id }) { produto ->
                    // SwipeToDismissBox: Permite excluir deslizando o card (Requisito 11)
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                produtoParaExcluir = produto
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
                        // AnimatedVisibility: Faz o card entrar com animação suave (Requisito 6)
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500)) { it / 2 }
                        ) {
                            ProdutoCard(
                                produto = produto,
                                onEdit = {
                                    editingProduto = produto
                                    nome = produto.nome; descricao = produto.descricao
                                    valor = produto.valor.toString(); quantidade = produto.quantidade.toString()
                                    nomeError = false; valorError = false; qtdError = false
                                    showDialog = true
                                },
                                onDelete = { produtoParaExcluir = produto }
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // Alerta de Confirmação antes de Excluir
        if (produtoParaExcluir != null) {
            AlertDialog(
                onDismissRequest = { produtoParaExcluir = null },
                title = { Text(stringResource(R.string.delete_confirm_title)) },
                text = { Text("Deseja realmente excluir este produto?") },
                confirmButton = {
                    Button(
                        onClick = {
                            val target = produtoParaExcluir!!
                            viewModel.delete(target)
                            produtoParaExcluir = null
                            // Snackbar com ação de Desfazer
                            scope.launch {
                                val result = snackbarHostState.showSnackbar("Produto removido", "Desfazer", duration = SnackbarDuration.Short)
                                if (result == SnackbarResult.ActionPerformed) viewModel.undoDelete()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text(stringResource(R.string.btn_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { produtoParaExcluir = null }) { Text(stringResource(R.string.btn_cancel)) }
                }
            )
        }

        // Janela de Cadastro com Mensagens de Erro (Validação)
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (editingProduto == null) stringResource(R.string.dialog_new_product) else stringResource(R.string.dialog_edit_product), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = nome, onValueChange = { nome = it; nomeError = false }, 
                            label = { Text(stringResource(R.string.label_product_name)) },
                            isError = nomeError, // Fica vermelho se houver erro
                            supportingText = { if (nomeError) Text(stringResource(R.string.error_field_required)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descricao, onValueChange = { descricao = it }, 
                            label = { Text(stringResource(R.string.label_short_description)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = valor, onValueChange = { valor = it; valorError = false }, 
                                label = { Text(stringResource(R.string.label_price)) },
                                isError = valorError,
                                modifier = Modifier.weight(1f),
                                prefix = { Text(stringResource(R.string.currency_prefix)) }
                            )
                            OutlinedTextField(
                                value = quantidade, onValueChange = { quantidade = it; qtdError = false }, 
                                label = { Text(stringResource(R.string.label_qty_short)) },
                                isError = qtdError,
                                modifier = Modifier.weight(0.7f)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val v = valor.toDoubleOrNull() ?: -1.0
                        val q = quantidade.toIntOrNull() ?: -1
                        if (nome.isBlank()) nomeError = true
                        if (v < 0) valorError = true
                        if (q < 0) qtdError = true
                        
                        if (!nomeError && !valorError && !qtdError) {
                            val p = Produto(editingProduto?.id ?: 0, nome, descricao, v, q)
                            if (editingProduto == null) viewModel.insert(p) else viewModel.update(p)
                            showDialog = false
                        }
                    }) { Text(stringResource(R.string.btn_confirm)) }
                }
            )
        }
    }
}

/**
 * --- COMPONENTE DO CARTÃO ---
 * Esta função desenha cada bloco de produto que aparece na lista.
 */
@Composable
fun ProdutoCard(produto: Produto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = produto.nome, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = produto.descricao, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                }
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(12.dp)) {
                    Text(text = stringResource(R.string.currency_format, produto.valor), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                val stockColor = if (produto.quantidade > 5) MaterialTheme.colorScheme.primary 
                                else if (produto.quantidade > 0) Color(0xFFE65100) 
                                else MaterialTheme.colorScheme.error

                Surface(color = stockColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (produto.quantidade > 0) Icons.Default.CheckCircle else Icons.Default.Warning, null, modifier = Modifier.size(16.dp), tint = stockColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(R.string.label_stock_qty, produto.quantidade), fontWeight = FontWeight.Bold, color = stockColor)
                    }
                }
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, null, tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}
