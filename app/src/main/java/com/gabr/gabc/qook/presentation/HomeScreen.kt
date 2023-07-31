package com.gabr.gabc.qook.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.gabr.gabc.qook.R
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

    // TODO: https://insert-koin.io/docs/quickstart/android-compose/#injecting-viewmodel-in-compose
}
