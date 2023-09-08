package com.gabr.gabc.qook.presentation.shared.components

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.DataExploration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R

@Composable
fun QActionBar(
    actions: List<@Composable () -> Unit>? = null,
    onBack: (() -> Unit)? = null,
    @StringRes title: Int = R.string.app_name,
) {
    BackHandler {
        onBack?.let { it() }
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
                .padding(horizontal = ((actions?.size ?: 1) * 48).dp)
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
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, "")
                }
            }
            if (actions != null) Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                actions.forEach { it() }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQActionBar() {
    QActionBar(
        actions = listOf {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.DataExploration, contentDescription = "")
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.DataExploration, contentDescription = "")
            }
        }
    )
}