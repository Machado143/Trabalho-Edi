package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.R
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.LoginState
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

/**
 * --- O QUE É ESTA TELA? ---
 * Esta é a tela de Login. No Jetpack Compose, as telas são funções.
 */

// @Composable: Esta anotação avisa ao compilador que esta função define uma interface visual.
@Composable
fun LoginScreen(
    navController: NavController, // navController: Objeto que controla a navegação (ir de uma tela para outra).
    viewModel: LoginViewModel      // viewModel: Objeto que contém a lógica de negócio (verificar se o login está correto).
) {
    // remember { mutableStateOf("") }: Variáveis de "estado". 
    // O Compose observa estas variáveis e redesenha a tela automaticamente quando o valor muda.
    var username by remember { mutableStateOf("") } // Guarda o texto digitado no campo de usuário.
    var password by remember { mutableStateOf("") } // Guarda o texto digitado no campo de senha.
    
    // collectAsState(): Transforma o fluxo de dados do ViewModel em um estado que o Compose entende.
    val loginState by viewModel.loginState.collectAsState()
    
    // SnackbarHostState: Controla as mensagens temporárias que aparecem no rodapé (avisos de erro).
    val snackbarHostState = remember { SnackbarHostState() }
    
    // rememberCoroutineScope(): Cria um escopo para rodar tarefas em segundo plano (como mostrar o aviso na tela).
    val scope = rememberCoroutineScope()

    // LaunchedEffect: Executa este código quando o 'loginState' mudar.
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                viewModel.resetState()
                // Navega para a tela principal ("main")
                navController.navigate("main") {
                    // Remove a tela de login do histórico para que o botão 'voltar' não retorne para cá.
                    popUpTo("login") { inclusive = true }
                }
            }
            is LoginState.Error -> {
                val message = (loginState as LoginState.Error).message
                scope.launch {
                    // Mostra a mensagem de erro que veio do servidor/banco.
                    snackbarHostState.showSnackbar(message)
                }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Scaffold: A estrutura básica da tela (permite colocar o SnackbarHost no lugar certo).
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        // Box: Um container que permite empilhar elementos um em cima do outro.
        Box(
            modifier = Modifier
                .fillMaxSize() // Ocupa a tela inteira.
                .padding(padding)
                .background(
                    // Cria um gradiente (degradê) vertical no fundo.
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            // Column: Organiza os elementos um embaixo do outro verticalmente.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally, // Centraliza na horizontal.
                verticalArrangement = Arrangement.Center           // Centraliza na vertical.
            ) {
                // Surface: Uma superfície elevada que desenha o ícone do sistema.
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(45.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                // Spacer: Cria um espaço vazio entre os componentes.
                Spacer(modifier = Modifier.height(32.dp))

                // Text: Exibe um texto formatado.
                Text(
                    text = stringResource(R.string.login_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.login_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Card: Um cartão elevado que contém o formulário de login.
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp) // Espaço fixo entre itens da coluna.
                    ) {
                        // OutlinedTextField: Campo de entrada de texto com borda.
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it }, // Atualiza a variável sempre que o usuário digita.
                            label = { Text(stringResource(R.string.label_username)) },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true // Apenas uma linha de texto.
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.label_password)) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary) },
                            visualTransformation = PasswordVisualTransformation(), // Mascara a senha com asteriscos.
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Button: Botão de ação principal.
                        Button(
                            onClick = { 
                                // Lógica: Se estiver vazio, avisa. Se não, tenta logar.
                                if (username.isBlank() || password.isBlank()) {
                                    scope.launch { snackbarHostState.showSnackbar("Preencha todos os campos") }
                                } else {
                                    viewModel.login(username, password) 
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            // Se estiver processando, mostra um círculo girando. Senão, mostra o texto.
                            if (loginState is LoginState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(stringResource(R.string.btn_access), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Botão para criar conta
                OutlinedButton(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Criar Nova Conta", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
