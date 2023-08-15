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
    val text: String,
    val textColor: Color,
    val color: Color,
) : Parcelable {
    companion object : Parceler<Tag> {
        private fun Parcel.writeColor(color: Color) {
            writeString(String.format("#%08X", color.toArgb()))
        }

        private fun Parcel.readColor(): Color {
            val colorString = readString()
            return Color(android.graphics.Color.parseColor(colorString))
        }

        override fun create(parcel: Parcel): Tag {
            return Tag(
                parcel.readString() ?: "",
                parcel.readColor(),
                parcel.readColor()
            )
        }

        override fun Tag.write(parcel: Parcel, flags: Int) {
            parcel.writeString(text)
            parcel.writeColor(textColor)
            parcel.writeColor(color)
        }
    }
}

fun Tag.toDto(): TagDto {
    return TagDto(
        text,
        String.format("#%08X", textColor.toArgb()),
        String.format("#%08X", color.toArgb())
    )
}
