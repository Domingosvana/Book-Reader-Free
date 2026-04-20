package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterTiltShift
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.ktor.websocket.Frame


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreenTopBar(
    title:String = "Biblioteca"
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(), // ocupa toda a largura do TopAppBar
                contentAlignment = Alignment.Center // centraliza o conteúdo no meio
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center
                )
            }
        },

    )
}











@Composable
@Preview
fun LibraryScreenTopBarPreview(){
    LibraryScreenTopBar()
}


