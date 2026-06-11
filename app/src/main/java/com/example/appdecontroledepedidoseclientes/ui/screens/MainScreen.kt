package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val userName by settingsViewModel.userName.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Menu Principal - Olá $userName") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { navController.navigate("clientes") }, modifier = Modifier.fillMaxWidth()) {
                Text("Clientes")
            }
            Button(onClick = { navController.navigate("produtos") }, modifier = Modifier.fillMaxWidth()) {
                Text("Produtos")
            }
            Button(onClick = { navController.navigate("pedidos") }, modifier = Modifier.fillMaxWidth()) {
                Text("Pedidos")
            }
            Button(onClick = { navController.navigate("configuracoes") }, modifier = Modifier.fillMaxWidth()) {
                Text("Configurações")
            }
        }
    }
}

