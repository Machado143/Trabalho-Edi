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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.SettingsViewModel

/**
 * --- O QUE É ESTA TELA? ---
 * Esta e a Tela Principal (Dashboard). Ela serve como o menu principal do aplicativo.
 * É o "hub" de onde o usuário acessa todas as outras partes do sistema.
 */

// @OptIn: Avisa que estamos usando componentes modernos do Material Design 3.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController, // navController: Ferramenta que gerencia as trocas de tela.
    settingsViewModel: SettingsViewModel // settingsViewModel: Fornece dados globais (como o nome do usuário).
) {
    // collectAsState: Transforma os dados do banco/preferências em algo que a tela pode "vigiar".
    // Se o nome do usuário mudar em outro lugar, esta tela atualiza sozinha aqui.
    val userName by settingsViewModel.userName.collectAsState()

    // Scaffold: A estrutura base da tela. No Android, ele facilita colocar a barra superior (TopBar).
    Scaffold(
        topBar = {
            // LargeTopAppBar: Aquela barra superior que fica maior e diminui quando você rola a tela.
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.dashboard_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        // %1$s: No arquivo strings.xml, isso é um código que o Android substitui pelo 'userName'.
                        Text(
                            stringResource(R.string.welcome_back, userName),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Botão de ícone no canto direito da barra superior.
                    IconButton(onClick = { navController.navigate("configuracoes") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle, 
                            contentDescription = stringResource(R.string.content_desc_profile), 
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding -> // padding: Espaço de segurança para o conteúdo não ficar escondido atrás da barra superior.
        Column(
            modifier = Modifier
                .fillMaxSize() // Ocupa toda a tela disponível.
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // Dá um respiro no topo.
            
            Text(
                text = stringResource(R.string.section_management),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // LazyVerticalGrid: Cria uma grade (como uma tabela) que só desenha o que está visível.
            // GridCells.Fixed(2): Significa que sempre teremos 2 colunas uma do lado da outra.
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Espaço entre as colunas.
                verticalArrangement = Arrangement.spacedBy(16.dp),   // Espaço entre as linhas.
                modifier = Modifier.fillMaxSize()
            ) {
                // Cada 'item' é um botão quadrado no nosso menu.
                item {
                    MenuCard(
                        stringResource(R.string.menu_clients), 
                        Icons.Default.People, 
                        stringResource(R.string.menu_clients_sub),
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        // Navega para a rota "clientes" definida no AppNavigation.
                        navController.navigate("clientes")
                    }
                }
                item {
                    MenuCard(
                        stringResource(R.string.menu_products), 
                        Icons.Default.Inventory2, 
                        stringResource(R.string.menu_products_sub),
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        navController.navigate("produtos")
                    }
                }
                item {
                    MenuCard(
                        stringResource(R.string.menu_orders), 
                        Icons.AutoMirrored.Filled.ReceiptLong, 
                        stringResource(R.string.menu_orders_sub),
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        navController.navigate("pedidos")
                    }
                }
                item {
                    MenuCard(
                        stringResource(R.string.menu_settings), 
                        Icons.Default.Settings, 
                        stringResource(R.string.menu_settings_sub),
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        navController.navigate("configuracoes")
                    }
                }
            }
        }
    }
}

/**
 * --- COMPONENTE CUSTOMIZADO ---
 * Criamos esta função para não ter que repetir o desenho do cartão 4 vezes.
 * Ela recebe os textos, o ícone e a ação de clique.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(
    title: String, 
    icon: ImageVector, 
    subtitle: String, 
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit // Função que será executada quando o usuário clicar no cartão.
) {
    // Card: Cria o retângulo visual. O parâmetro 'onClick' torna o cartão todo clicável.
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp), // Deixa os cantos bem arredondados.
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween // Joga o ícone pro topo e o texto pro final.
        ) {
            // Surface: Cria um fundinho colorido atrás do ícone.
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = contentColor.copy(alpha = 0.15f) // Cor com transparência.
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = contentColor
                    )
                }
            }
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}
