package com.gabr.gabc.qook.infrastructure.sharedPlanning

import android.net.Uri
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningFailure
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningRepository
import com.gabr.gabc.qook.domain.sharedPlanning.toDto
import com.gabr.gabc.qook.domain.storage.StorageRepository
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.domain.user.UserRepository
import com.gabr.gabc.qook.presentation.shared.Globals
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SharedPlanningRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val ingredientsRepository: IngredientsRepository,
    private val userRepository: UserRepository,
    private val res: StringResourcesProvider,
    private val storage: StorageRepository
) : SharedPlanningRepository {
    override suspend fun createSharedPlanning(sharedPlanning: SharedPlanning): Either<SharedPlanningFailure, SharedPlanning> {
        try {
            auth.currentUser?.let {
                val ref = db.collection(Globals.DB_GROUPS).document()
                val planningId = ref.path.split("/").last()

                ref.set(sharedPlanning.toDto().copy(users = listOf(it.uid))).await()

                if (sharedPlanning.photo.host != Globals.FIREBASE_HOST && sharedPlanning.photo != Uri.EMPTY) {
                    storage.uploadImage(
                        sharedPlanning.photo,
                        "${Globals.STORAGE_GROUPS}$planningId/${Globals.STORAGE_AVATAR}"
                    )
                }

                return Right(sharedPlanning.copy(id = planningId))
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningCreationFailed(res.getString(R.string.err_planning_update)))
        }
    }

    override suspend fun updateSharedPlanning(
        sharedPlanning: SharedPlanning,
        id: String
    ): Either<SharedPlanningFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_GROUPS).document(id)
                    .update(sharedPlanning.toDto().toMap()).await()

                if (sharedPlanning.photo.host != Globals.FIREBASE_HOST && sharedPlanning.photo != Uri.EMPTY) {
                    storage.uploadImage(
                        sharedPlanning.photo,
                        "${Globals.STORAGE_GROUPS}$id/${Globals.STORAGE_AVATAR}"
                    )
                }

                return Right(Unit)
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningCreationFailed(res.getString(R.string.err_planning_update)))
        }
    }

    override suspend fun addUserToSharedPlanning(id: String): Either<SharedPlanningFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_GROUPS).document(id)
                    .update(Globals.OBJ_SHARED_PLANNING_USERS, FieldValue.arrayUnion(it.uid))
                    .await()

                return Right(Unit)
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningCreationFailed(res.getString(R.string.err_planning_update)))
        }
    }

    override suspend fun removeUserToSharedPlanning(
        id: String,
        uid: String
    ): Either<SharedPlanningFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_GROUPS).document(id)
                    .update(Globals.OBJ_SHARED_PLANNING_USERS, FieldValue.arrayRemove(uid)).await()

                return Right(Unit)
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningCreationFailed(res.getString(R.string.err_planning_update)))
        }
    }

    override suspend fun deleteSharedPlanning(sharedPlanning: SharedPlanning): Either<SharedPlanningFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_GROUPS).document(sharedPlanning.id).delete().await()

                if (sharedPlanning.photo != Uri.EMPTY) {
                    storage.deleteImage("${Globals.STORAGE_GROUPS}${sharedPlanning.id}/${Globals.STORAGE_AVATAR}")
                }

                return Right(Unit)
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningDoesNotExist(res.getString(R.string.err_planning_update)))
        }
    }

    override fun getSharedPlannings() =
        callbackFlow {
            auth.currentUser?.let {
                val listener = db.collection(Globals.DB_GROUPS)
                    .whereArrayContains(Globals.OBJ_SHARED_PLANNING_USERS, it.uid)
                    .addSnapshotListener { value, _ ->
                        if (value == null) trySend(
                            Left(
                                SharedPlanningFailure.SharedPlanningRetrievalFailed(
                                    res.getString(R.string.err_plannings_retrieval)
                                )
                            )
                        )
                        else {
                            CoroutineScope(Dispatchers.IO).launch {
                                val sharedPlannings = mutableListOf<SharedPlanning>()
                                value.documents.forEach { doc ->
                                    val dto = doc.toObject<SharedPlanningDto>()
                                    dto?.let {
                                        var group = dto.toDomain()

                                        if (dto.hasPhoto) {
                                            val resStorage =
                                                storage.getDownloadUrl("${Globals.STORAGE_GROUPS}${dto.id}/${Globals.STORAGE_AVATAR}")
                                            resStorage.fold(
                                                ifLeft = {},
                                                ifRight = { uri -> group = group.copy(photo = uri) }
                                            )
                                        }
                                        sharedPlannings.add(group)
                                    }
                                }
                                trySend(Right(sharedPlannings))
                            }
                        }
                    }
                awaitClose {
                    listener.remove()
                }
            }
            trySend(
                Left(
                    SharedPlanningFailure.NotAuthenticated(
                        res.getString(R.string.error_user_not_auth)
                    )
                )
            )
        }

    override suspend fun getSharedPlanning(id: String): Either<SharedPlanningFailure, SharedPlanning> {
        try {
            auth.currentUser?.let {
                val res = db.collection(Globals.DB_GROUPS).document(id).get().await()

                val dto = res.toObject<SharedPlanningDto>()
                dto?.let { d ->
                    var group = d.toDomain()
                    val users = mutableListOf<User>()

                    if (d.hasPhoto) {
                        val resStorage =
                            storage.getDownloadUrl("${Globals.STORAGE_GROUPS}$id/${Globals.STORAGE_AVATAR}")
                        resStorage.fold(
                            ifLeft = {},
                            ifRight = { uri -> group = group.copy(photo = uri) }
                        )
                    }
                    d.users.forEach { uid ->
                        val userRes = userRepository.getUserFromId(uid)
                        userRes.fold(
                            ifLeft = {},
                            ifRight = { user ->
                                users.add(user)
                            }
                        )
                    }
                    group = group.copy(users = users)
                    return Right(group)
                }
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningRetrievalFailed(res.getString(R.string.err_plannings_retrieval)))
        }
    }
}