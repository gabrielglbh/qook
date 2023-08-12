package com.gabr.gabc.qook.presentation.profilePage.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRow(
    @DrawableRes res: Int? = null,
    icon: ImageVector? = null,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    trailingText: String? = null,
    text: String,
    onClick: () -> Unit,
) {
    if (res != null && icon != null) {
        throw Exception("Cannot render an Icon and a Image together")
    }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
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
            Text(
                text,
                modifier = Modifier
                    .padding(
                        top = 12.dp,
                        start = 24.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    )
                    .weight(1f),
                color = textColor,
            )
            if (trailingText != null) Text(
                trailingText,
                modifier = Modifier.padding(
                    top = 12.dp,
                    start = 24.dp,
                    end = 16.dp,
                    bottom = 12.dp
                ),
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}