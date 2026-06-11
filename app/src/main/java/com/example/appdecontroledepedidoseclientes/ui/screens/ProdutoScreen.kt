package com.example.appdecontroledepedidoseclientes.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.data.entity.Produto
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.ProdutoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoScreen(navController: NavController, viewModel: ProdutoViewModel) {
    val produtos by viewModel.produtos.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingProduto by remember { mutableStateOf<Produto?>(null) }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estoque de Produtos", fontWeight = FontWeight.SemiBold) },
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
                    editingProduto = null
                    nome = ""; descricao = ""; valor = ""; quantidade = ""
                    showDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Filled.Add, "Adicionar Produto") }
        }
    ) { padding ->
        if (produtos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhum produto em estoque", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(produtos) { produto ->
                    ProdutoCard(
                        produto = produto,
                        onEdit = {
                            editingProduto = produto
                            nome = produto.nome
                            descricao = produto.descricao
                            valor = produto.valor.toString()
                            quantidade = produto.quantidade.toString()
                            showDialog = true
                        },
                        onDelete = { viewModel.delete(produto) }
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (editingProduto == null) "Novo Produto" else "Editar Produto") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nome, onValueChange = { nome = it }, 
                            label = { Text("Nome do Produto") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = descricao, onValueChange = { descricao = it }, 
                            label = { Text("Descrição") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = valor, onValueChange = { valor = it }, 
                                label = { Text("Preço (R$)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = quantidade, onValueChange = { quantidade = it }, 
                                label = { Text("Qtd") },
                                modifier = Modifier.weight(0.6f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val p = Produto(
                                id = editingProduto?.id ?: 0,
                                nome = nome,
                                descricao = descricao,
                                valor = valor.toDoubleOrNull() ?: 0.0,
                                quantidade = quantidade.toIntOrNull() ?: 0
                            )
                            if (editingProduto == null) viewModel.insert(p) else viewModel.update(p)
                            showDialog = false
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Salvar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun ProdutoCard(produto: Produto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = produto.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = produto.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "R$ ${String.format("%.2f", produto.valor)}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info, 
                        null, 
                        modifier = Modifier.size(16.dp),
                        tint = if (produto.quantidade > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Estoque: ${produto.quantidade}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (produto.quantidade > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                    )
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Excluir", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
