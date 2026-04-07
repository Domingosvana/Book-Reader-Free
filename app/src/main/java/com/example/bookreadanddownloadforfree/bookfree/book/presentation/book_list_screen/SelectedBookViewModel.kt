package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import kotlinx.coroutines.flow.MutableStateFlow

class SelectedBookViewModel() : ViewModel() {
    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook = _selectedBook


    fun onSelectedBook(book: Book?){
        _selectedBook.value = book

    }

}