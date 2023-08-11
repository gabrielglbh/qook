package com.gabr.gabc.qook.presentation.homePage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.shared.components.QImage
import com.gabr.gabc.qook.presentation.shared.components.QImageType
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                HomeView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun HomeView() {
        Scaffold(
            topBar = {
                ActionBar()
            },
            bottomBar = {
                BottomNavigationBar()
            }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("HOME_SCREEN", modifier = Modifier.padding(it))
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ActionBar() {
        return Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Surface(
                modifier = Modifier
                    .width(48.dp)
                    .height(36.dp)
                    .padding(end = 12.dp),
                onClick = {},
                shape = MaterialTheme.shapes.extraLarge,
                color = Color.Transparent,
                border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.inversePrimary)
            ) {
                QImage(
                    resource = R.drawable.anonymous,
                    type = QImageType.ASSET,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    @Composable
    fun BottomNavigationBar() {
        return Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 8.dp,
                    spotColor = Color.Transparent
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BottomNavButton(
                icon = R.drawable.shopping,
                text = stringResource(R.string.home_shopping_bnb),
                modifier = Modifier.padding(start = 32.dp),
                onClick = {}
            )
            BottomNavButton(
                icon = R.drawable.add,
                text = stringResource(R.string.home_add_recipe_bnb),
                onClick = {}
            )
            BottomNavButton(
                icon = R.drawable.recipes,
                text = stringResource(R.string.home_recipes_bnb),
                modifier = Modifier.padding(end = 32.dp),
                onClick = {}
            )
        }
    }

    @Composable
    fun BottomNavButton(
        onClick: () -> Unit,
        @DrawableRes icon: Int,
        text: String,
        modifier: Modifier = Modifier
    ) {
        return Column(
            modifier = modifier.clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .width(36.dp)
                    .height(36.dp),
            ) {
                QImage(
                    resource = icon,
                    type = QImageType.ASSET,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(text)
        }
    }
}