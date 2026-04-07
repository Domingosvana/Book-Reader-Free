package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.bookreadanddownloadforfree.bookfree.app.Route
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.BookRepository
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BookLibraryViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository
): ViewModel(){
    //private val bookId = savedStateHandle.toRoute<Route.BookLibrary>()



    //private  val bookId =

    //private val = _state= MutableStateFlow(BookLibraryState())

private var cachedBooks = emptyList<Book>()

private var observeFavoriteJob: Job? = null








    private val _state = MutableStateFlow(BookLibraryState())
    val state = _state








        .onStart {
            observeFavoriteBooks()

        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )


    init {
        observeRepositories()
    }

    private fun observeRepositories(){
        viewModelScope.launch{
            combine(
               // bookRepository.getFavoriteBookAll(),
                bookRepository.countTotalDeFavorite()

            ){totalDeFavoriteNew ->
                _state.update{
                    it.copy(
                        totalDeFavorite = totalDeFavoriteNew[0]
                    )
                }

            }.collect {  }
        }
    }












    fun onAction(action: BookLibraryAction){

        when(action){

            is BookLibraryAction.OnclickBook -> {}

            is BookLibraryAction.OnSelectedBookChange -> {
                _state.update {
                    it.copy(book = action.book.value )
                }
            }



            is BookLibraryAction.OnFavoriteClick -> {

                viewModelScope.launch{
                    val book = action.book
                    val currentLang = book.languages.firstOrNull() ?: "pt"
                    if(state.value.favorites.any{ it.id == book.id  && it.languages.contains(currentLang) }) {
                        bookRepository.deleteFavoriteBook(book.id, language = currentLang)

                        }

                    else{
                            bookRepository.markAsFavorite(book)
                    }
                }
            }

            else -> Unit
        }


    }





    private fun observeFavoriteBooks(){
        observeFavoriteJob?.cancel()
        observeFavoriteJob = viewModelScope.launch {
            bookRepository.getFavoriteBookAll()
                .onEach { favoriteBooks ->
                    _state.update { it.copy(
                        favorites = favoriteBooks
                    ) }

                }
                .launchIn(viewModelScope)


        }

    }













}