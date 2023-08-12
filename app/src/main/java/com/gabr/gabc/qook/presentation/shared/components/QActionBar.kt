package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QActionBar(
    action: (@Composable () -> Unit)? = null,
    actionBehaviour: (() -> Unit)? = null,
    actionBorder: BorderStroke? = null,
    onBack: (() -> Unit)? = null
) {
    if ((action == null && actionBehaviour != null) || (action != null && actionBehaviour == null)) {
        throw Exception("You must provide action and actionBehaviour")
    }

    return Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier
                .width(48.dp)
                .height(36.dp)
                .padding(start = 12.dp),
            enabled = onBack != null,
            onClick = {
                onBack!!()
            },
            shape = CircleShape,
            color = Color.Transparent,
        ) {
            if (onBack != null) Icon(
                Icons.Outlined.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Surface(
            modifier = Modifier
                .width(48.dp)
                .height(36.dp)
                .padding(end = 12.dp),
            enabled = action != null,
            onClick = {
                if (actionBehaviour != null) actionBehaviour()
            },
            shape = CircleShape,
            color = Color.Transparent,
            border = actionBorder
        ) {
            if (action != null) action()
        }
    }
}