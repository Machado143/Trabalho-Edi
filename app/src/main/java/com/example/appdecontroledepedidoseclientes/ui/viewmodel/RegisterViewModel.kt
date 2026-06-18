package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdecontroledepedidoseclientes.data.dao.UsuarioDao
import com.example.appdecontroledepedidoseclientes.data.entity.Usuario
import com.example.appdecontroledepedidoseclientes.util.SecurityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    /**
     * Tenta registrar um novo usuário.
     * Valida o username e tenta inserir no banco.
     */
    fun register(username: String, senha: String, confirmSenha: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            // Validações
            when {
                username.isBlank() -> {
                    _registerState.value = RegisterState.Error("Nome de usuário não pode estar vazio")
                    return@launch
                }
                username.length < 3 -> {
                    _registerState.value = RegisterState.Error("Nome de usuário deve ter pelo menos 3 caracteres")
                    return@launch
                }
                senha.isBlank() -> {
                    _registerState.value = RegisterState.Error("Senha não pode estar vazia")
                    return@launch
                }
                senha.length < 4 -> {
                    _registerState.value = RegisterState.Error("Senha deve ter pelo menos 4 caracteres")
                    return@launch
                }
                senha != confirmSenha -> {
                    _registerState.value = RegisterState.Error("As senhas não conferem")
                    return@launch
                }
            }

            try {
                // Verifica se o usuário já existe
                val usuarioExistente = usuarioDao.getByUsername(username)
                if (usuarioExistente != null) {
                    _registerState.value = RegisterState.Error("Este nome de usuário já está em uso")
                    return@launch
                }

                // Cria o novo usuário com senha hasheada
                val senhaHash = SecurityUtils.hashPassword(senha)
                val novoUsuario = Usuario(username, senhaHash)
                usuarioDao.insert(novoUsuario)

                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Erro ao registrar: ${e.message}")
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}

