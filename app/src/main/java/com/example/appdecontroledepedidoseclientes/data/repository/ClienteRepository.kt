package com.example.appdecontroledepedidoseclientes.data.repository

import com.example.appdecontroledepedidoseclientes.data.dao.ClienteDao
import com.example.appdecontroledepedidoseclientes.data.entity.Cliente

class ClienteRepository(private val dao: ClienteDao) {
    val clientes = dao.getAll()
    suspend fun insert(cliente: Cliente) = dao.insert(cliente)
    suspend fun update(cliente: Cliente) = dao.update(cliente)
    suspend fun delete(cliente: Cliente) = dao.delete(cliente)
    suspend fun getById(id: Int) = dao.getById(id)
    fun getCount() = dao.getCount()
}
