package com.aenadgrleey.kommander.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.common.model.OnOpenEditor
import com.aenadgrleey.kommander.common.model.OpenEditor
import com.aenadgrleey.kommander.menu.ui.ProjectTree

@Composable
fun MenuPaneContent(
    menuComponent: MenuComponent,
    openEditor: OnOpenEditor
) {
    val state by menuComponent.state.collectAsState()
    LaunchedEffect(openEditor) {
        menuComponent.effect.collect {
            when (it) {
                is OpenEditor -> openEditor(it)
            }
        }
    }
    Column {
        Text(
            text = "Project Tree",
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 22.dp,
                    bottom = 10.dp
                )
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                )
        )
        ProjectTree(state.items, menuComponent::dispatch)
    }
}
