package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.R
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.ClienteViewModel
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.PedidoViewModel

/**
 * --- TELA DE DETALHES DO CLIENTE (Requisito 9) ---
 * Mostra os dados do cliente e todos os pedidos que ele já realizou.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDetailScreen(
    navController: NavController,
    clienteId: Int,
    clienteViewModel: ClienteViewModel,
    pedidoViewModel: PedidoViewModel
) {
    // Busca a lista de clientes e o cliente específico pelo ID
    val clientes by clienteViewModel.clientes.collectAsState()
    val cliente = clientes.find { it.id == clienteId }
    
    // Busca todos os pedidos e filtra apenas os que pertencem a este cliente
    val todosPedidos by pedidoViewModel.pedidosComDetalhes.collectAsState()
    val pedidosDoCliente = todosPedidos.filter { it.pedido.clienteId == clienteId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cliente?.nome ?: "Detalhes do Cliente") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.content_desc_back))
                    }
                }
            )
        }
    ) { padding ->
        if (cliente == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cliente não encontrado")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Seção 1: Informações de Contato
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text(
                                "Dados do Cliente", 
                                fontWeight = FontWeight.Bold, 
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(12.dp))
                            // Usando a função auxiliar definida no final do arquivo
                            InfoRow(Icons.Default.Phone, cliente.telefone)
                            InfoRow(Icons.Default.Email, cliente.email)
                            InfoRow(Icons.Default.Place, cliente.cidade)
                        }
                    }
                }

                // Seção 2: Histórico de Vendas
                item {
                    Text(
                        "Histórico de Pedidos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (pedidosDoCliente.isEmpty()) {
                    item {
                        Text(
                            "Este cliente ainda não possui pedidos registrados.", 
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(pedidosDoCliente) { item ->
                        // Reutiliza o card de pedido que definimos na PedidoScreen
                        PedidoCard(
                            pedidoComDetalhes = item,
                            onDelete = { pedidoViewModel.delete(item.pedido) },
                            onStatusChange = { novoStatus -> 
                                pedidoViewModel.updateStatus(item.pedido, novoStatus) 
                            }
                        )
                    }
                }
            }
        }
    }
}

