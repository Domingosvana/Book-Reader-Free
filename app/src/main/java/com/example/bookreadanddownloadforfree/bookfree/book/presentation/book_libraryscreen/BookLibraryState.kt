package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen

import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.UiText


data class  BookLibraryState(
    val favorites: List<Book> = emptyList(),
    val isLanding: Boolean = false,
    val selectedTabIndex: Int = 0,
    val errorMessages: UiText? = null,
    val isFavorite: Boolean= true,
    val book: Book? = null,
    val totalDeFavorite: Int = 0,
    //val totalDeDownload:Int = 0

)
/* {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookLibraryState

        if (isLanding != other.isLanding) return false
        if (selectedTabIndex != other.selectedTabIndex) return false
        if (isFavorite != other.isFavorite) return false
        if (favorites != other.favorites) return false
        if (errorMessages != other.errorMessages) return false
        if (book != other.book) return false
        if (!totalDeFavorite.contentEquals(other.totalDeFavorite)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLanding.hashCode()
        result = 31 * result + selectedTabIndex
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + favorites.hashCode()
        result = 31 * result + (errorMessages?.hashCode() ?: 0)
        result = 31 * result + (book?.hashCode() ?: 0)
        result = 31 * result + totalDeFavorite.contentHashCode()
        return result
    }
}*/


