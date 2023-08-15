package com.gabr.gabc.qook.presentation.shared.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.gabr.gabc.qook.R

@Composable
fun QTextForm(
    labelId: Int,
    modifier: Modifier = Modifier,
    value: String = "",
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    leadingIcon: ImageVector? = null,
    obscured: Boolean = false,
    focusedColor: Color = MaterialTheme.colorScheme.primary,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        onValueChange = onValueChange,
        label = { Text(stringResource(labelId)) },
        isError = isError,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = keyboardType,
            capitalization = KeyboardCapitalization.Sentences
        ),
        leadingIcon = if (leadingIcon == null) {
            null
        } else {
            { Icon(leadingIcon, contentDescription = "") }
        },
        visualTransformation = if (obscured) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = focusedColor,
            focusedTrailingIconColor = focusedColor,
            focusedLabelColor = focusedColor,
        )
    )
}

@Preview
@Composable
fun PreviewQTextForm() {
    QTextForm(
        labelId = R.string.app_name,
        onValueChange = {
            Log.i("LOGGER", it)
        },
        value = "email@gmail.com",
        leadingIcon = Icons.Outlined.Email
    )
}