package com.gabr.gabc.qook.infrastructure.tag

import androidx.compose.ui.graphics.Color
import com.gabr.gabc.qook.domain.tag.Tag
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class TagDto(
    @DocumentId val id: String = "",
    @PropertyName("text") val text: String = "",
    @PropertyName("color") val color: Int = -1
)

fun TagDto.toDomain(): Tag {
    return Tag(id, text, Color(color))
}

fun TagDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair("text", text),
        Pair("color", color),
    )
}