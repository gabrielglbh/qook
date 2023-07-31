package com.gabr.gabc.qook.infrastructure.user

import com.gabr.gabc.qook.domain.user.UserRepository
import com.google.firebase.firestore.auth.User

class UserRepositoryImpl : UserRepository {
    override suspend fun signInUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String, user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun removeAccount(oldPassword: String, newPassword: String, user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(user: com.gabr.gabc.qook.domain.user.User) {
        TODO("Not yet implemented")
    }

    override suspend fun removeUser(user: com.gabr.gabc.qook.domain.user.User) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: com.gabr.gabc.qook.domain.user.User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(): com.gabr.gabc.qook.domain.user.User {
        TODO("Not yet implemented")
    }
}