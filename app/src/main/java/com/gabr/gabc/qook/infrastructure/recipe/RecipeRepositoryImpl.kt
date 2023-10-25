package com.gabr.gabc.qook.infrastructure.recipe

import android.net.Uri
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.recipe.RecipeFailure
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.domain.recipe.toDto
import com.gabr.gabc.qook.domain.storage.StorageRepository
import com.gabr.gabc.qook.domain.tag.TagRepository
import com.gabr.gabc.qook.presentation.shared.Globals
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: StorageRepository,
    private val tagRepository: TagRepository,
    private val res: StringResourcesProvider
) : RecipeRepository {
    override suspend fun createRecipe(
        recipe: Recipe,
        fromSharedPlanning: Boolean
    ): Either<RecipeFailure, Recipe> {
        try {
            auth.currentUser?.let {
                val check = db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_RECIPES)
                    .whereEqualTo(Globals.OBJ_RECIPE_NAME, recipe.name)
                    .limit(1).get().await()
                if (!check.isEmpty) return Left(RecipeFailure.RecipeDuplicated(res.getString(R.string.err_recipes_dup)))

                val docRef = db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_RECIPES).document()

                val recipeId = docRef.path.split("/").last()

                var recipeDto = recipe.toDto()
                if (fromSharedPlanning) {
                    recipeDto = recipeDto.copy(tagIds = listOf())
                }
                docRef.set(recipeDto).await()

                if (recipe.photo.host != Globals.FIREBASE_HOST && recipe.photo != Uri.EMPTY) {
                    storage.uploadImage(
                        recipe.photo,
                        "${Globals.STORAGE_USERS}${it.uid}/${Globals.STORAGE_RECIPES}$recipeId.jpg"
                    )
                }

                return Right(recipe)
            }
            return Left(RecipeFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                RecipeFailure.RecipeCreationFailed(
                    "${err.code}: " +
                            res.getString(R.string.err_recipe_creation)
                )
            )
        } catch (err: StorageException) {
            return Left(
                RecipeFailure.RecipeDoesNotExist(res.getString(R.string.err_recipe_creation))
            )
        }
    }

    override suspend fun getRecipes(
        orderBy: String,
        query: String?,
        tagId: String?,
        lastRecipeId: String,
    ): Either<RecipeFailure, List<Recipe>> {
        try {
            auth.currentUser?.let {
                var lastDocSnapshot: DocumentSnapshot? = null
                if (lastRecipeId.isNotEmpty()) {
                    lastDocSnapshot = db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_RECIPES).document(lastRecipeId).get().await()
                }

                var queryDb = if (tagId != null) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_RECIPES)
                        .whereArrayContains(Globals.OBJ_RECIPE_TAG_IDS, tagId)
                        .orderBy(orderBy, Query.Direction.DESCENDING)
                } else if (query != null && query.trim().isNotEmpty()) {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_RECIPES)
                        .whereArrayContains(Globals.OBJ_RECIPE_KEYWORDS, query.lowercase())
                        .orderBy(orderBy, Query.Direction.DESCENDING)
                } else {
                    db.collection(Globals.DB_USER).document(it.uid)
                        .collection(Globals.DB_RECIPES)
                        .orderBy(orderBy, Query.Direction.DESCENDING)
                }

                if (lastDocSnapshot != null) {
                    queryDb = queryDb.startAfter(lastDocSnapshot)
                }

                val querySnapshot = queryDb.limit(Globals.RECIPES_LIMIT).get().await()

                val recipes = mutableListOf<Recipe>()
                querySnapshot.documents.forEach { doc ->
                    doc.toObject<RecipeDto>()?.let { recipeDto ->
                        var recipe = recipeDto.toDomain()
                        if (recipeDto.hasPhoto) {
                            val res =
                                storage.getDownloadUrl("${Globals.STORAGE_USERS}${it.uid}/${Globals.STORAGE_RECIPES}${recipeDto.id}.jpg")
                            res.fold(
                                ifLeft = {},
                                ifRight = { uri -> recipe = recipe.copy(photo = uri) }
                            )
                        }

                        val tagsRes = tagRepository.getTags(recipeDto, it.uid)
                        tagsRes.fold(
                            ifLeft = {},
                            ifRight = { tags -> recipe = recipe.copy(tags = tags) }
                        )
                        recipes.add(recipe)
                    }
                }
                return Right(recipes)
            }
            return Left(RecipeFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                RecipeFailure.RecipeRetrievalFailed(
                    "${err.code}: " +
                            res.getString(R.string.err_recipes_retrieve)
                )
            )
        }
    }

    override suspend fun updateRecipe(recipe: Recipe): Either<RecipeFailure, Recipe> {
        try {
            auth.currentUser?.let {
                val docRef = db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_RECIPES).document(recipe.id)

                val recipeId = docRef.path.split("/").last()

                docRef.set(recipe.toDto()).await()

                val recipePhotoPath =
                    "${Globals.STORAGE_USERS}${it.uid}/${Globals.STORAGE_RECIPES}$recipeId.jpg"
                if (recipe.photo.host != Globals.FIREBASE_HOST && recipe.photo != Uri.EMPTY) {
                    storage.uploadImage(recipe.photo, recipePhotoPath)
                }
                if (recipe.photo == Uri.EMPTY) {
                    storage.deleteImage(recipePhotoPath)
                }

                return Right(recipe)
            }
            return Left(RecipeFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                RecipeFailure.RecipeCreationFailed(
                    "${err.code}: " +
                            res.getString(R.string.err_recipes_update)
                )
            )
        } catch (err: StorageException) {
            return Left(
                RecipeFailure.RecipeDoesNotExist(res.getString(R.string.err_recipes_update))
            )
        }
    }

    override suspend fun removeRecipe(recipe: Recipe): Either<RecipeFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_RECIPES).document(recipe.id)
                    .delete().await()

                if (recipe.photo != Uri.EMPTY) {
                    storage.deleteImage("${Globals.STORAGE_USERS}${it.uid}/${Globals.STORAGE_RECIPES}${recipe.id}.jpg")
                }

                return Right(Unit)
            }
            return Left(RecipeFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                RecipeFailure.RecipeDoesNotExist(
                    "${err.code}: " +
                            res.getString(R.string.err_recipe_deletion_failed)
                )
            )
        }
    }

    override suspend fun getRecipe(
        recipeId: String,
        userId: String
    ): Either<RecipeFailure, Recipe> {
        try {
            auth.currentUser?.let {
                val query = db.collection(Globals.DB_USER).document(userId)
                    .collection(Globals.DB_RECIPES).document(recipeId).get().await()
                query.toObject<RecipeDto>()?.let { recipeDto ->
                    var recipe = recipeDto.toDomain()

                    if (recipeDto.hasPhoto) {
                        val res =
                            storage.getDownloadUrl("${Globals.STORAGE_USERS}${userId}/${Globals.STORAGE_RECIPES}$recipeId.jpg")
                        res.fold(
                            ifLeft = {},
                            ifRight = { uri -> recipe = recipe.copy(photo = uri) }
                        )
                    }

                    val tagsRes = tagRepository.getTags(recipeDto, userId)
                    tagsRes.fold(
                        ifLeft = {},
                        ifRight = { tags -> recipe = recipe.copy(tags = tags) }
                    )
                    return Right(recipe)
                }
            }
            return Left(RecipeFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                RecipeFailure.RecipeRetrievalFailed(
                    "${err.code}: " +
                            res.getString(R.string.err_recipes_retrieve)
                )
            )
        }
    }
}