package com.gabr.gabc.qook.domain.recipe

import com.gabr.gabc.qook.domain.Ingredient
import com.gabr.gabc.qook.domain.Tag
import java.util.Date

data class Recipe(
    val name: String,
    val creationDate: Date,
    val updateDate: Date,
    val easiness: Easiness,
    val time: RecipeTime,
    val photo: String,
    val description: String,
    val ingredients: List<Ingredient>,
    val tags: List<Tag>,
    )