package com.aenadgrleey.kommander.editor.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

val AxisLockNestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (available == Offset.Zero) return Offset.Zero

        val dx = available.x
        val dy = available.y

        return if (abs(dx) > abs(dy)) {
            Offset(0f, dy)
        } else {
            Offset(dx, 0f)
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val vx = available.x
        val vy = available.y

        return if (abs(vx) > abs(vy)) {
            Velocity(0f, vy)
        } else {
            Velocity(vx, 0f)
        }
    }
}