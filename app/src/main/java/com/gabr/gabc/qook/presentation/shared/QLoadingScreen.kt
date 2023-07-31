package com.gabr.gabc.qook.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gabr.gabc.qook.R

@Composable
fun QLoadingScreen() {
    return Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
    ) {
        Text(stringResource(R.string.app_name))
    }
}