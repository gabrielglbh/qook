package com.gabr.gabc.qook.infrastructure.tag

import androidx.compose.ui.graphics.Color
import com.gabr.gabc.qook.domain.tag.Tag
import com.google.firebase.firestore.PropertyName
import android.graphics.Color as UIColor

data class TagDto(
    @PropertyName("text") val text: String = "",
    @PropertyName("textColor") val textColor: String = " ",
    @PropertyName("color") val color: String = " "
)

fun TagDto.toDomain(): Tag {
    return Tag(text, Color(UIColor.parseColor(textColor)), Color(UIColor.parseColor(color)))
}