package com.gabr.gabc.qook.domain.tag

sealed class TagFailure(open val error: String) {
    data class TagDoesNotExist(override val error: String) : TagFailure(error)
    data class TagCreationFailed(override val error: String) : TagFailure(error)
    data class TagsRetrievalFailed(override val error: String) : TagFailure(error)
    data class NotAuthenticated(override val error: String) : TagFailure(error)
}
