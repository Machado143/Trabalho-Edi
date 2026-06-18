package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdecontroledepedidoseclientes.data.repository.ClienteRepository
import com.example.appdecontroledepedidoseclientes.data.repository.PedidoRepository
import com.example.appdecontroledepedidoseclientes.data.repository.ProdutoRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    clienteRepository: ClienteRepository,
    produtoRepository: ProdutoRepository,
    pedidoRepository: PedidoRepository
) : ViewModel() {

    val totalClientes = clienteRepository.getCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalProdutos = produtoRepository.getCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val pedidosHoje = pedidoRepository.getCountToday(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val monthYear = SimpleDateFormat("/MM/yyyy", Locale.getDefault()).format(Date())
    val faturamentoMes = pedidoRepository.getRevenueMonth("%$monthYear")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}
