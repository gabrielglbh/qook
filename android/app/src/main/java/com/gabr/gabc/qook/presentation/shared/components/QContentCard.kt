package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun QContentCard(
    modifier: Modifier = Modifier,
    arrangement: Arrangement.Vertical = Arrangement.Center,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onClick: (() -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    backgroundContent: (@Composable (Modifier) -> Unit)? = null,
    backgroundSize: Dp = 148.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = color,
        enabled = onClick != null,
        onClick = { onClick?.let { it() } }
    ) {
        Box(
            contentAlignment = if (backgroundContent != null) {
                Alignment.CenterEnd
            } else {
                Alignment.CenterStart
            }
        ) {
            if (backgroundContent != null) backgroundContent(
                Modifier
                    .alpha(0.1f)
                    .size(backgroundSize)
                    .offset(64.dp, 0.dp)
            )
            Column(
                verticalArrangement = arrangement,
                horizontalAlignment = alignment,
                modifier = Modifier.padding(12.dp)
            ) {
                content()
            }
        }
    }
}