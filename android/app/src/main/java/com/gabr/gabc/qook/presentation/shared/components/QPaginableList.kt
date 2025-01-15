package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun QPaginatedListTrigger(
    listState: LazyListState,
    initialThreshold: Int,
    loadMoreItems: () -> Unit,
) {
    var numberOfTimesThresholdReached by remember { mutableIntStateOf(1) }
    var maxThreshold by remember { mutableIntStateOf(initialThreshold) }

    val thresholdUpdate = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItemIndex =
                (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            val isLastItemNotAlreadyProvided = lastVisibleItemIndex >= maxThreshold
            isLastItemNotAlreadyProvided
        }
    }

    LaunchedEffect(thresholdUpdate) {
        snapshotFlow { thresholdUpdate.value }.distinctUntilChanged().collect {
            if (it) {
                numberOfTimesThresholdReached++
                maxThreshold = initialThreshold * numberOfTimesThresholdReached
                loadMoreItems()
            }
        }
    }
}