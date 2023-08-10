package com.gabr.gabc.qook.application

import com.gabr.gabc.qook.domain.user.UserRepository
import com.google.firebase.auth.FirebaseUser
import com.gabr.gabc.qook.domain.user.User as domainUser
import javax.inject.Inject

class LoginService @Inject constructor(private val repository: UserRepository) {
    suspend fun signInUser(email: String, password: String): Pair<FirebaseUser?, String> {
        return repository.signInUser(email, password)
    }

    suspend fun createUser(email: String, password: String): Pair<FirebaseUser?, String> {
        return repository.createUser(email, password)
    }

    suspend fun createUserInDB(user: domainUser): String {
        return repository.createUserInDB(user)
    }
}