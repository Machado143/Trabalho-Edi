package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.R
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.SettingsViewModel

/**
 * --- O QUE É ESTA TELA? ---
 * Esta é a tela de Configurações. No Android moderno (Jetpack Compose),
 * tudo o que você vê na tela é construído usando funções.
 */

// @Composable: Diz ao Android que esta função vai "desenhar" algo na tela.
// @OptIn: Usado para dizer que estamos cientes que estamos usando ferramentas novas/experimentais do Android.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController, // navController: É o "motorista" que nos leva de uma tela para outra.
    viewModel: SettingsViewModel   // viewModel: É o "cérebro" da tela. Ele guarda os dados e a lógica.
) {
    // collectAsState: Faz a tela "escutar" os dados. Se o dado mudar no banco, a tela atualiza sozinha.
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()

    // remember { mutableStateOf(...) }: Cria uma variável que a tela "lembra" o valor mesmo se ela for redesenhada.
    // É como um post-it que o código guarda para não esquecer o que o usuário digitou.
    var editingName by remember { mutableStateOf(userName) }
    
    // LaunchedEffect: Executa um bloco de código quando algo específico muda (neste caso, o nome do usuário).
    LaunchedEffect(userName) {
        editingName = userName
    }

    // Scaffold: É o "esqueleto" ou a estrutura básica da tela (topo, corpo e fundo).
    Scaffold(
        // topBar: É a barra que fica lá em cima, com o título e o botão de voltar.
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.title_settings), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_desc_back))
                    }
                }
            )
        }
    ) { padding -> // 'padding' é o espaço interno automático para o conteúdo não bater na barra superior.
        // Column: Coloca um item embaixo do outro (como uma pilha de pratos).
        Column(
            modifier = Modifier
                .fillMaxSize() // Ocupa toda a tela.
                .padding(padding) // Aplica o espaço da barra superior.
                .padding(horizontal = 16.dp), // Dá uma folga nas laterais.
            verticalArrangement = Arrangement.spacedBy(24.dp) // Dá um espaço entre os itens da coluna.
        ) {
            // Card: Cria um retângulo com bordas arredondadas e sombra, parecendo um cartão físico.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Row: Coloca os itens um ao lado do outro (na mesma linha).
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape, // Deixa o componente redondo.
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp)) // Spacer: É um "espaço vazio" para afastar os itens.
                        Text(
                            text = stringResource(R.string.section_profile),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // OutlinedTextField: É a caixinha de entrada de texto com borda.
                    OutlinedTextField(
                        value = editingName, // O que está escrito nela.
                        onValueChange = { editingName = it }, // O que acontece quando o usuário digita (o 'it' é a nova letra).
                        label = { Text(stringResource(R.string.label_display_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = { // trailingIcon: Um ícone que fica no final da caixa de texto.
                            if (editingName != userName) {
                                IconButton(onClick = { viewModel.saveUserName(editingName) }) {
                                    Icon(Icons.Default.Check, stringResource(R.string.btn_save), tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    )
                }
            }

            Column {
                Text(
                    text = stringResource(R.string.section_appearance),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        // Chamando uma função que criamos lá embaixo para economizar código.
                        SettingsToggleRow(
                            title = stringResource(R.string.setting_dark_mode),
                            subtitle = stringResource(R.string.setting_dark_mode_sub),
                            icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.saveDarkMode(it) }
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), thickness = 0.5.dp)
                        
                        SettingsToggleRow(
                            title = stringResource(R.string.setting_notifications),
                            subtitle = stringResource(R.string.setting_notifications_sub),
                            icon = Icons.Default.NotificationsActive,
                            checked = notifications,
                            onCheckedChange = { viewModel.saveNotifications(it) }
                        )
                    }
                }
            }

            // weight(1f): Faz este item "empurrar" o que vem depois para o final da tela.
            Spacer(modifier = Modifier.weight(1f))

            // Button: O botão clássico de clique.
            Button(
                onClick = { 
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(R.string.btn_logout), fontWeight = FontWeight.SemiBold)
            }
            
            Text(
                text = stringResource(R.string.app_version),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * --- O QUE É ISTO? ---
 * Esta é uma "função auxiliar". Em vez de escrever o código do Switch 
 * várias vezes, criamos este componente para repetir só o necessário.
 */
@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean, // Se o botão está ligado (true) ou desligado (false).
    onCheckedChange: (Boolean) -> Unit // O que o código deve fazer quando o usuário clicar no botão.
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // Switch: É o interruptor visual (o botão de deslizar).
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
