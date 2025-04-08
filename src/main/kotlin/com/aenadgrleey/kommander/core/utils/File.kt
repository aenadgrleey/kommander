package com.aenadgrleey.kommander.core.utils

import java.io.File

val File.isKotlinFile: Boolean
    get() = extension == "kts" || extension == "kt"