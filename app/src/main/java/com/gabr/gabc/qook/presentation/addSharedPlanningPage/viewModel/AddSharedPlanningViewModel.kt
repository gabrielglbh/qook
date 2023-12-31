package com.gabr.gabc.qook.presentation.addSharedPlanningPage.viewModel

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
import javax.inject.Inject

@HiltViewModel
class AddSharedPlanningViewModel @Inject constructor(
    private val sharedPlanningRepository: SharedPlanningRepository,
    private val provider: ContentResolverProvider
) : ViewModel() {
    var sharedPlanning = mutableStateOf(SharedPlanning.EMPTY)
        private set
    var isLoading = mutableStateOf(false)
        private set

    fun updateSharedPlanning(
        name: String? = null,
        resetDay: Int? = null,
        photo: Uri? = null,
    ) {
        val value = sharedPlanning.value
        sharedPlanning.value = value.copy(
            name = name ?: value.name,
            resetDay = resetDay ?: value.resetDay,
            photo = photo ?: value.photo,
        )
    }

    fun createSharedPlanning(onError: (String) -> Unit, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val sharedPlanning = sharedPlanning.value
            val res = sharedPlanningRepository.createSharedPlanning(
                sharedPlanning.copy(
                    photo = if (sharedPlanning.photo == Uri.EMPTY) {
                        Uri.EMPTY
                    } else if (sharedPlanning.photo.host != Globals.FIREBASE_HOST) {
                        Uri.fromFile(
                            ResizeImageUtil.resizeImageToFile(
                                sharedPlanning.photo,
                                provider.contentResolver()
                            )
                        )
                    } else {
                        sharedPlanning.photo
                    },
                )
            )
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = { sp -> onSuccess(sp.id) },
            )
            isLoading.value = false
        }
    }
}