package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.shared.Validators

@Composable
fun QChangeNameDialog(icon: ImageVector = Icons.Outlined.Face, setShowDialog: (Boolean) -> Unit, onClick: (String) -> Unit) {
    var textField by remember { mutableStateOf("") }
    var errorName by remember { mutableStateOf(false) }

    fun onSubmit() {
        if (!errorName && textField.trim().isNotEmpty()) {
            onClick(textField)
            setShowDialog(false)
        } else {
            errorName = true
        }
    }

    QDialog(
        onDismissRequest = { setShowDialog(false) },
        leadingIcon = icon,
        title = R.string.profile_change_name,
        content = {
            QTextForm(
                value = textField,
                labelId = R.string.login_name,
                onValueChange = {
                    textField = it
                    errorName = Validators.isNameInvalid(textField)
                },
                isError = errorName,
                onSubmitWithImeAction = { onSubmit() }
            )
        },
        onSubmit = { onSubmit() },
    )
}