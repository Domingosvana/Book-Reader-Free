package com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen

import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailAction

interface BookSearchAction {

    data class OnSearchQueryChange(val query: String): BookSearchAction

    data class OnBookClick(val book: Book): BookSearchAction

    data object  OnButtonSearchBack: BookSearchAction

    data object OnNavigateFilterScreen: BookSearchAction


    data class OnFavoriteClickSearch(val book: Book): BookSearchAction

}

interface BookFilterAction {

        data class onYearSelectedMax(var year: Int?): BookFilterAction

        data class onYearSelectedMin(var year: Int?): BookFilterAction

        data class onLanguagesChanged(var languages: Set<String>): BookFilterAction

        data class onExtensaoChanged(var extensao: Set<String>): BookFilterAction

       data object onButtonFilterBack: BookFilterAction


      data object Clear : BookFilterAction

    data object OnNavigateFilterBack:BookFilterAction






}













