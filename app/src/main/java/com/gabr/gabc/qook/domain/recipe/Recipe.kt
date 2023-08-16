package com.gabr.gabc.qook.domain.recipe

import android.net.Uri
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.domain.tag.toDto
import com.gabr.gabc.qook.infrastructure.recipe.RecipeDto
import java.time.LocalDate

data class Recipe(
    val name: String,
    val creationDate: LocalDate,
    val updateDate: LocalDate,
    val easiness: Easiness,
    val time: String,
    val photo: Uri,
    val description: String,
    val ingredients: List<String>,
    val tags: List<Tag>
) {
    companion object {
        val EMPTY_RECIPE = Recipe(
            name = "",
            creationDate = LocalDate.now(),
            updateDate = LocalDate.now(),
            easiness = Easiness.EASY,
            time = "",
            photo = Uri.EMPTY,
            description = "",
            ingredients = mutableListOf(),
            tags = mutableListOf()
        )
    }
}

fun Recipe.toDto(): RecipeDto {
    return RecipeDto(
        name,
        creationDate,
        updateDate,
        easiness.name,
        time,
        photo.toString(),
        description,
        ingredients,
        tags.map { it.toDto() },
    )
}