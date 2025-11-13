package com.aenadgrleey.kommander.core.utils

import okio.FileSystem
import okio.Path.Companion.toPath

val isWindows by lazy { System.getProperty("os.name").startsWith("Windows") }

val shell by lazy {
    if (isWindows) listOf("cmd.exe", "/c") else listOf("bash", "-c")
}

val parentDir by lazy { System.getProperty("user.dir").toPath() }

fun FileSystem.isEmptyDir(path: okio.Path): Boolean {
    val source = this.list(path)
    return !source.iterator().hasNext()
}

fun FileSystem.firstKotlinFile(dir: okio.Path): okio.Path? {
    val source = this.list(dir)
    val iterator = source.iterator()
    while (iterator.hasNext()) {
        val next = iterator.next()
        val metadata = this.metadataOrNull(next)
        if (metadata != null && metadata.isRegularFile && next.isKotlinFile) {
            return next
        }
    }
    return null
}


