package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdecontroledepedidoseclientes.data.dao.UsuarioDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, senhaHash: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val usuario = usuarioDao.getByUsername(username)
            if (usuario != null && usuario.senhaHash == senhaHash) {
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

