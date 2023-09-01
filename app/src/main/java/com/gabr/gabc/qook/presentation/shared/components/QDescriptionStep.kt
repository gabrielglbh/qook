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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun QDescriptionStep(
    step: String,
    stepIndex: Int,
    color: Color = MaterialTheme.colorScheme.onBackground,
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
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                "${stepIndex + 1}.",
                color = color,
            )
            Text(
                step,
                color = color,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Justify,
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