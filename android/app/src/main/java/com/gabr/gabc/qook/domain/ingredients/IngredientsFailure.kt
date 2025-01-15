package com.gabr.gabc.qook.domain.ingredients

sealed class IngredientsFailure(open val error: String) {
    data class IngredientsUpdateFailed(override val error: String) : IngredientsFailure(error)
    data class IngredientsRetrievalFailed(override val error: String) : IngredientsFailure(error)
    data class IngredientsDoesNotExist(override val error: String) : IngredientsFailure(error)
    data class NotAuthenticated(override val error: String) : IngredientsFailure(error)
}
