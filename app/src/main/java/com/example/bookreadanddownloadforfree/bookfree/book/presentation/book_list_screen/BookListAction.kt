package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen

import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book


sealed interface BookListAction{

    data class  OnSearchQueryChange(val query:String): BookListAction

    data class OnBookClick(val book: Book): BookListAction

    data object OnClickNavigates: BookListAction

    data class  OnTabSelected(val index:Int): BookListAction

    data object UpdategetRecommendedBooks: BookListAction


}
