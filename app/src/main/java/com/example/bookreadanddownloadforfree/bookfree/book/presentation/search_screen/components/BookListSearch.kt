package com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book


@Composable
fun BookListSearch(
    book: List<Book>,
    onBookClick: (Book) -> Unit,
    FavoriteClick: (Book) -> Unit,
    isFavorite: Set<String>,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            top = 0.dp,
            start = 8.dp,
            end = 8.dp,
            bottom = 80.dp // Aumente este valor se tiver uma BottomBar grande
        ),
        //verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        itemsIndexed(
            items = book,
            // ✅ Crucial: A chave do item na lista deve ser ID + Idioma
            key = { _, item -> "${item.id}_${item.languages.firstOrNull() ?: ""}" }
        ) { _, currentBook ->

            // ✅ Criamos a chave para este card específico
            val currentBookKey = "${currentBook.id}_${currentBook.languages.firstOrNull() ?: ""}"

            BookListItemNewSearch(
                book = currentBook,
                onClick = { onBookClick(currentBook) },
                onFavoriteClick = { FavoriteClick(currentBook) },
                // ✅ Agora ele só fica vermelho se o ID + Idioma baterem!
                isFavorite = isFavorite.contains(currentBookKey)
            )
        }

        /*
        itemsIndexed(
            // ✅ CHAVE COMPOSTA: ID + Idioma garante que seja única
            key = { _, book -> "${book.id}_${book.languages.firstOrNull() ?: "unknown"}" },
            items = book
        ) { index, books ->


            BookListItemNewSearch(
                book = books,
                onClick = { onBookClick(books) },
                onFavoriteClick = { FavoriteClick(books) },
                // ✅ AJUSTE NO FAVORITO (Veja explicação abaixo)
                isFavorite = isFavorite.contains(books.id)
            )

            if (index < book.lastIndex) {
                Spacer(Modifier.height(1.dp))
            }
        }

        */
    }

}


















@Composable
@Preview(showBackground = true)
fun BookListPreview() {
    BookListSearch(
        book = listOf(
            Book(
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
               // numPages = 234,
                numEditions = 234,
                publisher = "",
                format = "",
                source = ""
            )
            ),

        onBookClick = {},
        modifier = Modifier,
        scrollState = rememberLazyListState(),
        FavoriteClick = {},
        isFavorite =  setOfNotNull(),
    )


}