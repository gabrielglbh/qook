package com.gabr.gabc.qook.presentation.shared.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.unit.dp

@Composable
fun QSelectableItem(
    modifier: Modifier = Modifier,
    uri: Uri? = null,
    icon: ImageVector? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailingText: String? = null,
    text: String,
    onClick: (() -> Unit)? = null,
) {
    if (uri != null && icon != null) {
        throw Exception("Cannot render an Icon and a Image together")
    }

    Surface(
        color = Color.Transparent,
        modifier = modifier.fillMaxWidth(),
        enabled = onClick != null,
        onClick = { onClick?.let { it() } },
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (uri != null) QImageContainer(
                uri,
                placeholder = Icons.Outlined.Person,
                size = 48.dp,
                modifier = Modifier.padding(start = 16.dp)
            )
            if (icon != null) Icon(
                icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.padding(start = 16.dp)
            )
            QAutoSizeText(
                text = text,
                modifier = Modifier
                    .padding(
                        top = 12.dp,
                        start = 12.dp,
                        end = 4.dp,
                        bottom = 12.dp
                    )
                    .weight(1f),
                color = textColor,
            )
            if (trailingText != null) Text(
                trailingText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    top = 12.dp,
                    start = 8.dp,
                    end = 16.dp,
                    bottom = 12.dp
                ),
                color = textColor,
            )
        }
    }
}