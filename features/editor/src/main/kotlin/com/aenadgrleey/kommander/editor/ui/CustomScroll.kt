@file:OptIn(ExperimentalFoundationApi::class)

package com.aenadgrleey.kommander.editor.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.TextFieldScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import kotlin.math.min
import kotlin.math.roundToInt

fun Modifier.scrollContentWithTextField(
    scrollState: TextFieldScrollState
): Modifier = object : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints.copy(maxHeight = Constraints.Infinity))
        val visibleHeight = constraints.maxHeight
        val actualHeight = min(placeable.height, visibleHeight)

        return layout(placeable.width, actualHeight) {
            val offsetY = -scrollState.offset.roundToInt()
            placeable.placeRelative(0, offsetY)
        }
    }
}



