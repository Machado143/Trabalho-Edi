package com.example.appdecontroledepedidoseclientes.data.dao

import androidx.room.*
import com.example.appdecontroledepedidoseclientes.data.entity.ItemPedido
import kotlinx.coroutines.flow.Flow

/**
 * --- DAO PARA ITEMPEDIDO (Melhoria Técnica 13) ---
 * Gerencia as operações CRUD para os itens de pedido.
 */
@Dao
interface ItemPedidoDao {

    @Insert
    suspend fun insert(itemPedido: ItemPedido): Long

    @Insert
    suspend fun insertAll(itens: List<ItemPedido>)

    @Update
    suspend fun update(itemPedido: ItemPedido)

    @Delete
    suspend fun delete(itemPedido: ItemPedido)

    @Query("SELECT * FROM itens_pedido WHERE pedidoId = :pedidoId")
    fun getByPedidoId(pedidoId: Int): Flow<List<ItemPedido>>

    @Query("SELECT * FROM itens_pedido WHERE pedidoId = :pedidoId")
    suspend fun getByPedidoIdSync(pedidoId: Int): List<ItemPedido>

    @Query("DELETE FROM itens_pedido WHERE pedidoId = :pedidoId")
    suspend fun deleteByPedidoId(pedidoId: Int)

    @Query("SELECT COUNT(*) FROM itens_pedido")
    fun getCount(): Flow<Int>
}

