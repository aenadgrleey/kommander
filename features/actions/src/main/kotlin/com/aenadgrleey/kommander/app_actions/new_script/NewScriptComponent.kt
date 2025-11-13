package com.aenadgrleey.kommander.app_actions.new_script

import com.aenadgrleey.kommander.app_actions.new_script.model.Event
import com.aenadgrleey.kommander.app_actions.new_script.model.State
import com.aenadgrleey.kommander.core.navigation.MVIComponent
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet


class NewScriptDialogComponent(
) : MVIComponent<State, Event>() {

    override val defaultState = State.NotVisible

    override fun dispatch(event: Event) {
        when (event) {
            Event.Dismiss -> mState.update { State.NotVisible }
            is Event.UpdateDir -> mState.updateAndGet {
                (it as? State.Visible)?.copy(
                    dirPath = event.path,
                    dirError = checkDir(event.path)
                ) ?: it
            }
            is Event.UpdateName -> mState.update {
                (it as? State.Visible)?.copy(
                    dirPath = event.name,
                    dirError = checkName(event.name)
                ) ?: it
            }

            is Event.Create -> {

            }
        }
    }

    private fun checkDir(path: String): String {
        TODO()
    }

    private fun checkName(name: String): String {
        TODO()
    }



}
