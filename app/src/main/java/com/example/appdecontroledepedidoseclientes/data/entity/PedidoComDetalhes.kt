package com.example.appdecontroledepedidoseclientes.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PedidoComDetalhes(
    @Embedded val pedido: Pedido,
    
    @Relation(
        parentColumn = "clienteId",
        entityColumn = "id"
    )
    val cliente: Cliente,
    
    @Relation(
        parentColumn = "produtoId",
        entityColumn = "id"
    )
    val produto: Produto
)

