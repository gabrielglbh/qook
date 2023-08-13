package com.gabr.gabc.qook.presentation.profilePage.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QTextForm

@Composable
fun RemoveAccountDialog(setShowDialog: (Boolean) -> Unit, onClick: (String, String) -> Unit) {
    val oldPasswordField = remember { mutableStateOf("") }
    val newPasswordField = remember { mutableStateOf("") }

    QDialog(
        onDismissRequest = { setShowDialog(false) },
        leadingIcon = Icons.Outlined.Delete,
        title = R.string.profile_delete_account,
        disclaimer = {
            Text(
                stringResource(R.string.profile_delete_account_disclaimer),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        },
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