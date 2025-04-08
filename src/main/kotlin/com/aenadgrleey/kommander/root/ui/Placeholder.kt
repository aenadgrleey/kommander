package com.aenadgrleey.kommander.root.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aenadgrleey.kommander.core.navigation.PanesScope

@Composable
fun PanesScope.Placeholder(
    text: String,
) {
    Box(
        modifier = Modifier
            .fillPanesWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h2,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}