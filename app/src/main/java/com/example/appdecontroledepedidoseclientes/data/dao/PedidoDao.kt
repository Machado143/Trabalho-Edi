package com.example.appdecontroledepedidoseclientes.data.dao

import androidx.room.*
import com.example.appdecontroledepedidoseclientes.data.entity.Pedido
import com.example.appdecontroledepedidoseclientes.data.entity.PedidoComDetalhes
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Transaction
    @Query("SELECT * FROM pedido ORDER BY data DESC, hora DESC")
    fun getAllComDetalhes(): Flow<List<PedidoComDetalhes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pedido: Pedido)

    @Update
    suspend fun update(pedido: Pedido)

    @Delete
    suspend fun delete(pedido: Pedido)

    @Query("SELECT * FROM pedido WHERE id = :id")
    suspend fun getById(id: Int): Pedido?

    @Transaction
    @Query("SELECT * FROM pedido WHERE clienteId = :clienteId ORDER BY data DESC, hora DESC")
    fun getPedidosByCliente(clienteId: Int): Flow<List<PedidoComDetalhes>>

    @Query("SELECT COUNT(*) FROM pedido WHERE data = :today")
    fun getCountToday(today: String): Flow<Int>

    @Query("SELECT SUM(valorTotal) FROM pedido WHERE data LIKE :monthYear AND status != 'Cancelado'")
    fun getRevenueMonth(monthYear: String): Flow<Double?>
}
