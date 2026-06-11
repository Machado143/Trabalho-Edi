package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()

    var editingName by remember { mutableStateOf(userName) }
    
    // Update local state when flow updates
    LaunchedEffect(userName) {
        editingName = userName
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Configurações") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tema Escuro", modifier = Modifier.weight(1f))
                Switch(checked = isDarkMode, onCheckedChange = { viewModel.saveDarkMode(it) })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Notificações", modifier = Modifier.weight(1f))
                Switch(checked = notifications, onCheckedChange = { viewModel.saveNotifications(it) })
            }
            OutlinedTextField(
                value = editingName,
                onValueChange = { editingName = it },
                label = { Text("Nome do Usuário") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { viewModel.saveUserName(editingName) }, modifier = Modifier.fillMaxWidth()) {
                Text("Salvar Nome")
            }
        }
    }
}

