package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail

import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book

data class BookDetailState(

    val isLanding: Boolean = true,

    val isFavorite: Boolean = false,

    val book: Book? = null,
    val selectedTabIndex: Int = 0,

    val liberos: Book? = null,

    val description:Book? = null,


    )
