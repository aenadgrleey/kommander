package com.aenadgrleey.kommander.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.core.navigation.component
import com.aenadgrleey.kommander.menu.ui.ProjectTree
import com.aenadgrleey.kommander.root.AppDependencies
import com.aenadgrleey.kommander.root.model.Event
import com.aenadgrleey.kommander.root.model.OpenFileEffect
import com.aenadgrleey.kommander.root.model.Project
import com.aenadgrleey.kommander.root.model.State

@Composable
fun MenuPane(
    rootState: State,
    onRootEvent: (Event) -> Unit,
    deps: AppDependencies,
) {
    if (rootState.project is Project.Navigable) {
        val project = rootState.project as Project.Navigable
        val menuComponent = component(project) { MenuComponent(project, deps.fs) }
        LaunchedEffect(onRootEvent, menuComponent) {
            menuComponent.effect.collect {
                when (it) {
                    is OpenFileEffect -> onRootEvent(Event.OpenProjectFile(it.path))
                }
            }
        }
        val state by menuComponent.state.collectAsState()
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
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
            )
            ProjectTree(state.items, menuComponent::dispatch)
        }
    }
}
