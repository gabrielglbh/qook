package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R

@Composable
fun QQookTitle() {
    Text(
        stringResource(R.string.app_name),
        style = MaterialTheme.typography.headlineLarge.copy(
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(bottom = 48.dp)
    )
}