package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.componets




import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.bookGradients
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book

@Composable
fun ImagemError(
    book: Book,
    modifier: Modifier = Modifier
) {
    // Gera cor sempre baseada no ID (não depende de nada salvo)
    val gradientColors = remember(book.id) {
        val index = Math.abs(book.id.hashCode()) % bookGradients.size
        bookGradients[index].map {
            Color(it.toLong() or 0xFF000000L)
        }
    }

    val randomHeight = remember(book.id) { (120..200).random() }
    Card(
        modifier = modifier
            .size(width = 130.dp, height = randomHeight.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors)) // ✅ A cor aparece aqui
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = book.title,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(Color.White.copy(alpha = 0.5f))
                )

                Text(
                    text = book.authors.firstOrNull() ?: "Autor desconhecido",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}





@Preview
@Composable
fun ImagemErrorPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ImagemError(
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
                // numPages = 234,
                numEditions = 234,
                publisher = "",
                format = "",
                source = "",
                colors = listOf()
            )
        )
        ImagemError(
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
                // numPages = 234,
                numEditions = 234,
                publisher = "",
                format = "",
                source = "",
                colors = listOf()
            )
        )
    }
}









@Composable
@Preview
fun ImamErrorPreview(){
    ImagemError(
        book  =
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
        )


}


@Preview(showBackground = true)
@Composable
fun ImagemErrorTest() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ImagemError(
            book = Book(
                id = "livro1",
                title = "Dom Casmurro",
                authors = listOf("Machado de Assis"),
                coverUrl = "",
                languages = listOf("pt"),
                source = "",
                colors = listOf() // não importa mais
            )
        )

        ImagemError(
            book = Book(
                id = "livro2",
                title = "O Pequeno Príncipe",
                authors = listOf("Antoine de Saint-Exupéry"),
                coverUrl = "",
                languages = listOf("pt"),
                source = "",
                colors = listOf()
            )
        )
    }
}