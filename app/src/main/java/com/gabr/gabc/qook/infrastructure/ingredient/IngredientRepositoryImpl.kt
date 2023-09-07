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
    override suspend fun getIngredientsOfShoppingList(groupId: String?): Either<IngredientsFailure, Ingredients> {
        try {
            auth.currentUser?.let {
                val result = if (groupId == null) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .get()
                        .await()
                } else {
                    db.collection(Globals.DB_GROUPS).document(groupId)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .get()
                        .await()
                }
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

    override suspend fun removeIngredient(
        ingredient: Pair<String, Boolean>,
        groupId: String?
    ): Either<IngredientsFailure, Unit> {
        try {
            auth.currentUser?.let {
                val map = mapOf(
                    "${Globals.OBJ_SHOPPING_LIST}.${ingredient.first}" to FieldValue.delete()
                )

                if (groupId == null) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .update(map).await()
                } else {
                    db.collection(Globals.DB_GROUPS).document(groupId)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .update(map).await()
                }

                return Right(Unit)
            }
            return Left(IngredientsFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                IngredientsFailure.IngredientsDoesNotExist(res.getString(R.string.err_ingredients_delete))
            )
        }
    }

    override suspend fun removeIngredients(
        ingredients: Ingredients,
        groupId: String?
    ): Either<IngredientsFailure, Unit> {
        try {
            auth.currentUser?.let {
                val listMapped = mutableListOf<Pair<String, FieldValue>>()
                ingredients.list.forEach { (key, _) ->
                    listMapped.add("${Globals.OBJ_SHOPPING_LIST}.$key" to FieldValue.delete())
                }

                if (groupId == null) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .update(listMapped.toMap()).await()
                } else {
                    db.collection(Globals.DB_GROUPS).document(groupId)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .update(listMapped.toMap()).await()
                }

                return Right(Unit)
            }
            return Left(IngredientsFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                IngredientsFailure.IngredientsDoesNotExist(res.getString(R.string.err_ingredients_delete))
            )
        }
    }

    override suspend fun updateIngredient(
        ingredient: Pair<String, Boolean>,
        groupId: String?
    ): Either<IngredientsFailure, Unit> {
        try {
            auth.currentUser?.let {
                val map = mapOf(
                    Pair(
                        "${Globals.OBJ_SHOPPING_LIST}.${ingredient.first}",
                        ingredient.second
                    )
                )
                if (groupId == null) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .update(map).await()
                } else {
                    db.collection(Globals.DB_GROUPS).document(groupId)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .update(map).await()
                }

                return Right(Unit)
            }
            return Left(IngredientsFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(IngredientsFailure.IngredientsUpdateFailed(res.getString(R.string.err_ingredients_update)))
        }
    }

    override suspend fun updateIngredients(
        ingredients: Ingredients,
        groupId: String?
    ): Either<IngredientsFailure, Unit> {
        try {
            auth.currentUser?.let {
                val ingredientsMapped = mutableMapOf<String, Boolean>()
                ingredients.list.forEach { (key, value) ->
                    ingredientsMapped["${Globals.OBJ_SHOPPING_LIST}.$key"] = value
                }

                if (groupId == null) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .update(ingredientsMapped.toMap()).await()
                } else {
                    db.collection(Globals.DB_GROUPS).document(groupId)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .update(ingredientsMapped.toMap()).await()
                }

                return Right(Unit)
            }
            return Left(IngredientsFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(IngredientsFailure.IngredientsUpdateFailed(res.getString(R.string.err_ingredients_update)))
        }
    }

    override suspend fun resetIngredients(groupId: String?): Either<IngredientsFailure, Unit> {
        try {
            auth.currentUser?.let {
                if (groupId == null) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .set(Ingredients(mapOf()).toDto()).await()
                } else {
                    db.collection(Globals.DB_GROUPS).document(groupId)
                        .collection(Globals.DB_SHOPPING_LIST).document(Globals.DB_INGREDIENTS)
                        .set(Ingredients(mapOf()).toDto()).await()
                }

                return Right(Unit)
            }
            return Left(IngredientsFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(IngredientsFailure.IngredientsUpdateFailed(res.getString(R.string.err_ingredients_update)))
        }
    }
}