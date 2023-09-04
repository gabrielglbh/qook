package com.gabr.gabc.qook.infrastructure.sharedPlanning

import arrow.core.Either
import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningFailure
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningRepository
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class SharedPlanningRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val recipeRepository: RecipeRepository,
    private val ingredientsRepository: IngredientsRepository,
    private val res: StringResourcesProvider
) : SharedPlanningRepository {
    override suspend fun createSharedPlanning(): Either<SharedPlanningFailure, SharedPlanning> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSharedPlanning(id: String): Either<SharedPlanningFailure, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSharedPlanning(id: String): Either<SharedPlanningFailure, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getSharedPlannings(): Either<SharedPlanningFailure, List<SharedPlanning>> {
        TODO("Not yet implemented")
    }
}