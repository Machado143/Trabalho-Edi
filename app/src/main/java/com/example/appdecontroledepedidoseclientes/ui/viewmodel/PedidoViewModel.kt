package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdecontroledepedidoseclientes.data.entity.Pedido
import com.example.appdecontroledepedidoseclientes.data.repository.ClienteRepository
import com.example.appdecontroledepedidoseclientes.data.repository.PedidoRepository
import com.example.appdecontroledepedidoseclientes.data.repository.ProdutoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PedidoViewModel(
    private val repository: PedidoRepository,
    private val clienteRepository: ClienteRepository,
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    val pedidosComDetalhes = repository.pedidosComDetalhes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val clientes = clienteRepository.clientes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val produtos = produtoRepository.produtos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(pedido: Pedido) = viewModelScope.launch {
        repository.insert(pedido)
    }

    fun update(pedido: Pedido) = viewModelScope.launch {
        repository.update(pedido)
    }

    fun delete(pedido: Pedido) = viewModelScope.launch {
        repository.delete(pedido)
    }
}

