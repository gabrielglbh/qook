package com.gabr.gabc.qook.domain.tag

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gabr.gabc.qook.infrastructure.tag.TagDto
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(
    val id: String,
    val text: String,
    val color: Color,
) : Parcelable {
    companion object : Parceler<Tag> {
        private fun Parcel.writeColor(color: Color) {
            writeInt(color.toArgb())
        }

        private fun Parcel.readColor(): Color {
            return Color(readInt())
        }

        override fun create(parcel: Parcel): Tag {
            return Tag(
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readColor(),
            )
        }

        override fun Tag.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(text)
            parcel.writeColor(color)
        }
    }
}

fun Tag.toDto(): TagDto {
    return TagDto(
        id,
        text,
        color.toArgb()
    )
}
