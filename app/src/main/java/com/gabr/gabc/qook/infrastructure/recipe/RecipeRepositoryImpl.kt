package com.gabr.gabc.qook.infrastructure.recipe

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
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: StorageRepository,
    private val tagRepository: TagRepository,
    private val res: StringResourcesProvider
) : RecipeRepository {
    override suspend fun getRecipes(): Either<RecipeFailure, List<Recipe>> {
        try {
            auth.currentUser?.let {
                val query = db.collection("USERS").document(it.uid)
                    .collection("RECIPES").get().await()
                val recipes = mutableListOf<Recipe>()
                query.documents.forEach { doc ->
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

    override suspend fun createRecipe(recipe: Recipe): Either<RecipeFailure, Recipe> {
        try {
            auth.currentUser?.let {
                val docRef = db.collection("USERS").document(it.uid)
                    .collection("RECIPES").document()
                val recipeId = docRef.path.split("/").last()

                docRef.set(recipe.toDto()).await()

                storage.uploadImage(recipe.photo, "recipes/$recipeId.jpg")

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
        }
    }

    override suspend fun removeRecipe(id: String): Either<RecipeFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection("USERS").document(it.uid)
                    .collection("RECIPES").document(id)
                    .delete().await()

                storage.deleteImage("recipes/$id.jpg")

                return Right(Unit)
            }
            return Left(RecipeFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                RecipeFailure.RecipeDoesNotExist(
                    "${err.code}: " +
                            res.getString(R.string.err_tags_deletion_failed)
                )
            )
        }
    }

    override suspend fun updateRecipe(recipe: Recipe): Either<RecipeFailure, Unit> {
        auth.currentUser?.let {
            lateinit var result: Either<RecipeFailure, Unit>
            val docRef = db.collection("USERS").document(it.uid)
                .collection("RECIPES").document(recipe.id)

            db.runTransaction { transaction ->
                val recipeSnapshot = transaction.get(docRef)
                val originRecipe = recipeSnapshot.toObject<RecipeDto>()
                if (originRecipe != recipe.toDto()) transaction.update(
                    docRef,
                    recipe.toDto().toMap()
                )
            }.addOnCanceledListener {
                result =
                    Left(RecipeFailure.RecipeUpdateFailed(res.getString(R.string.err_recipes_update)))
            }.addOnFailureListener {
                result =
                    Left(RecipeFailure.RecipeUpdateFailed(res.getString(R.string.err_recipes_update)))
            }.addOnSuccessListener {
                result = Right(Unit)
            }.await()

            storage.uploadImage(recipe.photo, "recipes/${recipe.id}.jpg")

            return result
        }
        return Left(RecipeFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
    }

    override suspend fun getSearchedRecipes(filters: Map<String, String>): List<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecipe(id: String): Either<RecipeFailure, Recipe> {
        try {
            auth.currentUser?.let {
                val query = db.collection("USERS").document(it.uid)
                    .collection("RECIPES").document(id).get().await()
                query.toObject<RecipeDto>()?.let { recipeDto ->
                    var recipe = recipeDto.toDomain()
                    val res = storage.getDownloadUrl("recipes/${id}.jpg")
                    res.fold(
                        ifLeft = {},
                        ifRight = { uri -> recipe = recipe.copy(photo = uri) }
                    )
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