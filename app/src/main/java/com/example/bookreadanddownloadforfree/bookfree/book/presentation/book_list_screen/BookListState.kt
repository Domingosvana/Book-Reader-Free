package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen

import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.UiText

data class BookListState(
    //val searchQuery: String,
   // val searchResults: List<Book> = emptyList(),
    val favorites: List<Book> = emptyList(),
    val isloading: Boolean = true,
    val selectedTabIndex: Int = 0,
    val errorMessagens: UiText? = null,
    val getPopularBooks: List<Book> = emptyList() ,
    val getRecommendedBooks: List<Book> = emptyList()


    )
private val bookse = (1..100).map{
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
}

//https://openlibrary.org/search.json?q=bestsellers