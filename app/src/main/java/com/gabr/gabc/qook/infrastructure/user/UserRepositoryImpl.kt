package com.gabr.gabc.qook.infrastructure.user

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import com.gabr.gabc.qook.domain.storage.StorageRepository
import com.gabr.gabc.qook.domain.user.UserFailure
import com.gabr.gabc.qook.domain.user.UserRepository
import com.gabr.gabc.qook.domain.user.toDto
import com.gabr.gabc.qook.presentation.shared.Globals
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import com.gabr.gabc.qook.domain.user.User as domainUser

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: StorageRepository,
    private val res: StringResourcesProvider,
    private val planningRepository: PlanningRepository,
    private val ingredientsRepository: IngredientsRepository,
) : UserRepository {
    override suspend fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override suspend fun signInUser(
        email: String,
        password: String
    ): Either<UserFailure, FirebaseUser> {
        val signInFailed = res.getString(R.string.error_sign_in_failed)
        try {
            auth.signInWithEmailAndPassword(email, password).await().user?.let {
                return Right(it)
            }
            return Left(UserFailure.SignInFailed(signInFailed))
        } catch (err: FirebaseAuthException) {
            return Left(UserFailure.SignInFailed("${err.errorCode}: $signInFailed"))
        } catch (err: IllegalArgumentException) {
            return Left(UserFailure.SignInFailed(res.getString(R.string.error_empty_form)))
        }
    }

    override suspend fun createUser(
        email: String,
        password: String
    ): Either<UserFailure, FirebaseUser> {
        val creationFailed = res.getString(R.string.error_register_failed)
        try {
            auth.createUserWithEmailAndPassword(email, password).await().user?.let {
                return Right(it)
            }
            return Left(UserFailure.UserCreationFailed(creationFailed))
        } catch (err: FirebaseAuthException) {
            return Left(UserFailure.UserCreationFailed("${err.errorCode}: $creationFailed"))
        } catch (err: IllegalArgumentException) {
            return Left(UserFailure.UserCreationFailed(res.getString(R.string.error_empty_form)))
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Either<UserFailure, Unit> {
        try {
            auth.currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
                return Right(Unit)
            }
            return Left(
                UserFailure.PasswordChangeFailure(
                    res.getString(R.string.error_profile_password_change)
                )
            )
        } catch (err: FirebaseAuthException) {
            return Left(
                UserFailure.PasswordChangeFailure(
                    "${err.errorCode}: " +
                            res.getString(R.string.error_profile_password_change)
                )
            )
        }
    }

    override suspend fun createUserInDB(user: domainUser): Either<UserFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_USER)
                    .document(it.uid)
                    .set(user.toDto().copy(language = Locale.getDefault().language.uppercase()))
                    .await()

                val planningRes = planningRepository.resetPlanning()
                return planningRes.fold<Either<UserFailure, Unit>>(
                    ifLeft = {
                        return Left(
                            UserFailure.UserCreationFailed(res.getString(R.string.error_register_failed))
                        )
                    },
                    ifRight = {
                        val ingredientsRes = ingredientsRepository.resetIngredients()
                        return ingredientsRes.fold<Either<UserFailure, Unit>>(
                            ifLeft = {
                                return Left(
                                    UserFailure.UserCreationFailed(res.getString(R.string.error_register_failed))
                                )
                            },
                            ifRight = {
                                return Right(Unit)
                            }
                        )
                    }
                )
            }
            return Left(UserFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                UserFailure.UserCreationFailed(
                    "${err.code}: " +
                            res.getString(R.string.error_register_failed)
                )
            )
        }
    }

    override suspend fun removeAccount(
        oldPassword: String,
        newPassword: String
    ): Either<UserFailure, Unit> {
        try {
            auth.currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
                user.reauthenticate(credential).await()
                // TODO: NEEDS
                storage.deleteImage(Globals.STORAGE_AVATAR)
                user.delete().await()
                return Right(Unit)
            }
            return Left(UserFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: Exception) {
            return Left(UserFailure.UserRemovalFailure(res.getString(R.string.error_profile_delete_account)))
        }
    }

    override suspend fun updateUser(user: domainUser): Either<UserFailure, Unit> {
        auth.currentUser?.let {
            db.collection(Globals.DB_USER).document(it.uid).update(user.toDto().toMap()).await()
            return Right(Unit)
        }
        return Left(UserFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
    }

    override suspend fun getUser(): Either<UserFailure, domainUser> {
        try {
            auth.currentUser?.let {
                val ref = db.collection(Globals.DB_USER).document(it.uid).get().await()
                if (!ref.exists()) Left(
                    UserFailure.UserDoesNotExist(res.getString(R.string.error_user_does_not_exist))
                )
                else {
                    ref.toObject<UserDto>()?.let { dto ->
                        var user = dto.toDomain()
                        val result = storage.getDownloadUrl(Globals.STORAGE_AVATAR)
                        result.fold(
                            ifLeft = {},
                            ifRight = { uri -> user = user.copy(photo = uri) }
                        )
                        return Right(user)
                    }
                    return Left(
                        UserFailure.UserTranslationFailed(res.getString(R.string.error_user_corrupted))
                    )
                }
            }
            return Left(UserFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                UserFailure.UserDoesNotExist(
                    "${err.code}: " +
                            res.getString(R.string.error_user_retrieval)
                )
            )
        }
    }
}