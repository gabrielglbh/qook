package com.gabr.gabc.qook.presentation.shared.providers

import android.content.ContentResolver
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentResolverProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun contentResolver(): ContentResolver = context.contentResolver
}