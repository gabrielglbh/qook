package com.gabr.gabc.qook.infrastructure.recipe

import android.net.Uri
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.shared.Globals
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class RecipeDto constructor(
    @DocumentId val id: String = "",
    @PropertyName(Globals.OBJ_RECIPE_NAME) val name: String = "",
    @PropertyName(Globals.OBJ_RECIPE_CREATION) val creationDate: Long = 0L,
    @PropertyName(Globals.OBJ_RECIPE_UPDATE) val updateDate: Long = 0L,
    @PropertyName(Globals.OBJ_RECIPE_EASINESS) val easiness: String = "",
    @PropertyName("time") val time: String = "",
    @PropertyName(Globals.OBJ_RECIPE_DESCRIPTION) val description: String = "",
    @PropertyName(Globals.OBJ_RECIPE_INGREDIENTS) val ingredients: List<String> = listOf(),
    @PropertyName(Globals.OBJ_RECIPE_TAG_IDS) val tagIds: List<String> = listOf(),
)

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id,
        name,
        Date(creationDate),
        Date(updateDate),
        Easiness.valueOf(easiness),
        time,
        Uri.EMPTY,
        description,
        ingredients,
        listOf()
    )
}

fun RecipeDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair(Globals.OBJ_RECIPE_NAME, name),
        Pair(Globals.OBJ_RECIPE_CREATION, creationDate),
        Pair(Globals.OBJ_RECIPE_UPDATE, updateDate),
        Pair(Globals.OBJ_RECIPE_EASINESS, easiness),
        Pair("time", time),
        Pair(Globals.OBJ_RECIPE_DESCRIPTION, description),
        Pair(Globals.OBJ_RECIPE_INGREDIENTS, ingredients),
        Pair(Globals.OBJ_RECIPE_TAG_IDS, tagIds),
    )
}