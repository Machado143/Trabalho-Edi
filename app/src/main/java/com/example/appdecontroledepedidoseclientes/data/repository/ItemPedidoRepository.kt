package com.example.appdecontroledepedidoseclientes.data.repository

import com.example.appdecontroledepedidoseclientes.data.dao.ItemPedidoDao
import com.example.appdecontroledepedidoseclientes.data.entity.ItemPedido
import kotlinx.coroutines.flow.Flow

/**
 * --- REPOSITÓRIO ITEMPEDIDO (Melhoria Técnica 13) ---
 * Fornece uma camada de abstração entre o ViewModel e o DAO.
 */
class ItemPedidoRepository(private val itemPedidoDao: ItemPedidoDao) {

    suspend fun insert(itemPedido: ItemPedido): Long = itemPedidoDao.insert(itemPedido)

    suspend fun insertAll(itens: List<ItemPedido>) = itemPedidoDao.insertAll(itens)

    suspend fun update(itemPedido: ItemPedido) = itemPedidoDao.update(itemPedido)

    suspend fun delete(itemPedido: ItemPedido) = itemPedidoDao.delete(itemPedido)

    fun getByPedidoId(pedidoId: Int): Flow<List<ItemPedido>> = itemPedidoDao.getByPedidoId(pedidoId)

    suspend fun getByPedidoIdSync(pedidoId: Int): List<ItemPedido> = itemPedidoDao.getByPedidoIdSync(pedidoId)

    suspend fun deleteByPedidoId(pedidoId: Int) = itemPedidoDao.deleteByPedidoId(pedidoId)

    fun getCount(): Flow<Int> = itemPedidoDao.getCount()
}

