package com.gabr.gabc.qook.infrastructure.planning

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.Planning
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

    override suspend fun getPlanning(): Either<PlanningFailure, Planning> {
        try {
            auth.currentUser?.let {
                val res = db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_PLANNING).document(Globals.DB_PLANNING)
                    .get().await()

                res.toObject<PlanningDto>()?.let { planningDto ->
                    val planning = Planning(
                        firstDay = getRecipesFrom(planningDto.firstDay),
                        secondDay = getRecipesFrom(planningDto.secondDay),
                        thirdDay = getRecipesFrom(planningDto.thirdDay),
                        fourthDay = getRecipesFrom(planningDto.fourthDay),
                        fifthDay = getRecipesFrom(planningDto.fifthDay),
                        sixthDay = getRecipesFrom(planningDto.sixthDay),
                        seventhDay = getRecipesFrom(planningDto.seventhDay),
                    )
                    return Right(planning)
                }
            }
            return Left(PlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(PlanningFailure.PlanningRetrievalFailed(res.getString(R.string.err_plannings_retrieval)))
        }
    }

    override suspend fun updateRecipeFromPlanning(dayPlanning: DayPlanning): Either<PlanningFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_PLANNING).document(Globals.DB_PLANNING)
                    .update(
                        mapOf(
                            Pair(dayPlanning.id, dayPlanning.toDto())
                        )
                    ).await()

                return Right(Unit)
            }
            return Left(PlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(PlanningFailure.PlanningCreationFailed(res.getString(R.string.err_planning_update)))
        }
    }

    override suspend fun resetPlanning(): Either<PlanningFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_PLANNING).document(Globals.DB_PLANNING)
                    .set(Planning.EMPTY_PLANNING.toDto()).await()

                return Right(Unit)
            }
            return Left(PlanningFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(PlanningFailure.PlanningCreationFailed(res.getString(R.string.err_planning_reset)))
        }
    }

}