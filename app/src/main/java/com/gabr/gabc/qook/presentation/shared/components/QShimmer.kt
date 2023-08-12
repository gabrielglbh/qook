package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun QShimmer(controller: Boolean, composable: @Composable (Modifier) -> Unit) {
    val alpha by animateFloatAsState(
        if (controller) {
            1f
        } else {
            0f
        },
        tween(1000), label = "alpha",
    )

    composable(Modifier.alpha(alpha))
}