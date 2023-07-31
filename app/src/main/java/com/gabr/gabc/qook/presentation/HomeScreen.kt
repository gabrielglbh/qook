package com.gabr.gabc.qook.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.shared.QTextForm
import com.gabr.gabc.qook.presentation.theme.AppTheme

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Text(getString(R.string.app_name))
            }
        }
    }

    @Preview
    @Composable
    fun PreviewLogin() {
        return Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            QTextForm(
                labelId = R.string.login_name,
                onValueChange = {
                    Log.i("LOGGER", it)
                },
                value = "Gabriel",
            )
            QTextForm(
                labelId = R.string.login_email,
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
            QTextForm(
                labelId = R.string.login_password,
                onValueChange = {
                    Log.i("LOGGER", it)
                },
                value = "Password",
            )
        }
    }

    // TODO: https://insert-koin.io/docs/quickstart/android-compose/#injecting-viewmodel-in-compose
}
