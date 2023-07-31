package com.gabr.gabc.qook.di

import com.gabr.gabc.qook.infrastructure.ingredient.IngredientRepositoryImpl
import com.gabr.gabc.qook.infrastructure.recipe.RecipeRepositoryImpl
import com.gabr.gabc.qook.infrastructure.tag.TagRepositoryImpl
import com.gabr.gabc.qook.infrastructure.user.UserRepositoryImpl
import com.gabr.gabc.qook.presentation.viewModel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::RecipeRepositoryImpl)
    singleOf(::IngredientRepositoryImpl)
    singleOf(::TagRepositoryImpl)
    singleOf(::UserRepositoryImpl)

    single { Firebase.firestore }
    single { Firebase.auth }
    single { Firebase.storage }

    viewModel { UserViewModel(get()) }
}
