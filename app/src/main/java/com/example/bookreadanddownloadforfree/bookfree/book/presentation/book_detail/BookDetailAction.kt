package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail

import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book


sealed  interface BookDetailAction {

    data object  OnBackClickDetail: BookDetailAction

    data object  OnFavoriteClickDetail: BookDetailAction

    data class OnSelectedBookChange(val book: Book): BookDetailAction

    data class  OnTabSelected(val index:Int): BookDetailAction

}