package com.gabr.gabc.qook.presentation.planningSettingsPage.viewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningRepository
import com.gabr.gabc.qook.domain.user.UserRepository
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
    private val userRepository: UserRepository,
    private val provider: ContentResolverProvider
) : ViewModel() {
    var sharedPlanning = mutableStateOf(SharedPlanning.EMPTY)
        private set
    var currentUid = mutableStateOf<String?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            currentUid.value = userRepository.getCurrentUser()?.uid
        }
    }

    fun loadSharedPlanning(groupId: String) {
        viewModelScope.launch {
            sharedPlanningRepository.getSharedPlanning(groupId).collect { res ->
                res.fold(
                    ifLeft = {},
                    ifRight = { sp ->
                        sharedPlanning.value = sp
                    },
                )
            }
        }
    }

    fun updateSharedPlanning(sharedPlanning: SharedPlanning) {
        viewModelScope.launch {
            sharedPlanningRepository.updateSharedPlanning(sharedPlanning, sharedPlanning.id)
        }
    }

    fun updateGroupPhoto(uri: Uri) {
        val group = sharedPlanning.value
        viewModelScope.launch {
            val updatedUri = if (uri == Uri.EMPTY) {
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
            val updatedGroup = group.copy(photo = updatedUri)
            val res = sharedPlanningRepository.updateSharedPlanning(updatedGroup, group.id)
            res.fold(
                ifLeft = {},
                ifRight = {
                    sharedPlanning.value = sharedPlanning.value.copy(photo = updatedUri)
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

    fun exitSharedPlanning(onDelete: () -> Unit) {
        viewModelScope.launch {
            val res = sharedPlanningRepository.removeUserFromSharedPlanning(sharedPlanning.value.id)
            res.fold(
                ifLeft = {},
                ifRight = {
                    onDelete()
                }
            )
        }
    }

    fun deleteUserFromSharedPlanning(uid: String) {
        viewModelScope.launch {
            sharedPlanningRepository.removeUserFromSharedPlanning(sharedPlanning.value.id, uid)
        }
    }
}