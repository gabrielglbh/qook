package com.gabr.gabc.qook.presentation.shared.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import arrow.core.Either
import com.gabr.gabc.qook.R

@Composable
fun QActionBar(
    action: Either<@Composable () -> Unit, ImageVector>? = null,
    actionBehaviour: (() -> Unit)? = null,
    actionBorder: BorderStroke? = null,
    onBack: (() -> Unit)? = null,
    @StringRes title: Int = R.string.app_name,
) {
    if ((action == null && actionBehaviour != null) || (action != null && actionBehaviour == null)) {
        throw Exception("You must provide an action and actionBehaviour")
    }

    var actionMapped: @Composable () -> Unit = {}
    action?.let {
        it.fold(
            ifLeft = {
                actionMapped = {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 8.dp, top = 4.dp, bottom = 4.dp),
                        onClick = {
                            actionBehaviour!!()
                        },
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = actionBorder
                    ) {
                        it()
                    }
                }
            },
            ifRight = {
                actionMapped = {
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        color = Color.Transparent
                    ) {
                        IconButton(
                            onClick = { actionBehaviour!!() }
                        ) {
                            Icon(it, "")
                        }
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        QAutoSizeText(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier.padding(start = 8.dp),
                color = Color.Transparent,
            ) {
                if (onBack != null) IconButton(
                    onClick = { onBack() }
                ) {
                    Icon(Icons.Outlined.KeyboardArrowLeft, "")
                }
            }
            actionMapped()
        }
    }
}