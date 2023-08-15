package com.gabr.gabc.qook.infrastructure.tag

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.domain.tag.TagFailure
import com.gabr.gabc.qook.domain.tag.TagRepository
import com.gabr.gabc.qook.domain.tag.toDto
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val res: StringResourcesProvider
) : TagRepository {
    override suspend fun createTag(tag: Tag): Either<TagFailure, Tag> {
        try {
            auth.currentUser?.let {
                val docRef = db.collection("USERS").document(it.uid)
                    .collection("TAGS").add(tag.toDto()).await()
                val id = docRef.path.split("/").last()
                return Right(tag.copy(id = id))
            }
            return Left(TagFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                TagFailure.TagDoesNotExist(
                    "${err.code}: " +
                            res.getString(R.string.err_tags_creation_failed)
                )
            )
        }
    }

    override suspend fun removeTag(id: String): Either<TagFailure, Unit> {
        try {
            auth.currentUser?.let {
                db.collection("USERS").document(it.uid)
                    .collection("TAGS").document(id)
                    .delete().await()
                return Right(Unit)
            }
            return Left(TagFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                TagFailure.TagDoesNotExist(
                    "${err.code}: " +
                            res.getString(R.string.err_tags_deletion_failed)
                )
            )
        }
    }

    override suspend fun updateTag(tag: Tag): Either<TagFailure, Unit> {
        auth.currentUser?.let {
            db.collection("USERS").document(it.uid)
                .collection("TAGS").document(tag.id)
                .update(tag.toDto().toMap()).await()
            return Right(Unit)
        }
        return Left(TagFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
    }

    override suspend fun getTags(): Either<TagFailure, List<Tag>> {
        try {
            auth.currentUser?.let {
                val ref = db
                    .collection("USERS").document(it.uid)
                    .collection("TAGS").get().await()
                return Right(ref.toObjects<TagDto>().map { dto ->
                    dto.toDomain()
                })
            }
            return Left(TagFailure.NotAuthenticated(res.getString(R.string.error_user_not_auth)))
        } catch (err: FirebaseFirestoreException) {
            return Left(
                TagFailure.TagDoesNotExist(
                    "${err.code}: " + res.getString(R.string.err_tags_retrieve)
                )
            )
        }
    }

    override suspend fun getTags(recipeId: String): Either<TagFailure, List<Tag>> {
        TODO("Not yet implemented")
    }
}