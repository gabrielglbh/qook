package com.gabr.gabc.qook.presentation.shared

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.gabr.gabc.qook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QTextForm(
    labelId: Int,
    value: String = "",
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    return OutlinedTextField(
        value = value,
        singleLine = singleLine,
        modifier = Modifier
            .fillMaxWidth(),
        onValueChange = onValueChange,
        label = { Text(stringResource(labelId)) },
        isError = false,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction
        ),
        trailingIcon = trailingIcon
    )
}

@Preview
@Composable
fun PreviewQTextForm() {
    return QTextForm(
        labelId = R.string.app_name,
        onValueChange = {
            Log.i("LOGGER", it)
        },
        value = "email@gmail.com",
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.email),
                contentDescription = "Email Icon"
            )
        }
    )
}