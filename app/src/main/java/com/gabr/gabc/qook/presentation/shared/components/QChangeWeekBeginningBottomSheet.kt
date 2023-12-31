package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QChangeWeekBeginningBottomSheet(
    selected: Int,
    modalBottomSheetState: SheetState,
    setShowDialog: (Boolean) -> Unit,
    list: List<String> = listOf(),
    onClick: (Int) -> Unit
) {
    QBottomSheet(modalBottomSheetState = modalBottomSheetState, onDismiss = setShowDialog) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            QTextTitle(
                title = R.string.profile_change_reset_timing,
                subtitle = R.string.profile_change_reset_description
            )
            Spacer(modifier = Modifier.size(12.dp))
            LazyColumn {
                items(list.size) { x ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .align(Alignment.CenterHorizontally),
                        onClick = {
                            onClick(x)
                            setShowDialog(false)
                        },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = list[x],
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .weight(1f)
                            )
                            if (selected == x) Icon(Icons.Outlined.Check, contentDescription = "")
                        }
                    }
                }
            }
        }
    }
}