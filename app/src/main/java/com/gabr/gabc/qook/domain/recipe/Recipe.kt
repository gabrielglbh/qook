package com.gabr.gabc.qook.domain.recipe

import android.net.Uri
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.infrastructure.recipe.RecipeDto
import java.util.Calendar
import java.util.Date

data class Recipe(
    val id: String,
    val name: String,
    val creationDate: Date,
    val updateDate: Date,
    val easiness: Easiness,
    val time: String,
    val photo: Uri,
    val description: String,
    val ingredients: List<String>,
    val tags: List<Tag>
) {
    companion object {
        val EMPTY_RECIPE = Recipe(
            id = "",
            name = "",
            creationDate = Calendar.getInstance().time,
            updateDate = Calendar.getInstance().time,
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
        id,
        name,
        creationDate.time,
        updateDate.time,
        easiness.name,
        time,
        description,
        ingredients
    )
}