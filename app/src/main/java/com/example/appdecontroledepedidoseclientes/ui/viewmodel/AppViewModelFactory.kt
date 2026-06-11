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
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(usuarioDao) as T
        }
        if (modelClass.isAssignableFrom(ClienteViewModel::class.java)) {
            return ClienteViewModel(clienteRepository) as T
        }
        if (modelClass.isAssignableFrom(ProdutoViewModel::class.java)) {
            return ProdutoViewModel(produtoRepository) as T
        }
        if (modelClass.isAssignableFrom(PedidoViewModel::class.java)) {
            return PedidoViewModel(pedidoRepository, clienteRepository, produtoRepository) as T
        }
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

