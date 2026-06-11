package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appdecontroledepedidoseclientes.data.dao.UsuarioDao
import com.example.appdecontroledepedidoseclientes.data.datastore.SettingsDataStore
import com.example.appdecontroledepedidoseclientes.data.repository.ClienteRepository
import com.example.appdecontroledepedidoseclientes.data.repository.PedidoRepository
import com.example.appdecontroledepedidoseclientes.data.repository.ProdutoRepository

class AppViewModelFactory(
    private val clienteRepository: ClienteRepository,
    private val produtoRepository: ProdutoRepository,
    private val pedidoRepository: PedidoRepository,
    private val usuarioDao: UsuarioDao,
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(usuarioDao) as T
            }
            modelClass.isAssignableFrom(ClienteViewModel::class.java) -> {
                ClienteViewModel(clienteRepository) as T
            }
            modelClass.isAssignableFrom(ProdutoViewModel::class.java) -> {
                ProdutoViewModel(produtoRepository) as T
            }
            modelClass.isAssignableFrom(PedidoViewModel::class.java) -> {
                PedidoViewModel(pedidoRepository, clienteRepository, produtoRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsDataStore) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
