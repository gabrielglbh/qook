package com.gabr.gabc.qook.presentation.splashPage

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabr.gabc.qook.presentation.homePage.HomePage
import com.gabr.gabc.qook.presentation.loginPage.LoginPage
import com.gabr.gabc.qook.presentation.shared.components.QQookTitle
import com.gabr.gabc.qook.presentation.splashPage.viewModel.SplashViewModel
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

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
            viewModel.checkIfUserIsSignedIn(
                ifUserExists = {
                    val intent = Intent(this@SplashPage, HomePage::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val options = ActivityOptions.makeCustomAnimation(
                            baseContext,
                            R.anim.abc_fade_in,
                            R.anim.abc_fade_out
                        )
                        startActivity(intent, options.toBundle())
                    } else {
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                },
                ifUserDoesNotExist = {
                    val intent = Intent(this@SplashPage, LoginPage::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val options = ActivityOptions.makeCustomAnimation(
                            baseContext,
                            R.anim.abc_fade_in,
                            R.anim.abc_fade_out
                        )
                        startActivity(intent, options.toBundle())
                    } else {
                        startActivity(intent)
                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
                    }
                }
            )
        }

        AppTheme {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                QQookTitle()
            }
        }
    }
}