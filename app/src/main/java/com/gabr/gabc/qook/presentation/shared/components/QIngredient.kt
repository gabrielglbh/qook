package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun QIngredient(
    ingredient: String,
    strikeThrough: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onClear: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.let { it() } },
        enabled = onClick != null,
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            if (leadingIcon == null) {
                Text("•")
            } else {
                leadingIcon()
            }
            Text(
                ingredient,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                textDecoration = if (strikeThrough) {
                    TextDecoration.LineThrough
                } else {
                    null
                },
            )
            if (onClear != null) IconButton(
                modifier = Modifier.size(24.dp),
                onClick = { onClear() }
            ) {
                Icon(Icons.Outlined.Clear, contentDescription = null)
            }
        }
    }
}