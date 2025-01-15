package com.gabr.gabc.qook.domain.storage

import android.net.Uri
import arrow.core.Either

interface StorageRepository {
    suspend fun uploadImage(file: Uri, path: String): Either<StorageFailure, Uri>
    suspend fun getDownloadUrl(path: String): Either<StorageFailure, Uri>
    suspend fun deleteImage(path: String): Either<StorageFailure, Unit>
}