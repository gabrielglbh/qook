package com.gabr.gabc.qook.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.gabr.gabc.qook.domain.user.UserRepository

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    suspend fun signInUser(name: String, email: String, password: String) {
        repository.signInUser(email, password)
        repository.createUser(name)
    }
}