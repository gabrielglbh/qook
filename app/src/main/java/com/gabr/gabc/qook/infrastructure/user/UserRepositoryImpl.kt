package com.gabr.gabc.qook.infrastructure.user

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.domain.user.UserFailure
import com.gabr.gabc.qook.domain.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject
import com.gabr.gabc.qook.domain.user.User as domainUser

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : UserRepository {
    override suspend fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override suspend fun signInUser(
        email: String,
        password: String
    ): Either<UserFailure, FirebaseUser> {
        try {
            auth.signInWithEmailAndPassword(email, password).await().user?.let {
                return Right(it)
            }
            return Left(UserFailure.SignInFailed("Sign in failed. Try again"))
        } catch (err: FirebaseAuthException) {
            return Left(UserFailure.SignInFailed("${err.errorCode}: Sign in failed. Try again"))
        } catch (err: IllegalArgumentException) {
            return Left(UserFailure.SignInFailed("Fill in the form correctly and try again"))
        }
    }

    override suspend fun createUser(
        email: String,
        password: String
    ): Either<UserFailure, FirebaseUser> {
        try {
            auth.createUserWithEmailAndPassword(email, password).await().user?.let {
                return Right(it)
            }
            return Left(UserFailure.UserCreationFailed("Creation failed. Try again"))
        } catch (err: FirebaseAuthException) {
            return Left(UserFailure.UserCreationFailed("${err.errorCode}: Creation failed. Try again"))
        } catch (err: IllegalArgumentException) {
            return Left(UserFailure.UserCreationFailed("Fill in the form correctly and try again"))
        }
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

    override suspend fun createUserInDB(user: domainUser): Either<UserFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection("USERS")
                    .document(it.uid)
                    .set(Json.encodeToJsonElement(domainUser))
                    .await()
                return Right(Unit)
            }
            return Left(UserFailure.NotAuthenticated("Your user is not authenticated. Try again"))
        } catch (err: FirebaseFirestoreException) {
            return Left(UserFailure.UserCreationFailed("${err.code}: User creation failed"))
        }
    }

    override suspend fun removeUser(user: domainUser) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: domainUser) {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(): Either<UserFailure, domainUser> {
        try {
            auth.currentUser?.let {
                val ref = db.collection("USERS").document(it.uid).get().await()
                if (!ref.exists()) Left(UserFailure.UserDoesNotExist("Your user does not exist"))
                else {
                    ref.toObject<UserDto>()?.let { dto ->
                        return Right(dto.toDomain())
                    }
                    return Left(UserFailure.UserTranslationFailed("Your user is corrupted. Try again"))
                }
            }
            return Left(UserFailure.NotAuthenticated("Your user is not authenticated. Try again"))
        } catch (err: FirebaseFirestoreException) {
            return Left(UserFailure.UserDoesNotExist("${err.code}: Could not get user"))
        }
    }

    override suspend fun updateAvatar(image: String) {
        TODO("Not yet implemented")
    }
}