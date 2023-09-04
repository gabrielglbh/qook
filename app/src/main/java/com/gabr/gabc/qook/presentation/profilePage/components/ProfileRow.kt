package com.gabr.gabc.qook.presentation.profilePage.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.presentation.shared.components.QAutoSizeText

@Composable
fun ProfileRow(
    modifier: Modifier = Modifier,
    @DrawableRes res: Int? = null,
    icon: ImageVector? = null,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    trailingText: String? = null,
    text: String,
    onClick: () -> Unit,
) {
    if (res != null && icon != null) {
        throw Exception("Cannot render an Icon and a Image together")
    }

    Surface(
        color = Color.Transparent,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (res != null) Image(
                painter = painterResource(res),
                contentDescription = null,
                colorFilter = ColorFilter.tint(textColor),
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