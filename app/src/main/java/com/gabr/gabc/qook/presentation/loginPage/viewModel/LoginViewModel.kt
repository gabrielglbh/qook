package com.gabr.gabc.qook.presentation.loginPage.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gabr.gabc.qook.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    suspend fun signInUser(state: LoginState) {
        updateIsSigningIn(true)
        delay(4000)
        //repository.signInUser(state.email, state.password)
        //repository.createUser(state.name)
        updateIsSigningIn(false)
    }
}