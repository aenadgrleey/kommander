package com.aenadgrleey.kommander.core.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val FileKotlin: ImageVector
    get() {
        if (_FileKotlin != null) {
            return _FileKotlin!!
        }
        _FileKotlin = ImageVector.Builder(
            name = "FileKotlin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(11.59f, 3f)
                lineTo(3f, 11.59f)
                verticalLineTo(4f)
                curveToRelative(0f, -0.552f, 0.448f, -1f, 1f, -1f)
                horizontalLineTo(11.59f)
                close()
                moveTo(18.88f, 4.71f)
                lineTo(9.58f, 14f)
                lineToRelative(-6.46f, 6.46f)
                curveTo(3.04f, 20.32f, 3f, 20.17f, 3f, 20f)
                verticalLineToRelative(-5.59f)
                lineTo(14.41f, 3f)
                horizontalLineToRelative(3.76f)
                curveTo(19.07f, 3f, 19.51f, 4.08f, 18.88f, 4.71f)
                close()
                moveTo(18.586f, 21f)
                horizontalLineTo(5.42f)
                lineToRelative(7.785f, -7.795f)
                lineToRelative(6.087f, 6.087f)
                curveTo(19.923f, 19.923f, 19.477f, 21f, 18.586f, 21f)
                close()
            }
        }.build()

        return _FileKotlin!!
    }

@Suppress("ObjectPropertyName")
private var _FileKotlin: ImageVector? = null
