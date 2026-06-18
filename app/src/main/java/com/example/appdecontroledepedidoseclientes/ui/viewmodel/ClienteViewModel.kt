package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdecontroledepedidoseclientes.data.entity.Cliente
import com.example.appdecontroledepedidoseclientes.data.repository.ClienteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClienteViewModel(private val repository: ClienteRepository) : ViewModel() {

    // Lista de clientes vinda do banco de dados
    val clientes = repository.clientes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Armazena temporariamente o último cliente deletado para o "Desfazer"
    private var lastDeletedCliente: Cliente? = null

    fun insert(cliente: Cliente) = viewModelScope.launch {
        repository.insert(cliente)
    }

    fun update(cliente: Cliente) = viewModelScope.launch {
        repository.update(cliente)
    }

    fun delete(cliente: Cliente) = viewModelScope.launch {
        lastDeletedCliente = cliente
        repository.delete(cliente)
    }

    // Função para restaurar o cliente deletado (Botão Desfazer)
    fun undoDelete() = viewModelScope.launch {
        lastDeletedCliente?.let {
            repository.insert(it)
            lastDeletedCliente = null
        }
    }
}
