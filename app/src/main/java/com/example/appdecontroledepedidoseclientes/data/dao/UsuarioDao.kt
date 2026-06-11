package com.example.appdecontroledepedidoseclientes.data.dao

import androidx.room.*
import com.example.appdecontroledepedidoseclientes.data.entity.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuario WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario)
}

