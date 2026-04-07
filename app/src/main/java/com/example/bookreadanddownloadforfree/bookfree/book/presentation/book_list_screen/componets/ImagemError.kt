package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.componets


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.ui.theme.gradient3


@Composable
fun ImagemError(
    book: Book
){
    val randomHeight = remember(book.id) { (150..230).random() }




    Card(
            Modifier
                .background(
                    //  shape = RoundedCornerShape(100),
                    brush = Brush.linearGradient(gradient3)

                )
                .size(width = 130.dp
                , height= randomHeight.dp
            )

            ,

        ){
            Column(
                modifier = Modifier
                    .background(
                        //  shape = RoundedCornerShape(100),
                        brush = Brush.linearGradient(gradient3)

                    )
                    .fillMaxSize()
                    .fillMaxWidth()
                    .padding(6.dp)
            ){
                Box(modifier = Modifier
                    //.fillMaxSize()
                    //.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                //    .weight(1f)
                        )
                {
                    book.title.let { title ->
                        Text(
                            title, Modifier,
                            maxLines = 2, textAlign = TextAlign.Center


                        )
                    }
                }

                Spacer(Modifier.height(130.dp))

                Box(modifier = Modifier
                    //.fillMaxSize()
                    //.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    //    .weight(1f)
                )
                {
                    book.title.let { authors ->
                        Text(
                            authors, Modifier,
                            maxLines = 2, textAlign = TextAlign.Center


                        )
                    }
                }








        }


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
                source = ""
            )
        )


}


