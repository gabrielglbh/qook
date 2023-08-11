package com.gabr.gabc.qook.domain.tag

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gabr.gabc.qook.infrastructure.tag.TagDto

data class Tag(
    val text: String,
    val textColor: Color,
    val color: Color,
)

fun Tag.toDto(): TagDto {
    return TagDto(
        text,
        String.format("#%08X", textColor.toArgb()),
        String.format("#%08X", color.toArgb())
    )
}
