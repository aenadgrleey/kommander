@file:OptIn(ExperimentalFoundationApi::class)

package com.aenadgrleey.kommander.menu.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.common.model.FileType
import com.aenadgrleey.kommander.core.icons.FileKotlin
import com.aenadgrleey.kommander.core.icons.FileOrdinary
import com.aenadgrleey.kommander.core.icons.FolderClosed
import com.aenadgrleey.kommander.core.icons.FolderOpen
import com.aenadgrleey.kommander.menu.model.MenuEvent
import com.aenadgrleey.kommander.menu.model.MenuEvent.*
import com.aenadgrleey.kommander.menu.model.ProjectNode
import java.io.File


@Composable
fun ProjectNode(
    node: ProjectNode,
    modifier: Modifier = Modifier,
    onEvent: (MenuEvent) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .padding(top = 4.dp)
            .hoverable(interactionSource)
            .onClick(interactionSource = interactionSource) {
                when (node) {
                    is ProjectNode.FileNode -> onEvent(FileSelect(node))
                    is ProjectNode.ClosedDirectoryNode -> onEvent(DirectoryOpen(node))
                    is ProjectNode.OpenDirectoryNode -> onEvent(DirectoryClose(node))
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        val vector = when (node) {
            is ProjectNode.ClosedDirectoryNode -> Icons.FolderClosed
            is ProjectNode.OpenDirectoryNode -> Icons.FolderOpen
            is ProjectNode.FileNode -> when (node.type) {
                FileType.Kotlin -> Icons.FileKotlin
                FileType.Other -> Icons.FileOrdinary
            }
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            imageVector = vector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.size(20.dp)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = node.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}

@Preview
@Composable
private fun ProjectNodePreview() {
    val file = File("src/main/kotlin/com/aenadgrleey/kommander/menu/ui/Nodes.kt")
    Column {
        ProjectNode(
            node = ProjectNode.FileNode(
                file = file,
                name = file.name,
                type = FileType.Kotlin,
            ),
            onEvent = {}
        )

        ProjectNode(
            node = ProjectNode.OpenDirectoryNode(
                file = file,
                name = file.name,
                children = emptyList(),
            ),
            onEvent = {}
        )
    }
}
