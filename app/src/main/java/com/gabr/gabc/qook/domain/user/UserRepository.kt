package com.gabr.gabc.qook.domain.user

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import com.gabr.gabc.qook.domain.user.User as domainUser

interface UserRepository {
    suspend fun signInUser(email: String, password: String): Pair<FirebaseUser?, String>
    suspend fun createUser(email: String, password: String): Pair<FirebaseUser?, String>
    suspend fun signOut()
    suspend fun changePassword(oldPassword: String, newPassword: String, user: User)
    suspend fun removeAccount(oldPassword: String, newPassword: String, user: User)
    suspend fun createUserInDB(user: domainUser): String
    suspend fun removeUser(user: domainUser)
    suspend fun updateUser(user: domainUser)
    suspend fun getUser(): Pair<domainUser?, String>
    suspend fun updateAvatar(image: String)
}