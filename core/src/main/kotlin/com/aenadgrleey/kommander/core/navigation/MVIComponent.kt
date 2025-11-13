
package com.aenadgrleey.kommander.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class MVIComponent<State, Event>  {

    protected abstract val defaultState: State

    protected val mState by lazy { MutableStateFlow<State>(defaultState) }
    val state by lazy { mState.asStateFlow() }

    abstract fun dispatch(event: Event)

    private val mEffect = MutableSharedFlow<Effect>()
    val effect = mEffect.asSharedFlow()
    fun effect(effect: Effect) {
        coroutineScope.launch(Dispatchers.Main.immediate) { mEffect.emit(effect) }
    }

    @OptIn(ExperimentalStdlibApi::class)
    protected val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    protected fun coroutine(block: suspend CoroutineScope.() -> Unit) = coroutineScope.launch { block() }

    open fun onClear() {}
    fun clear() {
        onClear()
        coroutineScope.cancel()
    }
}

@Composable
fun <C> component(
    key: Any? = null,
    block: () -> C
): C where C : MVIComponent<*, *> {
    val component = remember(key) { block() }
    DisposableEffect(key) {
        val componentRef = component
        onDispose { componentRef.clear() }
    }
    return component
}

interface Effect
interface NoEvent
