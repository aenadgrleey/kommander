package com.aenadgrleey.kommander.root.model

import com.aenadgrleey.kommander.core.navigation.Effect
import com.aenadgrleey.kommander.core.text.TextPosition
import okio.Path

class OpenFileEffect(
    val path: Path,
    val position: TextPosition = TextPosition.Undefined,
) : Effect