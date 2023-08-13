package com.gabr.gabc.qook.presentation.profilePage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.profilePage.viewModel.ProfileViewModel

@Composable
fun Account(
    user: User,
    modifier: Modifier,
    viewModel: ProfileViewModel,
    onNameUpdated: () -> Unit,
    onChangePasswordSuccess: () -> Unit,
    onChangePasswordError: (String) -> Unit,
    onDeleteAccountSuccess: () -> Unit,
    onDeleteAccountError: (String) -> Unit,
) {
    val showNameDialog = remember { mutableStateOf(false) }
    val showPasswordDialog = remember { mutableStateOf(false) }
    val showDeleteAccountDialog = remember { mutableStateOf(false) }

    if (showNameDialog.value)
        ChangeNameDialog(
            setShowDialog = {
                showNameDialog.value = it
            },
            onClick = {
                viewModel.updateUser(user.copy(name = it)) {}
                onNameUpdated()
            }
        )
    if (showPasswordDialog.value)
        ChangePasswordDialog(
            setShowDialog = {
                showPasswordDialog.value = it
            },
            onClick = { old, new ->
                viewModel.changePassword(old, new, onSuccess = {
                    onChangePasswordSuccess()
                }, onError = {
                    onChangePasswordError(it)
                })
            }
        )
    if (showDeleteAccountDialog.value)
        RemoveAccountDialog(
            setShowDialog = {
                showDeleteAccountDialog.value = it
            },
            onClick = { old, new ->
                viewModel.deleteAccount(old, new, onSuccess = {
                    onDeleteAccountSuccess()
                }, onError = {
                    onDeleteAccountError(it)
                })
            }
        )

    Surface(
        modifier = modifier.padding(horizontal = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                stringResource(R.string.profile_account_label),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.padding(start = 16.dp, top = 12.dp)
            )
            ProfileRow(
                icon = Icons.Outlined.Face,
                text = stringResource(R.string.profile_change_name),
                trailingText = user.name
            ) {
                showNameDialog.value = true
            }
            ProfileRow(
                icon = Icons.Outlined.Lock,
                text = stringResource(R.string.profile_change_password)
            ) {
                showPasswordDialog.value = true
            }
            ProfileRow(
                icon = Icons.Outlined.Delete,
                text = stringResource(R.string.profile_delete_account),
                textColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                showDeleteAccountDialog.value = true
            }
        }
    }
}