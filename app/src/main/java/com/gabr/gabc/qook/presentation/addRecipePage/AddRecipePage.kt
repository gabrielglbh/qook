package com.gabr.gabc.qook.presentation.addRecipePage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.addRecipePage.components.RecipeDescription
import com.gabr.gabc.qook.presentation.addRecipePage.components.RecipeIngredients
import com.gabr.gabc.qook.presentation.addRecipePage.components.RecipeMetadataForm
import com.gabr.gabc.qook.presentation.addRecipePage.components.RecipeTags
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.addTagPage.AddTagPage
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.theme.AppTheme
import com.gabr.gabc.qook.presentation.theme.seed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRecipePage : ComponentActivity() {
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { actionMap ->
                when (actionMap.key) {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        if (!actionMap.value) {
                            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }

                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        if (actionMap.value) {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        } else {
                            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }

                    Manifest.permission.READ_MEDIA_IMAGES -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (actionMap.value) {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            } else {
                                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                        }
                    }
                }
            }
        }
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras
                val viewModel: AddRecipeViewModel by viewModels()

                if (extras?.getBoolean(AddTagPage.HAS_CREATED_TAG) == true) {
                    viewModel.gatherTags()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AddRecipeViewModel by viewModels()

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.updateMetadata(photo = uri)
                }
            }

        setContent {
            AppTheme {
                AddRecipeView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun AddRecipeView() {
        val viewModel: AddRecipeViewModel by viewModels()
        val colorScheme = MaterialTheme.colorScheme

        var currentPage by remember { mutableStateOf("step1") }
        var bar2Color by remember { mutableStateOf(colorScheme.outlineVariant) }
        var bar3Color by remember { mutableStateOf(colorScheme.outlineVariant) }
        var bar4Color by remember { mutableStateOf(colorScheme.outlineVariant) }
        val navController = rememberNavController()

        navController.addOnDestinationChangedListener { _, dest, _ ->
            currentPage = dest.route ?: "step1"
            bar2Color =
                if (currentPage == "step2" || currentPage == "step3" || currentPage == "step4") {
                    seed
                } else {
                    colorScheme.outlineVariant
                }
            bar3Color = if (currentPage == "step3" || currentPage == "step4") {
                seed
            } else {
                colorScheme.outlineVariant
            }
            bar4Color = if (currentPage == "step4") {
                seed
            } else {
                colorScheme.outlineVariant
            }
        }

        Scaffold(
            topBar = {
                QActionBar(
                    title = R.string.add_recipe_title,
                    onBack = {
                        if (currentPage != "step1") {
                            navController.popBackStack()
                        } else {
                            finish()
                        }
                    },
                    actionBehaviour = {
                        if (currentPage == "step2") {
                            val intent = Intent(this@AddRecipePage, AddTagPage::class.java)
                            resultLauncher.launch(intent)
                        }
                    },
                    action = {
                        if (currentPage == "step2") {
                            Icon(Icons.Outlined.BookmarkAdd, contentDescription = "")
                        }
                    }
                )
            }
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .consumeWindowInsets(it)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RecipeProgressBar(
                        colorBar2 = bar2Color,
                        colorBar3 = bar3Color,
                        colorBar4 = bar4Color
                    )
                    NavHost(
                        navController = navController,
                        startDestination = "step1",
                        modifier = Modifier.weight(1f)
                    ) {
                        composable("step1") {
                            RecipeMetadataForm(
                                onNavigate = {
                                    navController.navigate("step2")
                                },
                                requestMultiplePermissions = requestMultiplePermissions,
                                viewModel = viewModel
                            )
                        }
                        composable("step2") {
                            RecipeTags(
                                onNavigate = {
                                    navController.navigate("step3")
                                },
                                viewModel = viewModel
                            )
                        }
                        composable("step3") { RecipeIngredients { navController.navigate("step4") } }
                        composable("step4") { RecipeDescription {} }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }
    }

    @Composable
    fun RecipeProgressBar(
        colorBar2: Color,
        colorBar3: Color,
        colorBar4: Color
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = seed,
                modifier = Modifier
                    .height(6.dp)
                    .weight(1f)
            ) {}
            Spacer(modifier = Modifier.size(8.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = colorBar2,
                modifier = Modifier
                    .height(6.dp)
                    .weight(1f)
            ) {}
            Spacer(modifier = Modifier.size(8.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = colorBar3,
                modifier = Modifier
                    .height(6.dp)
                    .weight(1f)
            ) {}
            Spacer(modifier = Modifier.size(8.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = colorBar4,
                modifier = Modifier
                    .height(6.dp)
                    .weight(1f)
            ) {}
        }
    }
}