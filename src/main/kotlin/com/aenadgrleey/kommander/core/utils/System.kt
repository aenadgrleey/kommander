package com.aenadgrleey.kommander.core.utils

import java.io.File

val isWindows by lazy { System.getProperty("os.name").startsWith("Windows") }

val shell by lazy {
    if (isWindows) listOf("cmd.exe", "/c") else listOf("bash", "-c")
}

val parentDir by lazy { File(System.getProperty("user.dir")) }


