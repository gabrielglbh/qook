package com.gabr.gabc.qook.infrastructure

import com.gabr.gabc.qook.domain.Tag

data class TagDto (
    val text: String,
    val textColor: String,
    val color: String
)

fun TagDto.toDomain(): Tag {
    return Tag(text, textColor, color)
}