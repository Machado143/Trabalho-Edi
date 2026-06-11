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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.data.entity.Cliente
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.ClienteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteScreen(navController: NavController, viewModel: ClienteViewModel) {
    val clientes by viewModel.clientes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingCliente by remember { mutableStateOf<Cliente?>(null) }
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Clientes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                editingCliente = null
                nome = ""
                telefone = ""
                email = ""
                cidade = ""
                showDialog = true 
            }) {
                Icon(Icons.Filled.Add, "Adicionar")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(clientes) { cliente ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cliente.nome, style = MaterialTheme.typography.titleMedium)
                            Text(cliente.telefone)
                            Text(cliente.cidade)
                        }
                        IconButton(onClick = {
                            editingCliente = cliente
                            nome = cliente.nome
                            telefone = cliente.telefone
                            email = cliente.email
                            cidade = cliente.cidade
                            showDialog = true
                        }) {
                            Icon(Icons.Filled.Edit, "Editar")
                        }
                        IconButton(onClick = { viewModel.delete(cliente) }) {
                            Icon(Icons.Filled.Delete, "Excluir")
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (editingCliente == null) "Novo Cliente" else "Editar Cliente") },
                text = {
                    Column {
                        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
                        OutlinedTextField(value = telefone, onValueChange = { telefone = it }, label = { Text("Telefone") })
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") })
                        OutlinedTextField(value = cidade, onValueChange = { cidade = it }, label = { Text("Cidade") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val c = Cliente(
                            id = editingCliente?.id ?: 0,
                            nome = nome, telefone = telefone, email = email, cidade = cidade
                        )
                        if (editingCliente == null) viewModel.insert(c) else viewModel.update(c)
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

