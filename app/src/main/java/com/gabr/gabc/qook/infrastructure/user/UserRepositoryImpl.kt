package com.gabr.gabc.qook.infrastructure.user

import com.gabr.gabc.qook.domain.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.gabr.gabc.qook.domain.user.User as domainUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor (
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : UserRepository {
    override suspend fun signInUser(email: String, password: String) {
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

    override suspend fun createUser(name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeUser(user: domainUser) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: domainUser) {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(): domainUser {
        TODO("Not yet implemented")
    }

    override suspend fun updateAvatar(image: String) {
        TODO("Not yet implemented")
    }
}