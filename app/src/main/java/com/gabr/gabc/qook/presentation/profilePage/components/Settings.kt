package com.gabr.gabc.qook.presentation.profilePage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.profilePage.viewModel.ProfileViewModel
import com.gabr.gabc.qook.presentation.shared.QDateUtils.Companion.days
import com.gabr.gabc.qook.presentation.shared.components.QChangeWeekBeginningBottomSheet
import com.gabr.gabc.qook.presentation.shared.components.QContentCard
import com.gabr.gabc.qook.presentation.shared.components.QSelectableItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    viewModel: ProfileViewModel,
    user: User,
    modifier: Modifier = Modifier
) {
    var showWeekBeginningBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showWeekBeginningBottomSheet) {
        QChangeWeekBeginningBottomSheet(
            selected = user.resetDay,
            modalBottomSheetState = sheetState,
            setShowDialog = {
                showWeekBeginningBottomSheet = it
            },
            list = days.map { stringResource(it) },
            onClick = {
                viewModel.updateUser(user.copy(resetDay = it)) {}
            }
        )
    }

    QContentCard(
        modifier = modifier.padding(12.dp),
        arrangement = Arrangement.Top,
        alignment = Alignment.Start,
        backgroundContent = {
            Icon(Icons.Outlined.Settings, "", modifier = it)
        }
    ) {
        Text(
            stringResource(R.string.profile_settings_label),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )
        QSelectableItem(
            icon = Icons.Outlined.CalendarMonth,
            text = stringResource(R.string.profile_change_reset_timing),
            trailingText = stringResource(days[user.resetDay])
        ) {
            showWeekBeginningBottomSheet = true
        }
        QSelectableItem(
            icon = Icons.Outlined.Info,
            text = stringResource(R.string.profile_about_qook),
        ) {}
    }
}