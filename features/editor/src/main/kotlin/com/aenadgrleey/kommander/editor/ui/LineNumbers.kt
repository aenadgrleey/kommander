package com.aenadgrleey.kommander.editor.ui

import RobotoMonoFontFamily
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.aenadgrleey.kommander.core.compose.remember

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LineNumbers(
    lineHeight: TextUnit,
    lineHeightStyle: LineHeightStyle?,
    layoutResult: TextLayoutResult?
) {
    if (layoutResult != null) {
        Layout(
            content = {
                repeat(layoutResult.lineCount) { line ->
                    Text(
                        text = (line + 1).toString(),
                        lineHeight = lineHeight,
                        fontSize = 14.sp,
                        fontFamily = RobotoMonoFontFamily,
                        style = LocalTextStyle.current.remember {
                            it.copy(lineHeightStyle = lineHeightStyle)
                        }
                    )
                }
            }
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }

            val width = placeables.maxOfOrNull { it.width } ?: 0
            val height = layoutResult.size.height

            layout(width, height) {
                placeables.forEachIndexed { index, placeable ->
                    val y = layoutResult
                        .getLineTop(index).toInt()
                        .coerceAtLeast(0)
                    placeable.placeRelative(width - placeable.width, y)
                }
            }
        }
    }
}