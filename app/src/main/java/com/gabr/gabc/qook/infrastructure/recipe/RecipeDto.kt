package com.gabr.gabc.qook.infrastructure.recipe

import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.infrastructure.ingredient.IngredientDto
import com.gabr.gabc.qook.infrastructure.ingredient.toDomain
import com.gabr.gabc.qook.infrastructure.tag.TagDto
import com.gabr.gabc.qook.infrastructure.tag.toDomain
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class RecipeDto(
    @PropertyName("name") val name: String,
    @PropertyName("creationDate") val creationDate: Date,
    @PropertyName("updateDate") val updateDate: Date,
    @PropertyName("easiness") val easiness: String,
    @PropertyName("time") val time: RecipeTimeDto,
    @PropertyName("photo") val photo: String,
    @PropertyName("description") val description: String,
    @PropertyName("ingredients") val ingredients: List<IngredientDto>,
    @PropertyName("tags") val tags: List<TagDto>,
)

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        name,
        creationDate,
        updateDate,
        Easiness.valueOf(easiness),
        time.toDomain(),
        photo,
        description,
        ingredients.map { it.toDomain() },
        tags.map { it.toDomain() }
    )
}