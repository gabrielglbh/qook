package com.gabr.gabc.qook.domain.user

import com.google.firebase.firestore.auth.User
import com.gabr.gabc.qook.domain.user.User as domainUser

interface UserRepository {
    suspend fun signInUser(user: User)
    suspend fun signOut()
    suspend fun changePassword(oldPassword: String, newPassword: String, user: User)
    suspend fun removeAccount(oldPassword: String, newPassword: String, user: User)
    suspend fun createUser(user: domainUser)
    suspend fun removeUser(user: domainUser)
    suspend fun updateUser(user: domainUser)
    suspend fun getUser(): domainUser
}