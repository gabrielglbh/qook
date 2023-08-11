package com.gabr.gabc.qook.presentation.splashPage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.homePage.HomePage
import com.gabr.gabc.qook.presentation.loginPage.LoginPage
import com.gabr.gabc.qook.presentation.shared.components.QImage
import com.gabr.gabc.qook.presentation.shared.components.QImageType
import com.gabr.gabc.qook.presentation.splashPage.viewModel.SplashViewModel
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashView()
        }
    }

    @Composable
    fun SplashView() {
        val viewModel: SplashViewModel by viewModels()

        LaunchedEffect(key1 = Unit) {
            delay(2500)
            viewModel.checkIfUserIsSignedIn(
                ifUserExists = {
                    startActivity(Intent(this@SplashPage, HomePage::class.java))
                },
                ifUserDoesNotExist = {
                    startActivity(Intent(this@SplashPage, LoginPage::class.java))
                }
            )
        }

        AppTheme {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.inversePrimary)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                QImage(
                    resource = R.drawable.splash,
                    type = QImageType.GIF,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}