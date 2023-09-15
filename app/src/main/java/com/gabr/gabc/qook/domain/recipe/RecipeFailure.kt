package com.gabr.gabc.qook.domain.recipe

sealed class RecipeFailure(open val error: String) {
    data class RecipeCreationFailed(override val error: String) : RecipeFailure(error)
    data class RecipeRetrievalFailed(override val error: String) : RecipeFailure(error)
    data class RecipeDoesNotExist(override val error: String) : RecipeFailure(error)
    data class RecipeDuplicated(override val error: String) : RecipeFailure(error)
    data class NotAuthenticated(override val error: String) : RecipeFailure(error)
}
