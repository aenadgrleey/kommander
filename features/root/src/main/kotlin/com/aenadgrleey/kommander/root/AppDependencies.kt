package com.aenadgrleey.kommander.root

import androidx.compose.runtime.Immutable
import com.aenadgrleey.kommander.lsp.LspProcessor
import okio.FileSystem

@Immutable
data class AppDependencies(
    val lsp: LspProcessor,
    val fs: FileSystem
)