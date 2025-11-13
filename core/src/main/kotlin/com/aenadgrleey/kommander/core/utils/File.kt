package com.aenadgrleey.kommander.core.utils

import okio.Path
import java.io.File

val File.isKotlinFile: Boolean
    get() = extension == "kts" || extension == "kt"

val Path.extension: String
    get() = this.name.substringAfterLast('.', "")

val Path.isKotlinFile: Boolean
    get() = this.extension == "kts" || this.extension == "kt"