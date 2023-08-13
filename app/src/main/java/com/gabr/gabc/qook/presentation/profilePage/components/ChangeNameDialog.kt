package com.gabr.gabc.qook.presentation.profilePage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.shared.components.QTextForm

@Composable
fun ChangeNameDialog(setShowDialog: (Boolean) -> Unit, onClick: (String) -> Unit) {
    val textField = remember { mutableStateOf("") }

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Face,
                            contentDescription = "",
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.profile_change_name),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    QTextForm(
                        value = textField.value,
                        labelId = R.string.login_name,
                        onValueChange = {
                            textField.value = it
                        })
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                        Button(
                            onClick = {
                                onClick(textField.value)
                                setShowDialog(false)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.profile_save))
                        }
                    }
                }
            }
        }
    }
}