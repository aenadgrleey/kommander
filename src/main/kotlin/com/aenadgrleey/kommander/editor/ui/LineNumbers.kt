package com.aenadgrleey.kommander.editor.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.TextFieldScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LineNumbers(
    lineCount: Int,
    textStyle: TextStyle,
    scrollState: TextFieldScrollState,
) {
    Column(
        modifier = Modifier.fillMaxHeight()
            .scrollContentWithTextField(scrollState)
            .wrapContentSize()
            .widthIn(min = 30.dp),
        horizontalAlignment = Alignment.End
    ) {
        Spacer(Modifier.height(4.dp))
        repeat(lineCount) { line ->
            Text(
                text = (line + 1).toString(),
                style = textStyle,
            )
        }
        Spacer(Modifier.height(4.dp))
    }
}