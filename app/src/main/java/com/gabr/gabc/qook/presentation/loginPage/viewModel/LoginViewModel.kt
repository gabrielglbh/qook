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
    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set

    private fun setIsLoading(value: Boolean) {
        isLoading = value
    }

    fun updateLoginState(state: LoginFormState) {
        _formState.value = state
    }

    fun signInUser(ifRight: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                setIsLoading(true)
            }
            val state = _formState.value
            val result = repository.signInUser(state.email, state.password)
            result.fold(
                ifLeft = {
                    _formState.value = state.copy(error = it.error, password = "")
                },
                ifRight = {
                    ifRight()
                }
            )
            withContext(Dispatchers.Main) {
                setIsLoading(false)
            }
        }
    }

    fun createUser(ifRight: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                setIsLoading(true)
            }
            val state = _formState.value
            val userCreation = repository.createUser(state.email, state.password)
            userCreation.fold(
                ifLeft = {
                    _formState.value = state.copy(error = it.error, password = "")
                },
                ifRight = {
                    val userCreationInDB = repository.createUserInDB(User(state.name, state.email))
                    userCreationInDB.fold(
                        ifLeft = {
                            _formState.value = state.copy(error = it.error, password = "")
                        },
                        ifRight = {
                            ifRight()
                        }
                    )
                }
            )
            withContext(Dispatchers.Main) {
                setIsLoading(false)
            }
        }
    }
}