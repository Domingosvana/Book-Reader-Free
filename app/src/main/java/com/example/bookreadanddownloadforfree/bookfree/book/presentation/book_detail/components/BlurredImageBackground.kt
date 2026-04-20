package com.example.zlib.bookpedia.book.presentation.book_detail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.componets.ImagemError
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.PulseAnimation
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_dark_onBackground
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_light_scrim

@Composable
fun BlurredImageBackground(
    book: Book?, // Adicionado para poder passar os dados para a ImagemError
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var imageLoadResult by remember {
        mutableStateOf<Result<Painter>?>(null)
    }
    val painter = rememberAsyncImagePainter(
        model = book?.coverUrl,
        onSuccess = {
            val size = it.painter.intrinsicSize
            imageLoadResult = if(size.width > 1 && size.height > 1) {
                Result.success(it.painter)
            } else {
                Result.failure(Exception("Invalid image dimensions"))
            }
        },
        onError = {
            imageLoadResult = Result.failure(it.result.throwable)
        }
    )

    Box(modifier = modifier) {
        // CAMADA 1: O FUNDO
        Column(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .height(280.dp)
                    .fillMaxWidth()
                    .background(md_theme_dark_onBackground)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        // CAMADA 2: O CONTEÚDO
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            ElevatedCard(
                modifier = Modifier
                    .height(220.dp)
                    .aspectRatio(2 / 3f),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 15.dp)
            ) {
                AnimatedContent(
                    targetState = imageLoadResult,
                    label = "ImageLoad"
                ) { result ->
                    when {
                        result == null -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                PulseAnimation(modifier = Modifier.size(60.dp))
                            }
                        }
                        result.isSuccess -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painter,
                                    contentDescription = stringResource(R.string.book_cover),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                FavoriteButtonOverlay(isFavorite, onFavoriteClick)
                            }
                        }
                        else -> {
                            // AQUI: Substituição da imagem de erro pelo seu componente ImagemError
                            Box(modifier = Modifier.fillMaxSize()) {
                                book?.let {
                                    ImagemError(
                                        book = it,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                FavoriteButtonOverlay(isFavorite, onFavoriteClick)
                            }
                        }
                    }
                }
            }
            content()
        }
    }
}

@Composable
private fun FavoriteButtonOverlay(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(md_theme_light_scrim, Color.Transparent),
                        radius = 70f
                    )
                )
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                tint = if (isFavorite) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surfaceBright,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun BlurredImageBackgroundpreview() {
    BlurredImageBackground(
        book = null,
        isFavorite = true,
        onFavoriteClick = {},
        modifier = Modifier,
        content = {}
    )
}