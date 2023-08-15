package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.domain.tag.Tag

@Composable
fun QTag(
    tag: Tag,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        modifier = modifier.padding(horizontal = 12.dp),
        color = tag.color,
        enabled = enabled,
        onClick = {
            onClick?.let { it() }
        },
    ) {
        Text(
            tag.text,
            color = tag.textColor,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
        )
    }
}