package com.example.appdecontroledepedidoseclientes.data.dao

import androidx.room.*
import com.example.appdecontroledepedidoseclientes.data.entity.Produto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produto ORDER BY nome ASC")
    fun getAll(): Flow<List<Produto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(produto: Produto)

    @Update
    suspend fun update(produto: Produto)

    @Delete
    suspend fun delete(produto: Produto)

    @Query("SELECT * FROM produto WHERE id = :id")
    suspend fun getById(id: Int): Produto?

    @Query("SELECT COUNT(*) FROM produto")
    fun getCount(): Flow<Int>
}
