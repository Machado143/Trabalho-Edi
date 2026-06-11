package com.example.appdecontroledepedidoseclientes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "pedido",
    foreignKeys = [
        ForeignKey(entity = Cliente::class, parentColumns = ["id"], childColumns = ["clienteId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Produto::class, parentColumns = ["id"], childColumns = ["produtoId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("clienteId"), Index("produtoId")]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clienteId: Int,
    val produtoId: Int,
    val quantidade: Int,
    val data: String,
    val hora: String,
    val valorTotal: Double
)

