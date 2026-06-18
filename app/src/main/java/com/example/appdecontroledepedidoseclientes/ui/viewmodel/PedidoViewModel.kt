package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdecontroledepedidoseclientes.data.entity.Pedido
import com.example.appdecontroledepedidoseclientes.data.repository.PedidoRepository
import com.example.appdecontroledepedidoseclientes.data.repository.ClienteRepository
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

    private var lastDeletedPedido: Pedido? = null

    // Função de Inserção com atualização de estoque (Funcionalidade 8)
    fun insert(pedido: Pedido) = viewModelScope.launch {
        repository.insert(pedido)
        
        // Subtrai do estoque
        if (pedido.produtoId != null && pedido.quantidade != null) {
            val produto = produtoRepository.getById(pedido.produtoId)
            produto?.let {
                val novoEstoque = it.quantidade - pedido.quantidade
                produtoRepository.update(it.copy(quantidade = if (novoEstoque < 0) 0 else novoEstoque))
            }
        }
    }

    fun delete(pedido: Pedido) = viewModelScope.launch {
        lastDeletedPedido = pedido
        repository.delete(pedido)
        
        // Devolve ao estoque se cancelado/deletado
        if (pedido.produtoId != null && pedido.quantidade != null) {
            val produto = produtoRepository.getById(pedido.produtoId)
            produto?.let {
                produtoRepository.update(it.copy(quantidade = it.quantidade + pedido.quantidade))
            }
        }
    }

    fun undoDelete() = viewModelScope.launch {
        lastDeletedPedido?.let {
            insert(it)
            lastDeletedPedido = null
        }
    }

    fun updateStatus(pedido: Pedido, novoStatus: String) = viewModelScope.launch {
        repository.update(pedido.copy(status = novoStatus))
    }
}
