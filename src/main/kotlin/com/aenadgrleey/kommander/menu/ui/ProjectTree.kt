package com.aenadgrleey.kommander.menu.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.common.model.FileType
import com.aenadgrleey.kommander.core.theme.ThemedPreview
import com.aenadgrleey.kommander.menu.model.MenuEvent
import com.aenadgrleey.kommander.menu.model.ProjectNode
import java.io.File

@Composable
fun ProjectTree(
    nodes: List<ProjectNode>,
    onEvent: (MenuEvent) -> Unit
) {
    LazyColumn { nodes(nodes = nodes, depth = 0, onEvent = onEvent) }
}

fun LazyListScope.nodes(
    nodes: List<ProjectNode>,
    depth: Int,
    onEvent: (MenuEvent) -> Unit
) {
    nodes.forEach { node -> node(node, depth, onEvent) }
}

fun LazyListScope.node(
    node: ProjectNode,
    depth: Int,
    onEvent: (MenuEvent) -> Unit
) {
    val modifier = Modifier.padding(start = 8.dp * depth, bottom = 2.dp)
    when (node) {
        is ProjectNode.FileNode -> item(
            key = node.file.absolutePath,
            contentType = "file node depth of $depth"
        ) {
            ProjectNode(
                node = node,
                modifier = modifier,
                onEvent = onEvent
            )
        }

        is ProjectNode.ClosedDirectoryNode -> {
            item(
                key = node.file.absolutePath,
                contentType = "closed directory node depth of $depth"
            ) {
                ProjectNode(
                    node = node,
                    modifier = modifier,
                    onEvent = onEvent
                )
            }
        }

        is ProjectNode.OpenDirectoryNode -> {
            item(
                key = node.file.absolutePath,
                contentType = "open directory node depth of $depth"
            ) {
                ProjectNode(
                    node = node,
                    modifier = modifier,
                    onEvent = onEvent
                )
            }
            nodes(node.children, depth + 1, onEvent)
        }
    }
}

@Preview
@Composable
fun ProjectTreePreview() {
    ThemedPreview(hasBackground = true) {

        ProjectTree(
            nodes = listOf(
                ProjectNode.FileNode(
                    name = "File.kt",
                    file = File("File.kt"),
                    type = FileType.Kotlin
                ),
                ProjectNode.ClosedDirectoryNode(
                    name = "Folder",
                    file = File("Folder1")
                ),
                ProjectNode.OpenDirectoryNode(
                    name = "Folder",
                    file = File("Folder2"),
                    children = listOf(
                        ProjectNode.FileNode(
                            name = "File.kt",
                            file = File("Folder2/File.kt"),
                            type = FileType.Kotlin
                        )
                    )
                )
            ),
            {}
        )
    }
}
