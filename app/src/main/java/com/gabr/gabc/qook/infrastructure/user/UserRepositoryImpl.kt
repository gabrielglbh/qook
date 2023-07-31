package com.gabr.gabc.qook.infrastructure.user

import com.gabr.gabc.qook.domain.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.gabr.gabc.qook.domain.user.User as domainUser
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(private val auth: FirebaseAuth, private val db: FirebaseFirestore) : UserRepository {
    override suspend fun signInUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String, user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun removeAccount(oldPassword: String, newPassword: String, user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(name: String) {
        auth.currentUser?.let {
            db.collection("USERS").document().set(UserDto(
                name,
                it.email!!,
                null,
                1
            )).await()
        }
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
}