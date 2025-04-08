package com.aenadgrleey.kommander.core.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

typealias ComposableContent = @Composable () -> Unit
typealias RowContent = @Composable RowScope.() -> Unit
typealias BoxContent = @Composable BoxScope.() -> Unit