package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R

@Composable
fun QRemoveButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = { onClick() },
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.error
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
        )
    ) {
        Text(
            stringResource(R.string.tags_remove_tag),
            color = MaterialTheme.colorScheme.error
        )
    }
}