package com.gabr.gabc.qook.infrastructure.ingredient

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.ingredients.Ingredients
import com.gabr.gabc.qook.domain.ingredients.IngredientsFailure
import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import com.gabr.gabc.qook.domain.ingredients.toDto
import com.gabr.gabc.qook.presentation.shared.Globals
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IngredientRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val res: StringResourcesProvider,
) : IngredientsRepository {
    override suspend fun getIngredientsOfShoppingList(): Either<IngredientsFailure, Ingredients> {
        try {
            auth.currentUser?.let {
                val result = db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                    .get()
                    .await()
                result.toObject<IngredientsDto>()?.let { ingredientsDto ->
                    return Right(ingredientsDto.toDomain())
                }
                return Left(IngredientsFailure.IngredientsRetrievalFailed(res.getString(R.string.err_ingredients_retrieval)))
            }
            return Left(IngredientsFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(IngredientsFailure.IngredientsRetrievalFailed(res.getString(R.string.err_ingredients_retrieval)))
        }
    }

    override suspend fun removeIngredient(ingredients: Ingredients): Either<IngredientsFailure, Unit> {
        try {
            auth.currentUser?.let {
                val ingredientsMapped = mutableMapOf<String, Boolean>()
                ingredients.list.forEach { (key, _) ->
                    ingredientsMapped["${Globals.OBJ_SHOPPING_LIST}.$key"] to FieldValue.delete()
                }

                db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                    .update(ingredientsMapped.toMap()).await()

                return Right(Unit)
            }
            return Left(IngredientsFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                IngredientsFailure.IngredientsDoesNotExist(res.getString(R.string.err_ingredients_delete))
            )
        }
    }

    override suspend fun updateIngredient(ingredients: Ingredients): Either<IngredientsFailure, Unit> {
        try {
            auth.currentUser?.let {
                val ingredientsMapped = mutableMapOf<String, Boolean>()
                ingredients.list.forEach { (key, value) ->
                    ingredientsMapped["${Globals.OBJ_SHOPPING_LIST}.$key"] = value
                }

                db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                    .update(ingredientsMapped.toMap()).await()

                return Right(Unit)
            }
            return Left(IngredientsFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(IngredientsFailure.IngredientsUpdateFailed(res.getString(R.string.err_ingredients_update)))
        }
    }

    override suspend fun resetIngredients(): Either<IngredientsFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                    .set(Ingredients(mapOf()).toDto()).await()

                return Right(Unit)
            }
            return Left(IngredientsFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(IngredientsFailure.IngredientsUpdateFailed(res.getString(R.string.err_ingredients_update)))
        }
    }

}