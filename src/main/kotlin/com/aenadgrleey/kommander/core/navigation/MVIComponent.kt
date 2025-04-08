
package com.aenadgrleey.kommander.core.navigation

import com.aenadgrleey.kommander.core.utils.MainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class MVIComponent<State, Event>(
    coroutineContext: CoroutineContext = EmptyCoroutineContext
) : CoroutineComponent(coroutineContext) {

    protected abstract val defaultState: State

    protected val mState by lazy { MutableStateFlow<State>(defaultState) }
    val state by lazy { mState.asStateFlow() }

    @MainThread
    abstract fun dispatch(event: Event)

    private val mEffect = MutableSharedFlow<Effect>()
    val effect = mEffect.asSharedFlow()
    fun effect(effect: Effect) {
        coroutineScope.launch(Dispatchers.Main.immediate) { mEffect.emit(effect) }
    }
}

interface Effect
interface NoEvent
