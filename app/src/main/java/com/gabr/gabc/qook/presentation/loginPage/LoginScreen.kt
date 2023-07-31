package com.gabr.gabc.qook.presentation.loginPage

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.loginPage.viewModel.LoginState
import com.gabr.gabc.qook.presentation.loginPage.viewModel.LoginViewModel
import com.gabr.gabc.qook.presentation.shared.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.QTextForm
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                LoginScreenView()
            }
        }
    }

    @Preview
    @Composable
    fun LoginScreenView() {
        val viewModel: LoginViewModel by viewModels()
        val state by viewModel.loginState.collectAsState()
        return Box {
            Column {
                Text(getString(R.string.app_name))
                LoginForm(state) {
                    Log.i("LOGGER", "UserInfo ${state.name}, ${state.email}, ${state.password}")
                    CoroutineScope(Dispatchers.Main).launch { viewModel.signInUser(state) }
                }
            }
            if (viewModel.isSigningIn) QLoadingScreen()
        }
    }

    @Composable
    fun LoginForm(
        state: LoginState,
        onSubmit: () -> Unit
    ) {
        return Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            QTextForm(
                labelId = R.string.login_name,
                onValueChange = {
                    Log.i("LOGGER", it)
                },
                value = state.name,
            )
            QTextForm(
                labelId = R.string.login_email,
                onValueChange = {
                    Log.i("LOGGER", it)
                },
                value = state.email,
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
                value = state.password,
                obscured = true
            )
            Button(
                onClick = onSubmit
            ) {
                Text(stringResource(R.string.login_button))
            }
        }
    }
}
