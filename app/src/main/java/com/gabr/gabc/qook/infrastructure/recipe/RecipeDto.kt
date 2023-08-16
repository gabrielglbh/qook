package com.gabr.gabc.qook.infrastructure.recipe

import android.net.Uri
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.infrastructure.tag.TagDto
import com.gabr.gabc.qook.infrastructure.tag.toDomain
import com.google.firebase.firestore.PropertyName
import java.time.LocalDate

data class RecipeDto constructor(
    @PropertyName("name") val name: String = "",
    @PropertyName("creationDate") val creationDate: LocalDate = LocalDate.now(),
    @PropertyName("updateDate") val updateDate: LocalDate = LocalDate.now(),
    @PropertyName("easiness") val easiness: String = "",
    @PropertyName("time") val time: String = "",
    @PropertyName("photo") val photo: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("ingredients") val ingredients: List<String> = listOf(),
    @PropertyName("tags") val tags: List<TagDto> = listOf(),
)

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        name,
        creationDate,
        updateDate,
        Easiness.valueOf(easiness),
        time,
        Uri.parse(photo),
        description,
        ingredients,
        tags.map { it.toDomain() }
    )
}