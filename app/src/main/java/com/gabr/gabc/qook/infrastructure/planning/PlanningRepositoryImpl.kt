package com.gabr.gabc.qook.infrastructure.planning

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.PlanningFailure
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import com.gabr.gabc.qook.domain.planning.toDto
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.presentation.shared.Globals
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlanningRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val recipeRepository: RecipeRepository,
    private val res: StringResourcesProvider
) : PlanningRepository {
    private suspend fun getRecipesFrom(dayPlanningDto: DayPlanningDto): DayPlanning {
        var lunch = Recipe.EMPTY_RECIPE
        var dinner = Recipe.EMPTY_RECIPE

        if (dayPlanningDto.lunch.isNotEmpty()) {
            val lunchRes = recipeRepository.getRecipe(dayPlanningDto.lunch)
            lunchRes.fold(
                ifLeft = {},
                ifRight = { recipe -> lunch = recipe }
            )
        }

        if (dayPlanningDto.dinner.isNotEmpty()) {
            val dinnerRes = recipeRepository.getRecipe(dayPlanningDto.dinner)
            dinnerRes.fold(
                ifLeft = {},
                ifRight = { recipe -> dinner = recipe }
            )
        }

        return DayPlanning(dayPlanningDto.id, dayPlanningDto.dayIndex, lunch, dinner)
    }

    override suspend fun getPlanning(groupId: String?): Either<PlanningFailure, List<DayPlanning>> {
        try {
            auth.currentUser?.let {
                val planning = mutableListOf<DayPlanning>()
                val res = if (groupId == null) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_PLANNING)
                        .orderBy(Globals.OBJ_PLANNING_DAY_INDEX)
                        .get()
                        .await()
                } else {
                    db.collection(Globals.DB_GROUPS).document(groupId)
                        .collection(Globals.DB_PLANNING)
                        .orderBy(Globals.OBJ_PLANNING_DAY_INDEX)
                        .get()
                        .await()
                }

                res.forEach { doc ->
                    planning.add(getRecipesFrom(doc.toObject()))
                }

                return Right(planning)
            }
            return Left(PlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(PlanningFailure.PlanningRetrievalFailed(res.getString(R.string.err_plannings_retrieval)))
        }
    }

    override suspend fun updateRecipeFromPlanning(
        dayPlanning: DayPlanning,
        groupId: String?
    ): Either<PlanningFailure, Unit> {
        try {
            auth.currentUser?.let {
                if (groupId == null) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_PLANNING).document(dayPlanning.id)
                        .update(dayPlanning.toDto().toMap()).await()
                } else {
                    db.collection(Globals.DB_GROUPS).document(groupId)
                        .collection(Globals.DB_PLANNING).document(dayPlanning.id)
                        .update(dayPlanning.toDto().toMap()).await()
                }

                return Right(Unit)
            }
            return Left(PlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(PlanningFailure.PlanningCreationFailed(res.getString(R.string.err_planning_update)))
        }
    }

    override suspend fun resetPlanning(groupId: String?): Either<PlanningFailure, Unit> {
        try {
            auth.currentUser?.let {
                val planningCollection = if (groupId == null) {
                    db.collection(Globals.DB_USER).document(it.uid).collection(Globals.DB_PLANNING)
                } else {
                    db.collection(Globals.DB_GROUPS).document(groupId)
                        .collection(Globals.DB_PLANNING)
                }

                val batch = db.batch()

                batch.set(
                    planningCollection.document(Globals.OBJ_PLANNING_FIRST_DAY),
                    DayPlanning.EMPTY_DAY_PLANNING.copy(
                        dayIndex = 0,
                        id = Globals.OBJ_PLANNING_FIRST_DAY
                    ).toDto()
                )
                batch.set(
                    planningCollection.document(Globals.OBJ_PLANNING_SECOND_DAY),
                    DayPlanning.EMPTY_DAY_PLANNING.copy(
                        dayIndex = 1,
                        id = Globals.OBJ_PLANNING_SECOND_DAY
                    ).toDto()
                )
                batch.set(
                    planningCollection.document(Globals.OBJ_PLANNING_THIRD_DAY),
                    DayPlanning.EMPTY_DAY_PLANNING.copy(
                        dayIndex = 2,
                        id = Globals.OBJ_PLANNING_THIRD_DAY
                    ).toDto()
                )
                batch.set(
                    planningCollection.document(Globals.OBJ_PLANNING_FOURTH_DAY),
                    DayPlanning.EMPTY_DAY_PLANNING.copy(
                        dayIndex = 3,
                        id = Globals.OBJ_PLANNING_FOURTH_DAY
                    ).toDto()
                )
                batch.set(
                    planningCollection.document(Globals.OBJ_PLANNING_FIFTH_DAY),
                    DayPlanning.EMPTY_DAY_PLANNING.copy(
                        dayIndex = 4,
                        id = Globals.OBJ_PLANNING_FIFTH_DAY
                    ).toDto()
                )
                batch.set(
                    planningCollection.document(Globals.OBJ_PLANNING_SIXTH_DAY),
                    DayPlanning.EMPTY_DAY_PLANNING.copy(
                        dayIndex = 5,
                        id = Globals.OBJ_PLANNING_SIXTH_DAY
                    ).toDto()
                )
                batch.set(
                    planningCollection.document(Globals.OBJ_PLANNING_SEVENTH_DAY),
                    DayPlanning.EMPTY_DAY_PLANNING.copy(
                        dayIndex = 6,
                        id = Globals.OBJ_PLANNING_SEVENTH_DAY
                    ).toDto()
                )

                batch.commit().await()
                return Right(Unit)
            }
            return Left(PlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(PlanningFailure.PlanningCreationFailed(res.getString(R.string.err_planning_reset)))
        }
    }
}