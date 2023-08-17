package com.gabr.gabc.qook.domain.recipe

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.infrastructure.recipe.RecipeDto
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
    val description: String,
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
            description = "",
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
                parcel.readString() ?: "",
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
            parcel.writeString(description)
            parcel.writeStringList(ingredients)
            parcel.writeTypedList(tags)
        }
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
        ingredients,
        tags.map { it.id }
    )
}