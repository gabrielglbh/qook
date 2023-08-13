package com.gabr.gabc.qook.presentation.profilePage.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QTextForm

@Composable
fun ChangeNameDialog(setShowDialog: (Boolean) -> Unit, onClick: (String) -> Unit) {
    val textField = remember { mutableStateOf("") }

    QDialog(
        onDismissRequest = { setShowDialog(false) },
        leadingIcon = Icons.Outlined.Face,
        title = R.string.profile_change_name,
        content = {
            QTextForm(
                value = textField.value,
                labelId = R.string.login_name,
                onValueChange = {
                    textField.value = it
                }
            )
        },
        onSubmit = {
            onClick(textField.value)
        }
    )
}