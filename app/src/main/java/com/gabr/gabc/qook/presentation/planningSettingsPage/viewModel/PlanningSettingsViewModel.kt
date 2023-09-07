package com.gabr.gabc.qook.presentation.planningSettingsPage.viewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningRepository
import com.gabr.gabc.qook.presentation.shared.Globals
import com.gabr.gabc.qook.presentation.shared.ResizeImageUtil
import com.gabr.gabc.qook.presentation.shared.providers.ContentResolverProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PlanningSettingsViewModel @Inject constructor(
    private val sharedPlanningRepository: SharedPlanningRepository,
    private val provider: ContentResolverProvider
) : ViewModel() {
    var sharedPlanning = mutableStateOf(SharedPlanning.EMPTY_SHARED_PLANNING)
        private set
    var isLoading = mutableStateOf(false)
        private set

    fun loadSharedPlanning(sharedPlanning: SharedPlanning) {
        this.sharedPlanning.value = sharedPlanning
    }

    fun updateSharedPlanning(sharedPlanning: SharedPlanning) {
        viewModelScope.launch {
            val res =
                sharedPlanningRepository.updateSharedPlanning(sharedPlanning, sharedPlanning.id)
            res.fold(
                ifLeft = {},
                ifRight = {
                    loadSharedPlanning(sharedPlanning)
                }
            )
        }
    }

    fun updateGroupPhoto(uri: Uri) {
        val group = sharedPlanning.value
        viewModelScope.launch {
            val updatedGroup = group.copy(
                photo = if (uri == Uri.EMPTY) {
                    Uri.EMPTY
                } else if (uri.host != Globals.FIREBASE_HOST) {
                    Uri.fromFile(
                        ResizeImageUtil.resizeImageToFile(
                            uri,
                            provider.contentResolver(),
                            name = Calendar.getInstance().timeInMillis.toString()
                        )
                    )
                } else {
                    uri
                }
            )
            val res = sharedPlanningRepository.updateSharedPlanning(updatedGroup, group.id)
            res.fold(
                ifLeft = {},
                ifRight = {
                    loadSharedPlanning(updatedGroup)
                }
            )
        }
    }

    fun deleteSharedPlanning(onDelete: () -> Unit) {
        viewModelScope.launch {
            val res = sharedPlanningRepository.deleteSharedPlanning(sharedPlanning.value)
            res.fold(
                ifLeft = {},
                ifRight = {
                    onDelete()
                }
            )
        }
    }
}