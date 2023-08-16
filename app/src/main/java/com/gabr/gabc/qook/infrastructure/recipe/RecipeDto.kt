package com.gabr.gabc.qook.infrastructure.recipe

import android.net.Uri
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class RecipeDto constructor(
    @DocumentId val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("creationDate") val creationDate: Long = 0L,
    @PropertyName("updateDate") val updateDate: Long = 0L,
    @PropertyName("easiness") val easiness: String = "",
    @PropertyName("time") val time: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("ingredients") val ingredients: List<String> = listOf(),
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
        Pair("name", name),
        Pair("creationDate", creationDate),
        Pair("updateDate", updateDate),
        Pair("easiness", easiness),
        Pair("time", time),
        Pair("description", description),
        Pair("ingredients", ingredients),
    )
}