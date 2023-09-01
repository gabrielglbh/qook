package com.gabr.gabc.qook.domain.recipe

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.infrastructure.recipe.RecipeDto
import com.gabr.gabc.qook.presentation.shared.StringFormatters
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.parcelableCreator
import java.util.Calendar
import java.util.Date

@Parcelize
data class Recipe(
    val id: String,
    val name: String,
    val creationDate: Date,
    val updateDate: Date,
    val easiness: Easiness,
    val time: String,
    val photo: Uri,
    val description: List<String>,
    val ingredients: List<String>,
    val tags: List<Tag>
) : Parcelable {
    companion object : Parceler<Recipe> {
        val EMPTY_RECIPE = Recipe(
            id = "",
            name = "",
            creationDate = Calendar.getInstance().time,
            updateDate = Calendar.getInstance().time,
            easiness = Easiness.EASY,
            time = "",
            photo = Uri.EMPTY,
            description = mutableListOf<String>(),
            ingredients = mutableListOf(),
            tags = mutableListOf()
        )

        private fun Parcel.writeDate(date: Date) {
            writeLong(date.time)
        }

        private fun Parcel.readDate(): Date = Date(readLong())

        private fun Parcel.writeEnum(enum: Easiness) {
            writeString(enum.name)
        }

        private fun Parcel.readEnum(): Easiness =
            Easiness.valueOf(readString() ?: Easiness.EASY.name)

        private fun Parcel.writeUri(uri: Uri) {
            writeString(uri.toString())
        }

        private fun Parcel.readUri(): Uri = Uri.parse(readString())

        override fun create(parcel: Parcel): Recipe {
            return Recipe(
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readDate(),
                parcel.readDate(),
                parcel.readEnum(),
                parcel.readString() ?: "",
                parcel.readUri(),
                mutableListOf<String>().apply { parcel.readStringList(this) },
                mutableListOf<String>().apply { parcel.readStringList(this) },
                mutableListOf<Tag>().apply {
                    parcel.readTypedList(this, parcelableCreator<Tag>())
                }
            )
        }

        override fun Recipe.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(name)
            parcel.writeDate(creationDate)
            parcel.writeDate(updateDate)
            parcel.writeEnum(easiness)
            parcel.writeString(time)
            parcel.writeUri(photo)
            parcel.writeStringList(description)
            parcel.writeStringList(ingredients)
            parcel.writeTypedList(tags)
        }
    }
}

fun Recipe.toDto(): RecipeDto {
    val ingredientKeywords = mutableListOf<String>()
    ingredients.forEach {
        val keywords = StringFormatters.generateSubStrings(it)
        val each = it.split(' ').map { e -> e.lowercase() }
        ingredientKeywords.addAll(each)
        ingredientKeywords.addAll(keywords)
    }
    val nameKeywords = mutableListOf<String>().apply {
        addAll(name.split(' ').map { e -> e.lowercase() })
        addAll(StringFormatters.generateSubStrings(name))
    }

    return RecipeDto(
        id,
        name,
        (nameKeywords + ingredientKeywords).distinct(),
        creationDate.time,
        updateDate.time,
        easiness.name,
        time,
        photo != Uri.EMPTY,
        description,
        ingredients,
        tags.map { it.id }
    )
}