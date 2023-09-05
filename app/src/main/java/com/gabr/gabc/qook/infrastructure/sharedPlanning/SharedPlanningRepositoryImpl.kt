package com.gabr.gabc.qook.infrastructure.sharedPlanning

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningFailure
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningRepository
import com.gabr.gabc.qook.domain.sharedPlanning.toDto
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.domain.user.UserRepository
import com.gabr.gabc.qook.presentation.shared.Globals
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SharedPlanningRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val planningRepository: PlanningRepository,
    private val ingredientsRepository: IngredientsRepository,
    private val userRepository: UserRepository,
    private val res: StringResourcesProvider
) : SharedPlanningRepository {
    override suspend fun createSharedPlanning(sharedPlanning: SharedPlanning): Either<SharedPlanningFailure, SharedPlanning> {
        try {
            auth.currentUser?.let {
                val ref = db.collection(Globals.DB_GROUPS).document()
                val planningId = ref.path.split("/").last()

                ref.set(sharedPlanning.toDto().copy(users = listOf(it.uid))).await()

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

                return Right(Unit)
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningCreationFailed(res.getString(R.string.err_planning_update)))
        }
    }

    override suspend fun addUserToSharedPlanning(id: String): Either<SharedPlanningFailure, Unit> {
        try {
            // TODO: Permission DENIED?
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

    override suspend fun deleteSharedPlanning(id: String): Either<SharedPlanningFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_GROUPS).document(id).delete().await()
                return Right(Unit)
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningDoesNotExist(res.getString(R.string.err_planning_update)))
        }
    }

    override suspend fun getSharedPlannings(): Either<SharedPlanningFailure, List<SharedPlanning>> {
        try {
            auth.currentUser?.let {
                val plannings = mutableListOf<SharedPlanning>()
                val res = db.collection(Globals.DB_GROUPS)
                    .whereArrayContains(Globals.OBJ_SHARED_PLANNING_USERS, it.uid)
                    .get().await()

                res.forEach { doc ->
                    plannings.add(doc.toObject<SharedPlanningDto>().toDomain())
                }

                return Right(plannings)
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningRetrievalFailed(res.getString(R.string.err_plannings_retrieval)))
        }
    }

    override suspend fun getSharedPlanning(id: String): Either<SharedPlanningFailure, SharedPlanning> {
        try {
            auth.currentUser?.let {
                val res = db.collection(Globals.DB_GROUPS).document(id).get().await()

                val dto = res.toObject<SharedPlanningDto>()
                dto?.let { d ->
                    var planning = d.toDomain()
                    val ingredientRes = ingredientsRepository.getIngredientsOfShoppingList(id)
                    ingredientRes.fold(
                        ifLeft = {},
                        ifRight = { ingredients ->
                            planning = planning.copy(shoppingList = ingredients)
                        }
                    )
                    val planningRes = planningRepository.getPlanning(id)
                    planningRes.fold(
                        ifLeft = {},
                        ifRight = { p ->
                            planning = planning.copy(planning = p)
                        }
                    )
                    val users = mutableListOf<User>()
                    d.users.forEach { uid ->
                        val userRes = userRepository.getUserFromId(uid)
                        userRes.fold(
                            ifLeft = {},
                            ifRight = { user ->
                                users.add(user)
                            }
                        )
                    }
                    planning = planning.copy(users = users)
                    return Right(planning)
                }
            }
            return Left(SharedPlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(SharedPlanningFailure.SharedPlanningRetrievalFailed(res.getString(R.string.err_plannings_retrieval)))
        }
    }
}