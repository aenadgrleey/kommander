package com.aenadgrleey.kommander.core.compose

import androidx.compose.ui.Modifier

inline fun Modifier.conditional(
    condition: Boolean,
    block: () -> Modifier,
) = this.then(if (condition) block() else Modifier)