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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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

    @Composable
    fun LoginScreenView() {
        val viewModel: LoginViewModel by viewModels()
        val state by viewModel.loginState.collectAsState()
        return Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(getString(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.inversePrimary
                    ),
                    modifier = Modifier.padding(bottom = 48.dp)
                )
                LoginForm(viewModel, state) {
                    Log.i("LOGGER", "UserInfo ${state.name}, ${state.email}, ${state.password}")
                    CoroutineScope(Dispatchers.Main).launch { viewModel.signInUser(state) }
                }
            }
            if (viewModel.isSigningIn) QLoadingScreen()
        }
    }

    @Composable
    fun LoginForm(
        viewModel: LoginViewModel,
        state: LoginState,
        onSubmit: () -> Unit
    ) {
        return Column(
            modifier = Modifier
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            QTextForm(
                labelId = R.string.login_name,
                onValueChange = {
                    viewModel.updateLoginState(state.copy(name = it))
                },
                value = state.name,
                imeAction = ImeAction.Next
            )
            QTextForm(
                labelId = R.string.login_email,
                onValueChange = {
                    viewModel.updateLoginState(state.copy(email = it))
                },
                value = state.email,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.email),
                        contentDescription = "Email Icon",
                    )
                },
                imeAction = ImeAction.Next
            )
            QTextForm(
                labelId = R.string.login_password,
                onValueChange = {
                    viewModel.updateLoginState(state.copy(password = it))
                },
                value = state.password,
                obscured = true
            )
            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 24.dp, end = 24.dp),
                onClick = onSubmit
            ) {
                Text(stringResource(R.string.login_button))
            }
        }
    }
}
