package com.gabr.gabc.qook.presentation.loginPage.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.application.LoginService
import com.gabr.gabc.qook.domain.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val service: LoginService) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    var isSigningIn by mutableStateOf(false)
        private set

    private fun updateIsSigningIn(value: Boolean) {
        isSigningIn = value
    }

    fun updateLoginState(state: LoginState) {
        _loginState.value = state
    }

    fun signInUser() {
        viewModelScope.launch {
            val state = _loginState.value
            updateIsSigningIn(true)
            val (_, error) = service.signInUser(state.email, state.password)
            if (error.isNotEmpty()) _loginState.value = state.copy(error = error)
            updateIsSigningIn(false)
        }
    }

    fun createUser() {
        viewModelScope.launch {
            val state = _loginState.value
            updateIsSigningIn(true)
            val (user, error) = service.createUser(state.email, state.password)
            if (user != null) {
                val errorDB = service.createUserInDB(User(state.name, state.email))
                if (errorDB.isNotEmpty()) _loginState.value = state.copy(error = errorDB)
            } else {
                _loginState.value = state.copy(error = error)
            }
            updateIsSigningIn(false)
        }
    }
}