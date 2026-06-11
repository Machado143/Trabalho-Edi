package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        topBar = { TopAppBar(title = { Text("Produtos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                editingProduto = null
                nome = ""; descricao = ""; valor = ""; quantidade = ""
                showDialog = true 
            }) { Icon(Icons.Filled.Add, "Adicionar") }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(produtos) { produto ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(produto.nome, style = MaterialTheme.typography.titleMedium)
                            Text("Qtd: ${produto.quantidade} - R$ ${produto.valor}")
                        }
                        IconButton(onClick = {
                            editingProduto = produto
                            nome = produto.nome
                            descricao = produto.descricao
                            valor = produto.valor.toString()
                            quantidade = produto.quantidade.toString()
                            showDialog = true
                        }) { Icon(Icons.Filled.Edit, "Editar") }
                        IconButton(onClick = { viewModel.delete(produto) }) {
                            Icon(Icons.Filled.Delete, "Excluir")
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (editingProduto == null) "Novo Produto" else "Editar Produto") },
                text = {
                    Column {
                        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
                        OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") })
                        OutlinedTextField(value = valor, onValueChange = { valor = it }, label = { Text("Valor") })
                        OutlinedTextField(value = quantidade, onValueChange = { quantidade = it }, label = { Text("Quantidade") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val p = Produto(
                            id = editingProduto?.id ?: 0,
                            nome = nome,
                            descricao = descricao,
                            valor = valor.toDoubleOrNull() ?: 0.0,
                            quantidade = quantidade.toIntOrNull() ?: 0
                        )
                        if (editingProduto == null) viewModel.insert(p) else viewModel.update(p)
                        showDialog = false
                    }) { Text("Salvar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

