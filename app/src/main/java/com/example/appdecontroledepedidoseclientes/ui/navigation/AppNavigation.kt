package com.example.appdecontroledepedidoseclientes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.appdecontroledepedidoseclientes.ui.screens.*
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    clienteViewModel: ClienteViewModel,
    produtoViewModel: ProdutoViewModel,
    pedidoViewModel: PedidoViewModel,
    settingsViewModel: SettingsViewModel,
    mainViewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController, loginViewModel) }
        composable("register") { RegisterScreen(navController, registerViewModel) }
        composable("main") { MainScreen(navController, settingsViewModel, mainViewModel) }
        composable("clientes") { ClienteScreen(navController, clienteViewModel) }
        composable("produtos") { ProdutoScreen(navController, produtoViewModel) }
        composable("pedidos") { PedidoScreen(navController, pedidoViewModel) }
        composable("configuracoes") { SettingsScreen(navController, settingsViewModel) }
        
        // Tela de Detalhes do Cliente (Requisito 9)
        composable(
            route = "cliente_detalhe/{clienteId}",
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            ClienteDetailScreen(navController, clienteId, clienteViewModel, pedidoViewModel)
        }
    }
}
