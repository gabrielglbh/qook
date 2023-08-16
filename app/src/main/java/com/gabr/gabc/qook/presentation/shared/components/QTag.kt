package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.domain.tag.Tag

@Composable
fun QTag(
    tag: Tag,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        modifier = modifier,
        color = tag.color,
        enabled = enabled,
        onClick = {
            onClick?.let { it() }
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                tag.text,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (icon != null) Icon(
                imageVector = icon,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(14.dp)
            )
        }
    }
}