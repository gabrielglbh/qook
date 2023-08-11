package com.gabr.gabc.qook.presentation.loginPage.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun signInUser() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                updateIsSigningIn(true)
            }
            val state = _loginState.value
            val result = repository.signInUser(state.email, state.password)
            result.fold(
                ifLeft = {
                    _loginState.value = state.copy(error = it.error)
                },
                ifRight = {
                    withContext(Dispatchers.Main) {
                        updateIsSigningIn(false)
                    }
                }
            )
        }
    }

    fun createUser() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                updateIsSigningIn(true)
            }
            val state = _loginState.value
            val userCreation = repository.createUser(state.email, state.password)
            userCreation.fold(
                ifLeft = {
                    _loginState.value = state.copy(error = it.error)
                },
                ifRight = {
                    val userCreationInDB = repository.createUserInDB(User(state.name, state.email))
                    userCreationInDB.fold(
                        ifLeft = {
                            _loginState.value = state.copy(error = it.error)
                        },
                        ifRight = {}
                    )
                }
            )
            withContext(Dispatchers.Main) {
                updateIsSigningIn(false)
            }
        }
    }
}