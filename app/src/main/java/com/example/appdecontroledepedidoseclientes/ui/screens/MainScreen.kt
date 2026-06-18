package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.R
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.MainViewModel
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.SettingsViewModel

/**
 * --- DASHBOARD PRINCIPAL ATUALIZADO ---
 * Agora exibe métricas reais do banco de dados em vez de apenas botões.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController, 
    settingsViewModel: SettingsViewModel,
    mainViewModel: MainViewModel // O novo ViewModel que calcula os totais
) {
    val userName by settingsViewModel.userName.collectAsState()
    
    // Coleta as métricas reais (Funcionalidade 10)
    val totalClientes by mainViewModel.totalClientes.collectAsState()
    val totalProdutos by mainViewModel.totalProdutos.collectAsState()
    val pedidosHoje by mainViewModel.pedidosHoje.collectAsState()
    val faturamento by mainViewModel.faturamentoMes.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.dashboard_title), fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.welcome_back, userName), style = MaterialTheme.typography.bodyMedium)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("configuracoes") }) {
                        Icon(Icons.Default.AccountCircle, stringResource(R.string.content_desc_profile), modifier = Modifier.size(32.dp))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            
            Text(
                text = stringResource(R.string.section_management),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Grid de Métricas e Atalhos (Funcionalidade 10)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    MetricCard(
                        title = stringResource(R.string.menu_clients),
                        value = totalClientes.toString(),
                        subtitle = stringResource(R.string.metric_total_clients),
                        icon = Icons.Default.People,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = { navController.navigate("clientes") }
                    )
                }
                item {
                    MetricCard(
                        title = stringResource(R.string.menu_products),
                        value = totalProdutos.toString(),
                        subtitle = stringResource(R.string.metric_total_products),
                        icon = Icons.Default.Inventory2,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { navController.navigate("produtos") }
                    )
                }
                item {
                    MetricCard(
                        title = stringResource(R.string.menu_orders),
                        value = pedidosHoje.toString(),
                        subtitle = stringResource(R.string.metric_orders_today),
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { navController.navigate("pedidos") }
                    )
                }
                item {
                    MetricCard(
                        title = stringResource(R.string.menu_settings),
                        value = stringResource(R.string.currency_format, faturamento ?: 0.0),
                        subtitle = stringResource(R.string.metric_total_revenue),
                        icon = Icons.Default.TrendingUp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { navController.navigate("pedidos") }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, modifier = Modifier.size(28.dp))
            Column {
                Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
