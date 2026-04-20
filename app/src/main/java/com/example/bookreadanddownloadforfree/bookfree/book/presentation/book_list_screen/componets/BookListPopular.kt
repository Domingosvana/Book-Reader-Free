package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book


@Composable
fun BookListPopular(
    book: List<Book>,
    onBookClickPopulares: (Book) -> Unit,

) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
            .padding(start = 32.dp, end = 32.dp)
    ) {
        items(book,
            key ={ "${it.id}_${it.languages.firstOrNull()}" } //
        ) { books ->
            BookListItemPopular(
                book = books,
                onClickPopularBook = { onBookClickPopulares(books) },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}


@Preview
@Composable
fun PreviewBookListPopular() {
    BookListPopular(
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
                source = "",
                colors = listOf()
            )
        ),
        onBookClickPopulares = {}
    )
}

//.height((150 + (book.title.length.coerceAtMost(30) * 2)).dp)
//val randomHeight = remember(book.id) { (150..230).random() }
//.height(randomHeight.dp)

/*
Sim, é exatamente isso! Essa estratégia chama-se Offline-First (ou Cache-as-Single-Source-of-Truth).

O seu código está seguindo esta lógica:

Pergunta ao Banco: "Tens os dados aí?"

Se SIM: Entrega o que está no banco direto para a UI.

Se NÃO (ou se estiverem velhos/expirados):

Vai buscar à Internet (API).

Pega no que veio da API e Salva no Banco.

Lê do Banco novamente e entrega para a UI
 */