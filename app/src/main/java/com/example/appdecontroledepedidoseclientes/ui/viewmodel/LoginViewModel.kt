package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdecontroledepedidoseclientes.data.dao.UsuarioDao
import com.example.appdecontroledepedidoseclientes.util.SecurityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    /**
     * Tenta realizar o login.
     * Agora aplicamos o HASH na senha digitada para comparar com o hash salvo no banco.
     */
    fun login(username: String, senhaPura: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val usuario = usuarioDao.getByUsername(username)
            
            // Geramos o hash da senha digitada
            val hashDigitado = SecurityUtils.hashPassword(senhaPura)
            
            if (usuario != null && usuario.senhaHash == hashDigitado) {
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Error("Usuário ou senha inválidos")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
