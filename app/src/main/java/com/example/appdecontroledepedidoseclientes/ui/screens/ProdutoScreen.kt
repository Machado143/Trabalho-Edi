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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.R
import com.example.appdecontroledepedidoseclientes.data.entity.Produto
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.ProdutoViewModel

/**
 * --- EXPLICAÇÃO BÁSICA PARA APRESENTAÇÃO ---
 * 
 * @Composable: É como uma peça de LEGO. No Android moderno, as telas são montadas juntando 
 * essas peças. Sempre que você quiser "desenhar" algo na tela, usa o @Composable.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoScreen(
    navController: NavController, // O "controle remoto" que nos leva de uma tela para outra.
    viewModel: ProdutoViewModel    // O "gerente" que cuida dos dados dos produtos e fala com o banco de dados.
) {
    // collectAsState: Faz com que a tela fique "viva". Se alguém mudar um produto no banco,
    // a tela percebe e se redesenha sozinha com o novo dado.
    val produtos by viewModel.produtos.collectAsState()
    
    // remember { mutableStateOf(...) }: É a "memória" da tela. 
    // Se a tela piscar ou atualizar, ela não esquece o que estava acontecendo.
    var showDialog by remember { mutableStateOf(false) } // Controla se a janelinha (pop-up) está aberta.
    var editingProduto by remember { mutableStateOf<Produto?>(null) } // Guarda qual produto estamos mexendo.
    
    // Variáveis que guardam o que você digita nas caixinhas de texto.
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    // Scaffold: É a moldura da tela. Ele reserva o lugar da barra de cima e do botão de baixo.
    Scaffold(
        topBar = {
            // Barra superior com título e botão de voltar.
            MediumTopAppBar(
                title = { Text(stringResource(R.string.title_products), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_desc_back))
                    }
                }
            )
        },
        floatingActionButton = {
            // Botão redondo que fica "voando" na tela para adicionar novo produto.
            ExtendedFloatingActionButton(
                onClick = { 
                    editingProduto = null // Reseta para modo de criação
                    nome = ""; descricao = ""; valor = ""; quantidade = ""
                    showDialog = true 
                },
                icon = { Icon(Icons.Filled.AddShoppingCart, stringResource(R.string.content_desc_add)) },
                text = { Text(stringResource(R.string.btn_new_product)) },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { padding -> // 'padding' evita que o conteúdo fique escondido atrás da barra de cima.
        
        // Se a lista estiver vazia, mostra uma mensagem amigável.
        if (produtos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Inventory, 
                        null, 
                        modifier = Modifier.size(64.dp), 
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.empty_stock), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            // LazyColumn: Uma lista infinita que economiza bateria. 
            // Ela só gasta processamento para desenhar o que você está vendo agora.
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Para cada produto na lista, criamos um "Cartão" (ProdutoCard).
                items(produtos) { produto ->
                    ProdutoCard(
                        produto = produto,
                        onEdit = {
                            // Prepara os dados para editar
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
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // AlertDialog: Janelinha (Pop-up) que aparece para cadastrar ou editar um produto.
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (editingProduto == null) stringResource(R.string.dialog_new_product) else stringResource(R.string.dialog_edit_product), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                        // Caixinhas de texto (Inputs).
                        OutlinedTextField(
                            value = nome, onValueChange = { nome = it }, 
                            label = { Text(stringResource(R.string.label_product_name)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = descricao, onValueChange = { descricao = it }, 
                            label = { Text(stringResource(R.string.label_short_description)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = valor, onValueChange = { valor = it }, 
                                label = { Text(stringResource(R.string.label_price)) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                prefix = { Text(stringResource(R.string.currency_prefix)) }
                            )
                            OutlinedTextField(
                                value = quantidade, onValueChange = { quantidade = it }, 
                                label = { Text(stringResource(R.string.label_qty_short)) },
                                modifier = Modifier.weight(0.7f),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Cria o objeto produto e salva no banco de dados.
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
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(stringResource(R.string.btn_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
                }
            )
        }
    }
}

/**
 * --- COMPONENTE DO CARTÃO ---
 * Esta função desenha cada "bloco" de produto que aparece na lista.
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
            // Row: Organiza os itens deitados (lado a lado).
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = produto.nome,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = produto.descricao,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                
                // Exibe o preço formatado.
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.currency_format, produto.valor),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp) // Linha divisória.
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lógica de cor: Se o estoque estiver baixo, fica laranja/vermelho.
                val stockColor = if (produto.quantidade > 5) MaterialTheme.colorScheme.primary 
                                else if (produto.quantidade > 0) Color(0xFFE65100) 
                                else MaterialTheme.colorScheme.error

                Surface(
                    color = stockColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (produto.quantidade > 0) Icons.Default.CheckCircle else Icons.Default.Warning, 
                            null, 
                            modifier = Modifier.size(16.dp),
                            tint = stockColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.label_stock_qty, produto.quantidade),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = stockColor
                        )
                    }
                }
                
                // Botões para Editar e Excluir.
                Row {
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
}
