package com.aenadgrleey.kommander.runner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.runner.model.Event

@Composable
fun RunnerOutput(
    output: List<AnnotatedString>,
    onEvent: (Event) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = 16.dp,
                end = 8.dp,
                bottom = 16.dp,
            )
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(output) {
            SelectionContainer {
                ClickableText(
                    it, modifier = Modifier,
                ) { offset ->
                    it.getStringAnnotations(offset, offset)
                        .firstOrNull()
                        ?.let { annotation ->
                            onEvent(
                                Event.TextLinkClick(
                                    tag = annotation.tag,
                                    annotation = annotation.item
                                )
                            )
                        }
                }
            }
        }
    }
}