package com.aenadgrleey.kommander.root

import com.aenadgrleey.kommander.common.model.EditorCoordinates
import com.aenadgrleey.kommander.core.navigation.MVIComponent
import com.aenadgrleey.kommander.core.navigation.NoEvent
import com.aenadgrleey.kommander.core.navigation.PaneState
import com.aenadgrleey.kommander.core.utils.isKotlinFile
import com.aenadgrleey.kommander.editor.EditorComponent
import com.aenadgrleey.kommander.menu.MenuComponent
import com.aenadgrleey.kommander.menu.model.MenuEvent
import com.aenadgrleey.kommander.root.model.RootState
import com.aenadgrleey.kommander.runner.RunnerComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class RootComponent : MVIComponent<RootState, NoEvent>() {

    val menuComponent by childComponent(::MenuComponent)
    private var mEditorComponent = MutableStateFlow<EditorComponent?>(null)
    val editorComponent = mEditorComponent.asStateFlow()
    val runnerComponent by childComponent(::RunnerComponent)

    override val defaultState = RootState.Default
    override fun dispatch(event: NoEvent) = Unit

    fun menuRestate(paneState: PaneState) {
        mState.update { it.copy(menuPaneState = paneState) }
    }

    fun runnerRestate(paneState: PaneState) {
        mState.update { it.copy(runnerPaneState = paneState) }
    }

    fun initWithPath(path: String?) {
        val file = path
            ?.let(::File)
            ?.takeIf { it.exists() }
        when {
            file == null -> {
                mState.update {
                    it.copy(menuPaneState = it.menuPaneState.copy(visible = false))
                }
                menuComponent.dispatch(MenuEvent.ProjectClose)
            }

            file.isFile -> {
                mState.update {
                    it.copy(
                        menuPaneState = it.menuPaneState.copy(visible = false),
                        runnerPaneState = it.runnerPaneState.copy(widthDp = 400f)
                    )
                }
                menuComponent.dispatch(MenuEvent.ProjectOpen(file))
                openEditor(EditorCoordinates.Default, file)
            }

            file.isDirectory -> {
                mState.update {
                    it.copy(menuPaneState = it.menuPaneState.copy(visible = true))
                }
                menuComponent.dispatch(MenuEvent.ProjectOpen(file))

            }
        }
    }

    fun openEditor(coords: EditorCoordinates, file: File) {

        if (mEditorComponent.value?.file == file) {
            mEditorComponent.value?.forceCoordinates(coords)
            return
        }

        if (!file.isKotlinFile || !file.exists() || !file.isFile) {
            mEditorComponent.update { null }
            return
        }

        // here I had plans to implement caching of EditorComponents
        mEditorComponent.value?.clear()
        mEditorComponent.update { childComponent { EditorComponent(coords, file, it) }.value }
        mState.update { it.copy(editingFile = file.name) }
        runnerComponent.presetCommandForKotlinFile(file)
    }

    override fun onClear() {
        menuComponent.clear()
        runnerComponent.clear()
        mEditorComponent.value?.clear()
    }
}