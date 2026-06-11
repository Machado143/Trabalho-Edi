package com.example.appdecontroledepedidoseclientes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey val username: String,
    val senhaHash: String
)

