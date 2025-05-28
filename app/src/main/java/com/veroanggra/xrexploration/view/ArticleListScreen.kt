package com.veroanggra.xrexploration.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.veroanggra.xrexploration.data.Articles

@Composable
fun ArticleListScreen(
    articles: List<Articles>,
    onArticleClick: (Articles) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
            }
        }
    }) {
        items(articles) { article ->
            ArticleCardtItem(
                article = article,
                onClick = { onArticleClick(article) }
            )
        }
    }
}