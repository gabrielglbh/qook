package com.gabr.gabc.qook.domain.user

import arrow.core.Either
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import com.gabr.gabc.qook.domain.user.User as domainUser

interface UserRepository {
    suspend fun getCurrentUser(): FirebaseUser?
    suspend fun signInUser(email: String, password: String): Either<UserFailure, FirebaseUser>
    suspend fun createUser(email: String, password: String): Either<UserFailure, FirebaseUser>
    suspend fun signOut()
    suspend fun changePassword(oldPassword: String, newPassword: String, user: User)
    suspend fun removeAccount(oldPassword: String, newPassword: String, user: User)
    suspend fun createUserInDB(user: domainUser): Either<UserFailure, Unit>
    suspend fun removeUser(user: domainUser)
    suspend fun updateUser(user: domainUser)
    suspend fun getUser(): Either<UserFailure, domainUser>
    suspend fun updateAvatar(image: String)
}