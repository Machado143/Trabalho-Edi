package com.example.appdecontroledepedidoseclientes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * --- ENTIDADE ITEMPEDIDO (Melhoria Técnica 13) ---
 * Representa um item individual dentro de um pedido.
 * Permite que cada pedido contenha múltiplos produtos com quantidades diferentes.
 */
@Entity(
    tableName = "itens_pedido",
    foreignKeys = [
        ForeignKey(
            entity = Pedido::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ItemPedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pedidoId: Int,
    val produtoId: Int,
    val quantidade: Int,
    val valorUnitario: Double
)



