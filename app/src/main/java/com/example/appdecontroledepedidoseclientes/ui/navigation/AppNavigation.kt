package com.example.appdecontroledepedidoseclientes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appdecontroledepedidoseclientes.ui.screens.*
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    clienteViewModel: ClienteViewModel,
    produtoViewModel: ProdutoViewModel,
    pedidoViewModel: PedidoViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }
        composable("main") {
            MainScreen(
                navController = navController,
                settingsViewModel = settingsViewModel
            )
        }
        composable("clientes") {
            ClienteScreen(
                navController = navController,
                viewModel = clienteViewModel
            )
        }
        composable("produtos") {
            ProdutoScreen(
                navController = navController,
                viewModel = produtoViewModel
            )
        }
        composable("pedidos") {
            PedidoScreen(
                navController = navController,
                viewModel = pedidoViewModel
            )
        }
        composable("configuracoes") {
            SettingsScreen(
                navController = navController,
                viewModel = settingsViewModel
            )
        }
    }
}
