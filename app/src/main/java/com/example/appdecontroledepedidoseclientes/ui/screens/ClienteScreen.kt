package com.example.appdecontroledepedidoseclientes.ui.screens

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

/**
 * --- O QUE É ESTA TELA? ---
 * Esta é a tela de listagem e cadastro de Clientes. 
 * Ela permite ver quem são os clientes, adicionar novos, editar ou excluir.
 */

// @Composable: É a peça fundamental do Jetpack Compose. 
// Avisa que esta função vai transformar dados em elementos visuais (texto, botões, imagens).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteScreen(
    navController: NavController, // navController: Ferramenta para mudar de tela.
    viewModel: ClienteViewModel   // viewModel: Onde fica a lógica e o acesso aos dados dos clientes.
) {
    // collectAsState(): "Escuta" a lista de clientes do banco de dados. 
    // Se um cliente for adicionado ou removido, a tela atualiza na hora.
    val clientes by viewModel.clientes.collectAsState()
    
    // remember { mutableStateOf(...) }: Cria variáveis que o Android "lembra" mesmo se a tela girar ou atualizar.
    var showDialog by remember { mutableStateOf(false) } // Controla se o formulário (pop-up) está aberto.
    var editingCliente by remember { mutableStateOf<Cliente?>(null) } // Guarda qual cliente estamos editando agora.
    
    // Campos que guardam o que o usuário digita no formulário.
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }

    // Scaffold: Define a estrutura padrão da tela: barra superior, conteúdo e botão flutuante.
    Scaffold(
        topBar = {
            // MediumTopAppBar: Barra de título média, comum em telas de lista.
            MediumTopAppBar(
                title = { Text(stringResource(R.string.title_clients), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // Botão de voltar (setinha).
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_desc_back))
                    }
                },
                actions = {
                    // Ícone de lupa (apenas visual por enquanto).
                    IconButton(onClick = { /* Implementar busca futuramente */ }) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.content_desc_search))
                    }
                }
            )
        },
        floatingActionButton = {
            // ExtendedFloatingActionButton: Aquele botão redondo "flutuante" no canto da tela.
            ExtendedFloatingActionButton(
                onClick = { 
                    editingCliente = null // Dizemos que é um "novo" cliente, não uma edição.
                    nome = ""; telefone = ""; email = ""; cidade = "" // Limpa os campos.
                    showDialog = true // Abre o pop-up de cadastro.
                },
                icon = { Icon(Icons.Filled.PersonAdd, stringResource(R.string.content_desc_add)) },
                text = { Text(stringResource(R.string.btn_new_client)) },
                containerColor = MaterialTheme.colorScheme.primary, // Cor principal do app.
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { padding -> // padding: Garante que a lista não fique embaixo da barra superior.
        
        // Estrutura condicional (IF): Se a lista estiver vazia, mostramos um aviso.
        if (clientes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PeopleOutline, 
                        null, 
                        modifier = Modifier.size(64.dp), 
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.empty_clients), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            // LazyColumn: É a versão moderna do ListView. 
            // Ela é "Preguiçosa" (Lazy) porque só carrega os itens que o usuário está vendo, economizando memória.
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espaço entre os cartões.
            ) {
                // items(): Percorre a lista de clientes e cria um componente visual para cada um.
                items(clientes) { cliente ->
                    ClienteCard(
                        cliente = cliente,
                        onEdit = {
                            // Quando clica em editar, preenchemos o formulário com os dados atuais do cliente.
                            editingCliente = cliente
                            nome = cliente.nome
                            telefone = cliente.telefone
                            email = cliente.email
                            cidade = cliente.cidade
                            showDialog = true
                        },
                        onDelete = { viewModel.delete(cliente) } // Chama a função de apagar no ViewModel.
                    )
                }
                // Spacer extra no final para o botão flutuante não tampar o último item da lista.
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // AlertDialog: É o pop-up (caixa de diálogo) para preencher os dados.
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false }, // Fecha o pop-up se clicar fora.
                title = { Text(if (editingCliente == null) stringResource(R.string.dialog_new_client) else stringResource(R.string.dialog_edit_client), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                        // OutlinedTextField: Caixas de entrada de texto.
                        OutlinedTextField(
                            value = nome, onValueChange = { nome = it }, 
                            label = { Text(stringResource(R.string.label_full_name)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.Person, null) }
                        )
                        OutlinedTextField(
                            value = telefone, onValueChange = { telefone = it }, 
                            label = { Text(stringResource(R.string.label_phone)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.Phone, null) }
                        )
                        OutlinedTextField(
                            value = email, onValueChange = { email = it }, 
                            label = { Text(stringResource(R.string.label_email)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.Email, null) }
                        )
                        OutlinedTextField(
                            value = cidade, onValueChange = { cidade = it }, 
                            label = { Text(stringResource(R.string.label_city)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.LocationCity, null) }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Cria um novo objeto Cliente com o que foi digitado.
                            val c = Cliente(
                                id = editingCliente?.id ?: 0, // Se for novo é 0 (o banco gera), se for edição mantém o original.
                                nome = nome, telefone = telefone, email = email, cidade = cidade
                            )
                            // Salva ou Atualiza no banco de dados.
                            if (editingCliente == null) viewModel.insert(c) else viewModel.update(c)
                            showDialog = false // Fecha o pop-up.
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(stringResource(R.string.btn_save)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
                }
            )
        }
    }
}

/**
 * --- O QUE É ISTO? ---
 * Componente que desenha o "Cartão" de cada cliente na lista.
 */
@Composable
fun ClienteCard(cliente: Cliente, onEdit: () -> Unit, onDelete: () -> Unit) {
    // Card: Cria o retângulo visual com bordas e cor.
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        // Row: Coloca os itens um ao lado do outro (Avatar, Nome e Botões).
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surface: Cria um quadrado arredondado para ser o "Avatar" do cliente.
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Pega a primeira letra do nome do cliente.
                    Text(
                        text = cliente.nome.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Column: Informações do cliente uma embaixo da outra.
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cliente.nome, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Chamamos a função InfoRow para cada dado (Telefone, E-mail, Cidade).
                InfoRow(Icons.Default.LocalPhone, cliente.telefone)
                InfoRow(Icons.Default.Mail, cliente.email)
                InfoRow(Icons.Default.Place, cliente.cidade)
            }
            
            // Pequena coluna com os botões de ação no canto direito.
            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, stringResource(R.string.content_desc_edit), tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, stringResource(R.string.content_desc_delete), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

/**
 * Função auxiliar para desenhar uma linha com ícone pequeno + texto.
 */
@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 1.dp)) {
        Icon(icon, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text, 
            style = MaterialTheme.typography.bodySmall, 
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1 // Garante que o texto não quebre a linha se for muito longo.
        )
    }
}
