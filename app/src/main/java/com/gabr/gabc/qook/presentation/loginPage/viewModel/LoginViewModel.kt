package com.gabr.gabc.qook.presentation.loginPage.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

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

    suspend fun signInUser() {
        val state = _loginState.value
        updateIsSigningIn(true)
        val (_, error) = repository.signInUser(state.email, state.password)
        if (error.isNotEmpty()) _loginState.value = state.copy(error = error)
        updateIsSigningIn(false)
    }

    suspend fun createUser() {
        val state = _loginState.value
        updateIsSigningIn(true)
        val (user, error) = repository.createUser(state.email, state.password)
        if (user != null) {
            val errorDB = repository.createUserInDB(User(state.name, state.email))
            if (errorDB.isNotEmpty()) _loginState.value = state.copy(error = errorDB)
        } else {
            _loginState.value = state.copy(error = error)
        }
        updateIsSigningIn(false)
    }
}