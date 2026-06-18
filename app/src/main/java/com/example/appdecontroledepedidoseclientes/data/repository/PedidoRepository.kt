package com.example.appdecontroledepedidoseclientes.data.repository

import com.example.appdecontroledepedidoseclientes.data.dao.PedidoDao
import com.example.appdecontroledepedidoseclientes.data.entity.Pedido

class PedidoRepository(private val dao: PedidoDao) {
    val pedidosComDetalhes = dao.getAllComDetalhes()
    suspend fun insert(pedido: Pedido) = dao.insert(pedido)
    suspend fun update(pedido: Pedido) = dao.update(pedido)
    suspend fun delete(pedido: Pedido) = dao.delete(pedido)
    suspend fun getById(id: Int) = dao.getById(id)
    
    fun getPedidosByCliente(clienteId: Int) = dao.getPedidosByCliente(clienteId)
    fun getCountToday(today: String) = dao.getCountToday(today)
    fun getRevenueMonth(monthYear: String) = dao.getRevenueMonth(monthYear)
}
