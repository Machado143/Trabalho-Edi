package com.example.appdecontroledepedidoseclientes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produto")
data class Produto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val descricao: String,
    val valor: Double,
    val quantidade: Int
)

