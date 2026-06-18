package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.PersonAdd
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
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.RegisterState
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

/**
 * --- TELA DE CADASTRO DE USUÁRIO ---
 * Permite que novos usuários se registrem no sistema.
 */
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel
) {
    var username by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmSenha by remember { mutableStateOf("") }
    
    val registerState by viewModel.registerState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // LaunchedEffect para reagir a mudanças no estado
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                viewModel.resetState()
                // Navega de volta para login com sucesso
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is RegisterState.Error -> {
                val message = (registerState as RegisterState.Error).message
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // TopBar customizada
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Criar Conta",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Logo/Ícone
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(45.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                // Título
                Text(
                    text = "Novo Usuário",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Crie sua conta para acessar o sistema",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Formulário
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Campo Username
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Nome de Usuário") },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )

                        // Campo Senha
                        OutlinedTextField(
                            value = senha,
                            onValueChange = { senha = it },
                            label = { Text("Senha") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )

                        // Campo Confirmar Senha
                        OutlinedTextField(
                            value = confirmSenha,
                            onValueChange = { confirmSenha = it },
                            label = { Text("Confirmar Senha") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botão Registrar
                        Button(
                            onClick = {
                                viewModel.register(username, senha, confirmSenha)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            enabled = registerState !is RegisterState.Loading
                        ) {
                            if (registerState is RegisterState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Criar Conta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Link para login
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Já tem conta? ", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Faça login", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

