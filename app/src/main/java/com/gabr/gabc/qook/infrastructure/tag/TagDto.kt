package com.gabr.gabc.qook.infrastructure.tag

import androidx.compose.ui.graphics.Color
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.shared.Globals
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class TagDto(
    @DocumentId val id: String = "",
    @PropertyName(Globals.OBJ_TAG_NAME) val text: String = "",
    @PropertyName(Globals.OBJ_TAG_KEYWORDS) val keywords: List<String> = listOf(),
    @PropertyName("color") val color: Int = -1
)

fun TagDto.toDomain(): Tag {
    return Tag(id, text, Color(color))
}

fun TagDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair(Globals.OBJ_TAG_NAME, text),
        Pair(Globals.OBJ_TAG_KEYWORDS, keywords),
        Pair("color", color),
    )
}