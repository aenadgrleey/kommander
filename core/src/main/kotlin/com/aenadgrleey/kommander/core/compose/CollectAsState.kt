package com.aenadgrleey.kommander.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

@Composable
fun <T, R> StateFlow<T>.collectAsState(
    transform: (T) -> R,
) = map { transform(it) }.collectAsState(transform(value))