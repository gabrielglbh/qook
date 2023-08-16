package com.gabr.gabc.qook.presentation.shared.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun QTextTitle(
    @StringRes title: Int,
    @StringRes subtitle: Int
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            stringResource(subtitle),
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.outline
            ),
            textAlign = TextAlign.Center
        )
    }
}