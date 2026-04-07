package com.example.zlib.bookpedia.book.presentation.book_detail.components






import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.PulseAnimation
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_dark_tertiaryContainer
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_light_background
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_light_scrim

@Composable
fun BlurredImageBackground(
    coverUrl: String?,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var imageLoadResult by remember {
        mutableStateOf<Result<Painter>?>(null)
    }
    val painter = rememberAsyncImagePainter(
        model = coverUrl,
        onSuccess = {
            val size = it.painter.intrinsicSize
            imageLoadResult = if(size.width > 1 && size.height > 1) {
                Result.success(it.painter)
            } else {
                Result.failure(Exception("Invalid image dimensions"))
            }
        },
        onError = {
            it.result.throwable.printStackTrace()
        }
    )

    Box(modifier = modifier) {
        // CAMADA 1: O FUNDO (Acompanha o tamanho do conteúdo)
        Column(modifier = Modifier.matchParentSize()) {
            // Parte superior borrada (Altura fixa para o efeito visual)
            Box(
                modifier = Modifier
                    .height(280.dp)
                    .fillMaxWidth()
                    .background(md_theme_dark_tertiaryContainer)
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
            // Parte inferior (Cor de fundo do app que estica com o texto)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(md_theme_light_background)
            )
        }

        // CAMADA 2: O CONTEÚDO (Capa + Título + Info)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Espaço fixo para posicionar a capa no meio do blur
            Spacer(modifier = Modifier.height(40.dp))

            ElevatedCard(
                modifier = Modifier
                    .height(230.dp)
                    .aspectRatio(2 / 3f),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 15.dp
                )
            ) {
                AnimatedContent(
                    targetState = imageLoadResult,
                    label = "ImageLoad"
                ) { result ->
                    when(result) {
                        null -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            PulseAnimation(modifier = Modifier.size(60.dp))
                        }
                        else -> {
                            Box {
                                Image(
                                    painter = if(result.isSuccess) painter else {
                                        painterResource(R.drawable.book_error_2)
                                    },
                                    contentDescription = stringResource(R.string.book_cover),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent),
                                    contentScale = if(result.isSuccess) ContentScale.Crop else ContentScale.Fit
                                )
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
                                        imageVector = if(isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        tint = Color.Red,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Aqui entra o Título, Autores e Informações que você passar na Screen
            content()
        }
    }
}

/*
@Composable
fun BlurredImageBackground(
    coverUrl: String?,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
   // onBackClicks: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
   // onColorRetrieved: (Color) -> Unit = {} // Callback para a TopBar // Callback para avisar a tela sobre a cor extraída
) {
    val context = LocalContext.current
    var imageLoadResult by remember { mutableStateOf<Result<Painter>?>(null) }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(coverUrl)
            // ✅ Importante: Hardware Bitmaps não permitem extração de cores
            .allowHardware(false)
            .build(),
        onSuccess = { result ->
            val size = result.painter.intrinsicSize
            if (size.width > 1 && size.height > 1) {
                imageLoadResult = Result.success(result.painter)

                // 🎨 EXTRAÇÃO DE COR COM PALETTE API
                val drawable = result.result.image
                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    Palette.from(bitmap).generate { palette ->
                        // Tentamos pegar a cor Vibrante, se não houver, a Dominante
                        val extractedColor = palette?.vibrantSwatch?.rgb
                            ?: palette?.dominantSwatch?.rgb

                        extractedColor?.let { colorInt ->
                            onColorRetrieved(Color(colorInt))
                        }
                    }
                }
            } else {
                imageLoadResult = Result.failure(Exception("Invalid dimensions"))
            }
        },
        onError = {
            it.result.throwable.printStackTrace()
        }
    )

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .background(md_theme_dark_tertiaryContainer)
            ) {
                Image(
                    painter = painter,
                    contentDescription = stringResource(R.string.book_cover),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth()
                    .background(md_theme_light_background)
            )
        }
/*
        IconButton(
            onClick = onBackClicks,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.go_back),
                tint = Color.White
            )
        }

 */

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.15f))
            ElevatedCard(
                modifier = Modifier
                    .height(230.dp)
                    .aspectRatio(2 / 3f),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 15.dp
                )
            ) {
                AnimatedContent(
                    targetState = imageLoadResult
                ) { result ->
                    when(result) {
                        null -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            PulseAnimation(
                                modifier = Modifier
                                    .size(60.dp)
                            )
                        }
                        else -> {
                            Box {
                                Image(
                                    painter = if(result.isSuccess) painter else {
                                        painterResource(R.drawable.book_error_2)
                                    },
                                    contentDescription = stringResource(R.string.book_cover),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent),
                                    contentScale = if(result.isSuccess) {
                                        ContentScale.Crop
                                    } else {
                                        ContentScale.Fit
                                    }
                                )
                                IconButton(
                                    onClick = onFavoriteClick,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    md_theme_light_scrim, Color.Transparent
                                                ),
                                                radius = 70f
                                            )
                                        )
                                ) {
                                    Icon(
                                        imageVector = if(isFavorite) {
                                            Icons.Filled.Favorite
                                        } else {
                                            Icons.Outlined.FavoriteBorder
                                        },
                                        tint = Color.Red,
                                        contentDescription = if(isFavorite) {
                                            stringResource(R.string.remove_from_favorites)
                                        } else {
                                            stringResource(R.string.mark_as_favorite)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            content()
        }
    }
}

@Preview
@Composable
private fun BlurredImageBackgroundpreview() {
    BlurredImageBackground(
        coverUrl = "image",
        isFavorite = true,
        onFavoriteClick = {},
        modifier = Modifier,
        content ={}
    )

}

 */