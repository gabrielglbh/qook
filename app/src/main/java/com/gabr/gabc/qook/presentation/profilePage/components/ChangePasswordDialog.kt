package com.gabr.gabc.qook.presentation.profilePage.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QTextForm

@Composable
fun ChangePasswordDialog(setShowDialog: (Boolean) -> Unit, onClick: (String, String) -> Unit) {
    val oldPasswordField = remember { mutableStateOf("") }
    val newPasswordField = remember { mutableStateOf("") }

    QDialog(
        onDismissRequest = { setShowDialog(false) },
        leadingIcon = Icons.Outlined.Lock,
        title = R.string.profile_change_password,
        content = {
            Column {
                QTextForm(
                    value = oldPasswordField.value,
                    labelId = R.string.profile_old_password,
                    onValueChange = {
                        oldPasswordField.value = it
                    },
                    obscured = true
                )
                Spacer(modifier = Modifier.size(20.dp))
                QTextForm(
                    value = newPasswordField.value,
                    labelId = R.string.profile_new_password,
                    onValueChange = {
                        newPasswordField.value = it
                    },
                    obscured = true
                )
            }
        },
        onSubmit = {
            onClick(oldPasswordField.value, newPasswordField.value)
        }
    )
}