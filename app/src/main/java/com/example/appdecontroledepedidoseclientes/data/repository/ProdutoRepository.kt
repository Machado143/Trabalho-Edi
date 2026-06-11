package com.example.appdecontroledepedidoseclientes.data.repository

import com.example.appdecontroledepedidoseclientes.data.dao.ProdutoDao
import com.example.appdecontroledepedidoseclientes.data.entity.Produto

class ProdutoRepository(private val dao: ProdutoDao) {
    val produtos = dao.getAll()
    suspend fun insert(produto: Produto) = dao.insert(produto)
    suspend fun update(produto: Produto) = dao.update(produto)
    suspend fun delete(produto: Produto) = dao.delete(produto)
    suspend fun getById(id: Int) = dao.getById(id)
}

