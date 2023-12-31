package com.gabr.gabc.qook.infrastructure.storage

import android.net.Uri
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.storage.StorageFailure
import com.gabr.gabc.qook.domain.storage.StorageRepository
import com.gabr.gabc.qook.presentation.shared.providers.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val res: StringResourcesProvider
) : StorageRepository {
    override suspend fun uploadImage(file: Uri, path: String): Either<StorageFailure, Uri> {
        try {
            auth.currentUser?.let {
                val ref = storage.reference.child(path)
                ref.putFile(file).await()
                return Right(ref.downloadUrl.await())
            }
            return Left(StorageFailure.NotAuthenticated(res.getString(R.string.err_storage_upload_image)))
        } catch (err: Exception) {
            return Left(StorageFailure.ImageUpdateFailed(res.getString(R.string.err_storage_upload_image)))
        }
    }

    override suspend fun getDownloadUrl(path: String): Either<StorageFailure, Uri> {
        try {
            auth.currentUser?.let {
                return Right(storage.reference.child(path).downloadUrl.await())
            }
            return Left(StorageFailure.NotAuthenticated(res.getString(R.string.err_storage_retrieval)))
        } catch (err: StorageException) {
            return Left(StorageFailure.ImagesRetrievalFailed(res.getString(R.string.err_storage_retrieval)))
        }
    }

    override suspend fun deleteImage(path: String): Either<StorageFailure, Unit> {
        try {
            auth.currentUser?.let {
                storage.reference.child(path).delete().await()
                return Right(Unit)
            }
            return Left(StorageFailure.NotAuthenticated(res.getString(R.string.err_storage_delete)))
        } catch (err: StorageException) {
            return Left(StorageFailure.ImageDoesNotExist(res.getString(R.string.err_storage_delete)))
        }
    }
}