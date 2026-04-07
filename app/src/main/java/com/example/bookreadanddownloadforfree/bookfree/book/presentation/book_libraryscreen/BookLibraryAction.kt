package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen

import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import androidx.compose.runtime.State
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailAction

sealed interface BookLibraryAction {

    data object OnClickFilter: BookLibraryAction

    data class OnclickBook(val book: Book): BookLibraryAction

    data class OnFavoriteClick(val book: Book): BookLibraryAction


    data class OnSelectedBookChange(val book:State<Book?>): BookLibraryAction

   // data class OnSelectedBookChange(val book: State<Book?>): BookDetailAction

}
