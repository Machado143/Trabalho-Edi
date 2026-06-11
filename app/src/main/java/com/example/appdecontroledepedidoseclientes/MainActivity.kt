package com.example.appdecontroledepedidoseclientes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.appdecontroledepedidoseclientes.data.database.AppDatabase
import com.example.appdecontroledepedidoseclientes.data.datastore.SettingsDataStore
import com.example.appdecontroledepedidoseclientes.data.repository.ClienteRepository
import com.example.appdecontroledepedidoseclientes.data.repository.PedidoRepository
import com.example.appdecontroledepedidoseclientes.data.repository.ProdutoRepository
import com.example.appdecontroledepedidoseclientes.ui.navigation.AppNavigation
import com.example.appdecontroledepedidoseclientes.ui.theme.AppDeControleDePedidosEClientesTheme
import com.example.appdecontroledepedidoseclientes.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = AppDatabase.getDatabase(this)
        val clienteRepo = ClienteRepository(db.clienteDao())
        val produtoRepo = ProdutoRepository(db.produtoDao())
        val pedidoRepo = PedidoRepository(db.pedidoDao())
        val settingsDataStore = SettingsDataStore(this)
        
        val factory = AppViewModelFactory(clienteRepo, produtoRepo, pedidoRepo, db.usuarioDao(), settingsDataStore)
        
        val loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        val clienteViewModel = ViewModelProvider(this, factory)[ClienteViewModel::class.java]
        val produtoViewModel = ViewModelProvider(this, factory)[ProdutoViewModel::class.java]
        val pedidoViewModel = ViewModelProvider(this, factory)[PedidoViewModel::class.java]
        val settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        
        enableEdgeToEdge()
        setContent {
            val isDarkPref by settingsViewModel.isDarkMode.collectAsState()
            
            AppDeControleDePedidosEClientesTheme(darkTheme = isDarkPref) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        clienteViewModel = clienteViewModel,
                        produtoViewModel = produtoViewModel,
                        pedidoViewModel = pedidoViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
