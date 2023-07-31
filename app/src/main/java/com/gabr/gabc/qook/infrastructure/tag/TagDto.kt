package com.gabr.gabc.qook.infrastructure.tag

import com.gabr.gabc.qook.domain.tag.Tag

data class TagDto (
    val text: String,
    val textColor: String,
    val color: String
)

fun TagDto.toDomain(): Tag {
    return Tag(text, textColor, color)
}