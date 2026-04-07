package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.components

import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.componets.ImagemError


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.PulseAnimation
import kotlin.math.round


@Composable
fun BookListItemFavorite(
    book: Book,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean= true
){
    val randomHeight = remember(book.id) { (150..195).random() }


    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            //   .bottomBorder(20.dp, color = Color(0xFFFFC107) ) // <--- Aplica aqui
            .fillMaxWidth()
            .drawBehind {
                val strokeWidthPx = 1.dp.toPx()
                val y = size.height - strokeWidthPx / 2

                // Define quanto de espaço vazio você quer nas pontas
                val margin = 60f

                drawLine(
                    color = Color(0xFFFFC107),
                    start = Offset(margin, y), // Começa após a margem
                    end = Offset(size.width - margin, y), // Termina antes da largura total
                    strokeWidth = strokeWidthPx
                )
            }
            .padding( bottom = 2.dp) ,
        color = MaterialTheme.colorScheme.background,


        //  border= BorderStroke(7.dp, MaterialTheme.colorScheme.outline),

    ) {
        Row(
            modifier = Modifier
                .padding(12.dp) // Espaçamento interno do card
                .height(IntrinsicSize.Min), // Faz a Row ter a altura da maior coluna
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            //   verticalAlignment = Alignment.Bottom

        ) {
            // --- 1. SEÇÃO DA CAPA ---
            Box(
                modifier = Modifier
                    .width(100.dp) // Largura fixa para manter o padrão
                    .height(150.dp), // Altura fixa para evitar saltos no layout
                contentAlignment = Alignment.Center
            ) {
                var imageLoadResult by remember { mutableStateOf<Result<Painter>?>(null) }

                val painter = rememberAsyncImagePainter(
                    model = book.coverUrl,
                    contentScale = ContentScale.Crop,
                    onSuccess = { imageLoadResult = Result.success(it.painter) },
                    onError = { imageLoadResult = Result.failure(it.result.throwable) }
                )

                val painterState by painter.state.collectAsStateWithLifecycle()
                val transition by animateFloatAsState(
                    targetValue = if (painterState is AsyncImagePainter.State.Success) 1f else 0f,
                    animationSpec = tween(durationMillis = 600)
                )

                when (val result = imageLoadResult) {
                    null -> PulseAnimation(modifier = Modifier.fillMaxSize())
                    else -> {
                        if (result.isSuccess) {
                            Image(
                                painter = painter,
                                contentDescription = book.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        alpha = transition
                                        scaleX = 0.9f + (0.1f * transition)
                                        scaleY = 0.9f + (0.1f * transition)
                                    }
                            )
                        } else {
                            ImagemError(book = book)
                        }
                    }
                }
            }

            // --- 2. SEÇÃO DE TEXTOS ---
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa todo o espaço central disponível
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Autores formatados sem os colchetes da lista [ ]
                Text(
                    text = book.authors.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Estrelas e Rating
                book.averageRating?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107), // Amarelo Gold
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = String.format("%.1f", rating),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f)) // Empurra os metadados para o fundo

                // Metadados (Ano, Idioma, etc)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    book.publishedYear?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    book.languages.firstOrNull()?.let { lang ->
                        Text(
                            text = lang.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    // Exemplo de Formato (usando uma string placeholder se estiver vazio)
                    /*   Text(
                           text = "PDF",
                           style = MaterialTheme.typography.labelSmall,
                           modifier = Modifier
                               .background(MaterialTheme.colorScheme.surfaceVariant)
                               .padding(horizontal = 6.dp, vertical = 2.dp)
                       )
                       */
                }
            }

            // --- 3. BOTÃO FAVORITO ---
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    tint = if (isFavorite)  Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                    contentDescription = null
                )
            }
        }
    }



/*
    Surface(
        //  shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            //.fillMaxSize()
            .clickable(onClick = onClick),
        color = Color.White
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(195.dp)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .width(130.dp)
                    // .height(195.dp)
                    .padding(8.dp),
                // contentAlignment = Alignment.Center
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
                                    .width(130.dp)
                                    .height(randomHeight.dp)
                                    .graphicsLayer {
                                        rotationX = (1f - transition) * 30f
                                        val scale = 0.8f + (0.2f * transition)
                                        scaleX = scale
                                        scaleY = scale
                                    }
                            )
                        } else {
                            // ✅ Exibe o cartão customizado de erro
                            ImagemError(book = book)
                        }
                    }
                }
            }



            Column(
                modifier = Modifier
                    .weight(1f)
                ,  //  .padding(12.dp),
                // verticalArrangement = Arrangement.Center
                //.fillMaxWidth()
                //  .weight(1f),
                // verticalArrangement = Arrangement.Center
                //  horizontalAlignment = Alignment.Start
                //  verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                book.authors.toString().let { authorName ->
                    Text(
                        text = authorName,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                book.averageRating?.let { rating ->
                    Row(modifier = Modifier,
                        // verticalAlignment = Alignment.CenterVertically,
                        // horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${round(rating * 10) / 10.0}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }
                //Spacer(Modifier.height(15.dp))
                Box(modifier = Modifier.weight(1f)){}
                Row() {
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.Bottom),
                        //verticalAlignment =
                    ){
                        book.publishedYear.toString()?.let {publishedYear->
                            Text(
                                text = publishedYear,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }




                    Spacer(Modifier.width(15.dp))

                    book.languages.toString().let { languages ->
                        Text(
                            text = languages,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines =1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(Modifier.width(15.dp))
                    book.title[0].toString().let {availableFormats ->
                        Text(
                            text = availableFormats,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    }

                }


            }
            Box(
                modifier= Modifier
                    //   .fillMaxSize()
                    .padding(top =8.dp,end = 8.dp),
                contentAlignment = Alignment.TopEnd

            ){
                // val isFavorite = isFavorite.contains(book.id)
                IconButton(

                    onClick = onFavoriteClick,
                    modifier = Modifier
                        //   .align(Alignment.TopEnd)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Yellow, Color.Transparent
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
    */


}

































@Composable
@Preview
fun BookListItemSearchPreview(){
    BookListItemFavorite(
        book = Book(
            id = "/works/OL1234567W",
            title = "Dom Casmurro",
            authors = listOf("Machado de Assis"),
            publishedYear = 2023.toString(),
            coverUrl = "",
            description = "Romance que narra...",
            downloadUrl = "",
            languages = listOf("pt-BR"),
            averageRating = 23.34,
            ratingsCount = 34,
            //numPages = 234,
            numEditions = 234,
            publisher = "",
            format = "",
            source = ""
        ),
        onClick = {},
        onFavoriteClick = {},
        isFavorite =false
    )
}


// Livro completamente disponível offline
