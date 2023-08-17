package com.gabr.gabc.qook.domain.user

import arrow.core.Either
import com.google.firebase.auth.FirebaseUser
import com.gabr.gabc.qook.domain.user.User as domainUser

interface UserRepository {
    suspend fun getCurrentUser(): FirebaseUser?
    suspend fun signInUser(email: String, password: String): Either<UserFailure, FirebaseUser>
    suspend fun createUser(email: String, password: String): Either<UserFailure, FirebaseUser>
    suspend fun signOut()
    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Either<UserFailure, Unit>

    suspend fun removeAccount(oldPassword: String, newPassword: String): Either<UserFailure, Unit>
    suspend fun createUserInDB(user: domainUser): Either<UserFailure, Unit>
    suspend fun updateUser(user: domainUser): Either<UserFailure, Unit>
    suspend fun getUser(): Either<UserFailure, domainUser>
}