package com.gabr.gabc.qook.domain.ingredients

import android.os.Parcelable
import com.gabr.gabc.qook.infrastructure.ingredient.IngredientsDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredients(
    val list: Map<String, Boolean>
) : Parcelable

fun Ingredients.toDto(): IngredientsDto {
    return IngredientsDto(list)
}