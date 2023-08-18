package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun QAutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 1,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    val localStyle = LocalTextStyle.current
    var textStyle by remember { mutableStateOf(style ?: localStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        style = textStyle,
        textAlign = textAlign,
        maxLines = maxLines,
        softWrap = false,
        color = color,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight) {
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )

}