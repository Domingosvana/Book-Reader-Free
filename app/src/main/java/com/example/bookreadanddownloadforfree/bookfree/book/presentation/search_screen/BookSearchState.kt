package com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen

import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.UiText



data class BookSearchState(
    val searchQuery: String = "",
    val searchResults: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val favoriteIds: Set<String> = emptySet(),
   // val isFavorite: Set<String> = emptySet(),
    val errorMessageSearch: UiText? = null,
    val book: Book? = null
)

data class BookFilterState(
    val searchQuery: String = "",
    val searchResults: List<Book> = emptyList(),
    //val isLoading: Boolean = false
    var selectedYearDeMax: Int? = null,
    var selectedYearDeMin: Int? = null,
    var selectedLanguages: Set<String> = emptySet(),
    var selectedExtensao: Set<String> = emptySet(),

)



/*
data class BookSearchState(

    val searchQuery: String,

    val searchResults: List<Book> = emptyList(),

    val isLoading: Boolean= true,

    val isFavorite: Boolean ,

    val errorMessageSearch: UiText? = null,
    val book: Book?,
)

 */
