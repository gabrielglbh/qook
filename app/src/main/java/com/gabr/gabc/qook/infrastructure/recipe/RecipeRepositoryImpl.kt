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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
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
    override suspend fun getRecipes(
        orderBy: String,
        ascending: Boolean,
        query: String?,
        tagId: String?
    ): Either<RecipeFailure, List<Recipe>> {
        try {
            auth.currentUser?.let {
                val collection = db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_RECIPES)

                if (tagId != null) {
                    collection.whereArrayContains(Globals.OBJ_RECIPE_TAG_IDS, tagId)
                }
                if (query != null && query.trim().isNotEmpty()) {
                    collection.whereArrayContains(Globals.OBJ_RECIPE_KEYWORDS, query.lowercase())
                }
                val snapshot = collection.orderBy(
                    orderBy, if (ascending) {
                        Query.Direction.ASCENDING
                    } else {
                        Query.Direction.DESCENDING
                    }
                ).get().await()

                val recipes = mutableListOf<Recipe>()
                snapshot.documents.forEach { doc ->
                    val result = getRecipe(doc.id)
                    result.fold(
                        ifLeft = {},
                        ifRight = { recipe -> recipes.add(recipe) }
                    )
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

    override suspend fun updateRecipe(recipe: Recipe, id: String?): Either<RecipeFailure, Recipe> {
        try {
            auth.currentUser?.let {
                val docCollection = db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_RECIPES)
                val docRef = if (id == null) {
                    docCollection.document()
                } else {
                    docCollection.document(id)
                }

                val recipeId = docRef.path.split("/").last()

                docRef.set(recipe.toDto()).await()

                if (recipe.photo.host != Globals.FIREBASE_HOST && recipe.photo != Uri.EMPTY) {
                    storage.uploadImage(recipe.photo, "${Globals.STORAGE_RECIPES}$recipeId.jpg")
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

    override suspend fun removeRecipe(id: String): Either<RecipeFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_RECIPES).document(id)
                    .delete().await()

                try {
                    storage.deleteImage("${Globals.STORAGE_RECIPES}$id.jpg")
                } catch (_: StorageException) {
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

    override suspend fun getSearchedRecipes(filters: Map<String, String>): List<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecipe(id: String): Either<RecipeFailure, Recipe> {
        try {
            auth.currentUser?.let {
                val query = db.collection(Globals.DB_USER).document(it.uid)
                    .collection(Globals.DB_RECIPES).document(id).get().await()
                query.toObject<RecipeDto>()?.let { recipeDto ->
                    var recipe = recipeDto.toDomain()

                    try {
                        val res = storage.getDownloadUrl("${Globals.STORAGE_RECIPES}${id}.jpg")
                        res.fold(
                            ifLeft = {},
                            ifRight = { uri -> recipe = recipe.copy(photo = uri) }
                        )
                    } catch (_: StorageException) {
                    }

                    val tagsRes = tagRepository.getTags(id)
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