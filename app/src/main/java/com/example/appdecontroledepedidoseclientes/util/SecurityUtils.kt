package com.example.appdecontroledepedidoseclientes.util

import java.security.MessageDigest

/**
 * Utilitário de segurança para o aplicativo.
 */
object SecurityUtils {
    /**
     * Transforma uma string (senha) em um hash SHA-256.
     * Isso garante que a senha real nunca seja salva no banco de dados.
     */
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
