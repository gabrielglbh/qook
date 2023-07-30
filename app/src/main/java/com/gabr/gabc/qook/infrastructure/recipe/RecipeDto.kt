package com.gabr.gabc.qook.infrastructure.recipe

import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.infrastructure.IngredientDto
import com.gabr.gabc.qook.infrastructure.TagDto
import com.gabr.gabc.qook.infrastructure.toDomain
import java.util.Date

data class RecipeDto(
    val name: String,
    val creationDate: Date,
    val updateDate: Date,
    val easiness: String,
    val time: RecipeTimeDto,
    val photo: String,
    val description: String,
    val ingredients: List<IngredientDto>,
    val tags: List<TagDto>,
)

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        name,
        creationDate,
        updateDate,
        Easiness.valueOf(easiness.uppercase()),
        time.toDomain(),
        photo, description,
        ingredients.map { it.toDomain() },
        tags.map { it.toDomain() }
    )
}