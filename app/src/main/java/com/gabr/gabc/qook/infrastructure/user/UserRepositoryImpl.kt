package com.gabr.gabc.qook.infrastructure.user

import com.gabr.gabc.qook.domain.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.gabr.gabc.qook.domain.user.User as domainUser
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor (
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : UserRepository {
    override suspend fun signInUser(email: String, password: String): Pair<FirebaseUser?, String> {
        return try {
            Pair(auth.signInWithEmailAndPassword(email, password).await().user, "")
        } catch (err: FirebaseAuthException) {
            Pair(null, "ERR(${err.errorCode}): Sign in failed")
        }
    }

    override suspend fun createUser(email: String, password: String): Pair<FirebaseUser?, String> {
        return try {
            Pair(auth.createUserWithEmailAndPassword(email, password).await().user, "")
        } catch (err: FirebaseAuthException) {
            Pair(null, "ERR(${err.errorCode}): Sign in failed")
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

    override suspend fun createUserInDB(user: domainUser): String {
        return try {
            auth.currentUser?.let {
                db.collection("USERS")
                    .document(it.uid)
                    .set(Json.encodeToJsonElement(domainUser))
                    .await()
                ""
            }
            "ERR(Auth): Not authenticated"
        } catch (err: FirebaseFirestoreException) {
            "ERR(${err.code}): User creation failed"
        }
    }

    override suspend fun removeUser(user: domainUser) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: domainUser) {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(): Pair<domainUser?, String> {
        return try {
            auth.currentUser?.let {
                val ref = db.collection("USERS").document(it.uid).get().await()
                if (!ref.exists()) Pair(null, "ERR(Auth): User does not exist")
                else {
                    ref.toObject<UserDto>()?.let {dto ->
                        Pair(dto.toDomain(), "")
                    }
                    Pair(null, "ERR(Auth): Error translating object")
                }
            }

            Pair(null, "ERR(Auth): Not authenticated")
        } catch (err: FirebaseFirestoreException) {
            Pair(null, "ERR(${err.code}): User retrieval failed")
        }
    }

    override suspend fun updateAvatar(image: String) {
        TODO("Not yet implemented")
    }
}