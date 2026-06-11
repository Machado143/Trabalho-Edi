package com.example.appdecontroledepedidoseclientes.data.dao

import androidx.room.*
import com.example.appdecontroledepedidoseclientes.data.entity.Cliente
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM cliente ORDER BY nome ASC")
    fun getAll(): Flow<List<Cliente>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cliente: Cliente)

    @Update
    suspend fun update(cliente: Cliente)

    @Delete
    suspend fun delete(cliente: Cliente)

    @Query("SELECT * FROM cliente WHERE id = :id")
    suspend fun getById(id: Int): Cliente?
}

