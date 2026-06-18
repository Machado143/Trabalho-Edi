package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdecontroledepedidoseclientes.data.entity.Produto
import com.example.appdecontroledepedidoseclientes.data.repository.ProdutoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProdutoViewModel(private val repository: ProdutoRepository) : ViewModel() {

    val produtos = repository.produtos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var lastDeletedProduto: Produto? = null

    fun insert(produto: Produto) = viewModelScope.launch {
        repository.insert(produto)
    }

    fun update(produto: Produto) = viewModelScope.launch {
        repository.update(produto)
    }

    fun delete(produto: Produto) = viewModelScope.launch {
        lastDeletedProduto = produto
        repository.delete(produto)
    }

    fun undoDelete() = viewModelScope.launch {
        lastDeletedProduto?.let {
            repository.insert(it)
            lastDeletedProduto = null
        }
    }
}
