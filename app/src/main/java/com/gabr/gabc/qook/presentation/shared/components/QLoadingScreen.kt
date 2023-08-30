package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R

@Preview(showBackground = true)
@Composable
fun QLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        QImage(
            resource = R.drawable.loading,
            type = QImageType.GIF,
            modifier = Modifier.padding(horizontal = 64.dp)
        )
    }
}