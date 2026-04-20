

package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.componets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.PulseAnimation

@Composable
fun BookListItemPopular(
    book: Book,
    onClickPopularBook: () -> Unit,
    modifier: Modifier = Modifier
) {

    val randomHeight = remember(book.id) { (150..230).random() }




    Surface(
        modifier = modifier.clickable(onClick = onClickPopularBook)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .width(130.dp)
                // ❌ REMOVIDO height fixo
                , // .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            var imageLoadResult by remember { mutableStateOf<Result<Painter>?>(null) }

            val painter = rememberAsyncImagePainter(
                model = book.coverUrl,
                contentScale = ContentScale.Crop,
                onSuccess = { result ->
                    imageLoadResult = Result.success(result.painter)
                },
                onError = { error ->
                    imageLoadResult = Result.failure(error.result.throwable)
                }
            )

            val painterState by painter.state.collectAsStateWithLifecycle()
            val transition by animateFloatAsState(
                targetValue = if (painterState is AsyncImagePainter.State.Success) 1f else 0f,
                animationSpec = tween(durationMillis = 800)
            )

            when (val result = imageLoadResult) {
                null -> PulseAnimation(modifier = Modifier.width(60.dp).height(90.dp))
                else -> {
                    if (result.isSuccess) {
                        Image(
                            painter = painter,
                            contentDescription = book.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.background)
                                .width(130.dp)
                                .graphicsLayer {
                                    rotationX = (1f - transition) * 30f
                                    val scale = 0.8f + (0.2f * transition)
                                    scaleX = scale
                                    scaleY = scale
                                }
                                // 🔹 Altura variável (efeito irregular)
                                .height(randomHeight.dp)
                        )
                    } else {
                        ImagemError(book = book)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BookListItemPopularPreview() {
    BookListItemPopular(
        book = Book(
            id = "/works/OL1234567W",
            title = "Dom Casmurro",
            authors = listOf("Machado de Assis"),
            publishedYear = "2023",
            coverUrl = "", // Sem imagem para forçar o erro
            description = "Romance que narra...",
            downloadUrl = "",
            languages = listOf("pt-BR"),
            averageRating = 4.5,
            ratingsCount = 34,
            numEditions = 234,
            publisher = "",
            format = "",
            source = "",
            colors = listOf()
        ),
        onClickPopularBook = {},
        modifier = Modifier
    )
}
