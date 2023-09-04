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
import com.gabr.gabc.qook.presentation.shared.components.QContentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    viewModel: ProfileViewModel,
    user: User,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        stringResource(R.string.monday),
        stringResource(R.string.tuesday),
        stringResource(R.string.wednesday),
        stringResource(R.string.thursday),
        stringResource(R.string.friday),
        stringResource(R.string.saturday),
        stringResource(R.string.sunday)
    )

    var showWeekBeginningBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showWeekBeginningBottomSheet) {
        ChangeWeekBeginningBottomSheet(
            selected = user.resetDay,
            modalBottomSheetState = sheetState,
            setShowDialog = {
                showWeekBeginningBottomSheet = it
            },
            list = days,
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
        ProfileRow(
            icon = Icons.Outlined.CalendarMonth,
            text = stringResource(R.string.profile_change_reset_timing),
            trailingText = days[user.resetDay]
        ) {
            showWeekBeginningBottomSheet = true
        }
        ProfileRow(
            icon = Icons.Outlined.Info,
            text = stringResource(R.string.profile_about_qook),
        ) {}
    }
}