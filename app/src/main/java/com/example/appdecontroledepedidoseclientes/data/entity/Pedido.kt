package com.example.appdecontroledepedidoseclientes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * --- ENTIDADE PEDIDO (Atualizada para Melhoria Técnica 13) ---
 * Mantém os campos antigos (produtoId, quantidade) para compatibilidade,
 * mas agora também suporta múltiplos produtos via ItemPedido.
 * 
 * Os campos produtoId e quantidade são opcionais (podem ser nulos quando há ItemPedido).
 */
@Entity(
    tableName = "pedido",
    foreignKeys = [
        ForeignKey(entity = Cliente::class, parentColumns = ["id"], childColumns = ["clienteId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Produto::class, parentColumns = ["id"], childColumns = ["produtoId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("clienteId"), Index("produtoId")]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clienteId: Int,
    val produtoId: Int? = null, // Opcional - usado quando há apenas 1 produto
    val quantidade: Int? = null, // Opcional - usado quando há apenas 1 produto
    val data: String,
    val hora: String,
    val valorTotal: Double,
    val status: String = "Pendente" // Pendente, Em preparo, Entregue, Cancelado
)
