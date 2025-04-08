package com.aenadgrleey.kommander.core.navigation

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class CoroutineComponent(
    coroutineContext: CoroutineContext = EmptyCoroutineContext
) {
    @OptIn(ExperimentalStdlibApi::class)
    protected val coroutineScope = CoroutineScope(
        coroutineContext.let {
            if (it[CoroutineDispatcher.Key] != null) it else it + Dispatchers.Default
        } + SupervisorJob()
    )
    protected fun coroutine(block: suspend CoroutineScope.() -> Unit) = coroutineScope.launch { block() }

    fun <T : CoroutineComponent> CoroutineComponent.childComponent(
        create: (CoroutineContext) -> T
    ) = lazy { create(coroutineScope.coroutineContext) }


    open fun onClear() {}

    fun clear() {
        onClear()
        coroutineScope.cancel()
    }
}