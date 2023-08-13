package com.gabr.gabc.qook.presentation.profilePage.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QTextForm

@Composable
fun ChangePasswordDialog(setShowDialog: (Boolean) -> Unit, onClick: (String, String) -> Unit) {
    var oldPasswordField by remember { mutableStateOf("") }
    var newPasswordField by remember { mutableStateOf("") }
    var errorOld by remember { mutableStateOf(false) }
    var errorNew by remember { mutableStateOf(false) }

    QDialog(
        onDismissRequest = { setShowDialog(false) },
        leadingIcon = Icons.Outlined.Lock,
        title = R.string.profile_change_password,
        content = {
            Column {
                QTextForm(
                    value = oldPasswordField,
                    labelId = R.string.profile_old_password,
                    onValueChange = {
                        oldPasswordField = it
                        errorOld = Validators.isPasswordInvalid(oldPasswordField)
                    },
                    obscured = true,
                    isError = errorOld
                )
                Spacer(modifier = Modifier.size(20.dp))
                QTextForm(
                    value = newPasswordField,
                    labelId = R.string.profile_new_password,
                    onValueChange = {
                        newPasswordField = it
                        errorNew = Validators.isPasswordInvalid(newPasswordField)
                    },
                    obscured = true,
                    isError = errorNew
                )
            }
        },
        onSubmit = {
            if (!errorOld && !errorNew && oldPasswordField.trim()
                    .isNotEmpty() && newPasswordField.trim().isNotEmpty()
            ) {
                onClick(oldPasswordField, newPasswordField)
                setShowDialog(false)
            } else {
                errorOld = true
                errorNew = true
            }
        }
    )
}