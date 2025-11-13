package com.aenadgrleey.kommander.app_actions

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import com.aenadgrleey.kommander.root.model.Event as RootEvent
import com.aenadgrleey.kommander.root.model.State as RootState

@Composable
fun FrameWindowScope.AppActions(
    rootState: RootState,
    onRootEvent: (RootEvent) -> Unit,
) {

}