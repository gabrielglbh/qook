package com.gabr.gabc.qook.presentation.loginPage.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.domain.user.UserRepository
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val resources: StringResourcesProvider
) : ViewModel() {
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
            if (state.name.isEmpty()) {
                _formState.value = state.copy(
                    error = resources.getString(R.string.error_empty_form),
                    password = ""
                )
            } else {
                val userCreation = repository.createUser(state.email, state.password)
                userCreation.fold(
                    ifLeft = {
                        _formState.value = state.copy(error = it.error, password = "")
                    },
                    ifRight = {
                        val userCreationInDB =
                            repository.createUserInDB(
                                User(
                                    id = "",
                                    name = state.name,
                                    email = state.email,
                                    language = Locale.getDefault().language.uppercase(),
                                    messagingToken = "",
                                    adminOf = listOf(),
                                    hasPhoto = false,
                                )
                            )
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
            }
            withContext(Dispatchers.Main) {
                setIsLoading(false)
            }
        }
    }
}