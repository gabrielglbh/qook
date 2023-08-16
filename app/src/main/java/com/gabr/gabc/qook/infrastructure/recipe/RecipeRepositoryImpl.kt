package com.gabr.gabc.qook.infrastructure.recipe

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.recipe.RecipeFailure
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.domain.recipe.toDto
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.domain.tag.toDto
import com.gabr.gabc.qook.infrastructure.tag.toMap
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val res: StringResourcesProvider
) : RecipeRepository {
    override suspend fun getRecipes(): Either<RecipeFailure, List<Recipe>> {
        // TODO: Needs Firebase Storage Service to GET images with downloadUrl and path
        try {
            auth.currentUser?.let {
                val query = db.collection("USERS").document(it.uid)
                    .collection("RECIPES").get().await()
                return Right(query.toObjects())
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
                val writeBatch = db.batch()
                val docRef = db.collection("USERS").document(it.uid)
                    .collection("RECIPES").document()
                val recipeId = docRef.path.split("/").last()

                writeBatch.set(docRef, recipe.toDto())
                recipe.tags.forEach { tag ->
                    writeBatch.set(
                        db.collection("USERS").document(it.uid)
                            .collection("RECIPES").document(recipeId)
                            .collection("TAGS").document(tag.id), tag.toDto()
                    )
                }
                writeBatch.commit().await()

                val file = recipe.photo
                val imageRef = storage.reference.child("${it.uid}/recipes/$recipeId.jpg")
                val uploadTask = imageRef.putFile(file).await()

                if (uploadTask.error != null) {
                    return Left(RecipeFailure.RecipeCreationFailed(res.getString(R.string.error_avatar_update)))
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
        }
    }

    override suspend fun removeRecipe(id: String): Either<RecipeFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection("USERS").document(it.uid)
                    .collection("RECIPES").document(id)
                    .delete().await()

                storage.reference.child("${it.uid}/recipes/$id.jpg")
                    .delete().await()

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
            val tagDocRefs = mutableListOf<DocumentReference>()
            recipe.tags.forEach { tag ->
                tagDocRefs.add(
                    docRef.collection("TAGS").document(tag.id)
                )
            }

            db.runTransaction { transaction ->
                val recipeSnapshot = transaction.get(docRef)
                val originRecipe = recipeSnapshot.toObject<Recipe>()
                if (originRecipe != recipe) transaction.update(docRef, recipe.toDto().toMap())

                tagDocRefs.forEachIndexed { x, ref ->
                    val tagSnapshot = transaction.get(ref)
                    val originTag = tagSnapshot.toObject<Tag>()
                    if (originTag != recipe.tags[x]) transaction.update(
                        ref,
                        recipe.tags[x].toDto().toMap()
                    )
                }
            }.addOnCanceledListener {
                result =
                    Left(RecipeFailure.RecipeUpdateFailed(res.getString(R.string.error_recipes_update)))
            }.addOnFailureListener {
                result =
                    Left(RecipeFailure.RecipeUpdateFailed(res.getString(R.string.error_recipes_update)))
            }.addOnSuccessListener {
                result = Right(Unit)
            }.await()

            val file = recipe.photo
            val imageRef = storage.reference.child("${it.uid}/recipes/${recipe.id}.jpg")
            val uploadTask = imageRef.putFile(file).await()

            if (uploadTask.error != null) {
                result =
                    Left(RecipeFailure.RecipeCreationFailed(res.getString(R.string.error_avatar_update)))
            }

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
                return Right(query.toObject()!!)
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