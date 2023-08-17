package com.gabr.gabc.qook.domain.storage

sealed class StorageFailure(open val error: String) {
    data class ImageDoesNotExist(override val error: String) : StorageFailure(error)
    data class ImageUpdateFailed(override val error: String) : StorageFailure(error)
    data class ImagesRetrievalFailed(override val error: String) : StorageFailure(error)
    data class NotAuthenticated(override val error: String) : StorageFailure(error)
}
