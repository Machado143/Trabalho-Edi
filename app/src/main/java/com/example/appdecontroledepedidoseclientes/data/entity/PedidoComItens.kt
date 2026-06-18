package com.example.appdecontroledepedidoseclientes.data.entity

/**
 * --- SUPPORT FOR MULTIPLE PRODUCTS PER ORDER (Enhancement 13) ---
 * These are simple data classes for displaying order information.
 * They are NOT Room entities - just data transfer objects.
 */

/**
 * Represents a single item in an order with product details
 */
data class ItemPedidoDisplay(
    val itemId: Int,
    val pedidoId: Int,
    val produtoId: Int,
    val produtoNome: String,
    val quantidade: Int,
    val valorUnitario: Double
)

/**
 * Represents a complete order with all its items
 */
data class PedidoComItensDisplay(
    val pedidoId: Int,
    val clienteId: Int,
    val clienteNome: String,
    val data: String,
    val hora: String,
    val valorTotal: Double,
    val status: String,
    val itens: List<ItemPedidoDisplay> = emptyList()
)



