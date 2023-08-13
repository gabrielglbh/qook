package com.gabr.gabc.qook.presentation.profilePage.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.domain.user.UserRepository
import com.gabr.gabc.qook.presentation.shared.ResizeImageUtil.Companion.resizeImageToFile
import com.gabr.gabc.qook.presentation.shared.providers.ContentResolverProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val provider: ContentResolverProvider
) :
    ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    fun setDataForLocalLoading(user: User?, avatar: Uri) {
        _userState.value = UserState(user = user, avatarUrl = avatar)
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.signOut()
        }
    }

    fun getUser(onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getUser()
            result.fold(
                ifLeft = {
                    _userState.value = _userState.value.copy(error = it.error)
                    onError(it.error)
                },
                ifRight = {
                    _userState.value = _userState.value.copy(user = it)
                }
            )
        }
    }

    fun updateUser(user: User, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateUser(user)
            result.fold(
                ifLeft = {
                    onError(it.error)
                },
                ifRight = {
                    _userState.value = _userState.value.copy(user = user)
                }
            )
        }
    }

    fun getAvatar(onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            val result = repository.getAvatar()
            result.fold(
                ifLeft = {
                    onError?.let { f -> f(it.error) }
                },
                ifRight = {
                    _userState.value = _userState.value.copy(avatarUrl = it)
                }
            )
        }
    }

    fun updateAvatar(uri: Uri, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            val result =
                repository.updateAvatar(resizeImageToFile(uri, provider.contentResolver()).path)
            result.fold(
                ifLeft = {
                    onError?.let { f -> f(it.error) }
                },
                ifRight = {
                    _userState.value = _userState.value.copy(avatarUrl = it)
                }
            )
        }
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.changePassword(oldPassword, newPassword)
            result.fold(
                ifLeft = {
                    onError(it.error)
                },
                ifRight = {
                    onSuccess()
                }
            )
        }
    }

    fun deleteAccount(
        oldPassword: String,
        newPassword: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.removeAccount(oldPassword, newPassword)
            result.fold(
                ifLeft = {
                    onError(it.error)
                },
                ifRight = {
                    onSuccess()
                }
            )
        }
    }
}