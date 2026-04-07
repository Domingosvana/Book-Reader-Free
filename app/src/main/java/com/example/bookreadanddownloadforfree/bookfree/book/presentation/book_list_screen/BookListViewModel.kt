package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import androidx.room.util.query
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.BookRepository
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.core.domian.onError
import com.example.bookreadanddownloadforfree.bookfree.core.domian.onSuccess
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.toUiText
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.compareTo

class BookListViewModel(
    private val bookRepository: BookRepository
): ViewModel() {

   // private var cachedBooks = emptyList<Book>()

   // private var searchJob: Job? = null


    private val _state = MutableStateFlow(BookListState(

    ))
    val state = _state
        //val state = _state.onStart { ... }.stateIn(...)
        //
        //Esse é o fluxo que você expõe publicamente para a UI observar.
        //
        //O onStart é chamado quando alguém começa a coletar o estado,
        //ou seja, quando a tela é aberta pela primeira vez.
        //
        //Dentro dele:
        //.onStart {
       //     if (cachedBooks.isEmpty()){
        ///        observeSearchQuery()
         //   }

//}
        .stateIn(
            //SharingStarted.WhileSubscribed(5000L) quer dizer:
            //“se ninguém estiver observando por até 5 segundos, pausa o fluxo”
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value // valor inicial
        )
        //.stateIn(
          //  viewModelScope,
           // SharingStarted.WhileSubscribed(5000L)

        //)


    fun onAction(action: BookListAction){

        when(action){



            is BookListAction.OnBookClick ->{}

            is BookListAction.OnTabSelected ->{
                _state.update{it.copy(  )}
            }

            else -> Unit

        }
    }
//state.map { it.searchQuery }
//Pega o fluxo de estado (BookListState) e transforma em um fluxo apenas de textos de busca (a string digitada).
//Assim, tudo que vem depois só lida com o texto, não com o estado completo.


    //distinctUntilChanged()
    //
    //Evita buscas repetidas.
    //Se o usuário digita "kotlin", apaga e digita "kotlin" de novo, o Flow não vai emitir o mesmo valor duas vezes seguidas.
    //Isso é ótimo para evitar chamadas desnecessárias à API.

    //debounce(500L)
    //
    //Aguarda 500 milissegundos depois que o usuário para de digitar antes de emitir o texto.
    //Isso significa que se o usuário digitar "ko", "kot", "kotl", "kotli", "kotlin" rapidamente, apenas "kotlin" será processado.
    //
    //🔹 Isso protege a API de chamadas repetitivas e melhora a performance.



    init {
        getPopularBooks()
        geterecomendedBooks()
    }


private fun getPopularBooks()=viewModelScope.launch {

    _state.update {
        it.copy(isloading = true)
    }

    bookRepository.getAllPopularBook(query = String(),period = String())
        .onSuccess { getPopularBooks ->
            _state.update {
                it.copy(
                    isloading = false,
                    errorMessagens = null,
                    getPopularBooks = getPopularBooks

                )
            }
        }
        .onError { error   ->
            _state.update {
                it.copy(
                    isloading = false,
                    getPopularBooks = emptyList(),
                    errorMessagens = error.toUiText(),

                )
            }
        }


}



    private fun geterecomendedBooks()=viewModelScope.launch {

        _state.update {
            it.copy(isloading = true)
        }

        bookRepository.getRecommendedBooks()
            .onSuccess { getRecommendedBooks ->
                _state.update {
                    it.copy(
                        isloading = false,
                        errorMessagens = null,
                        getRecommendedBooks = getRecommendedBooks

                    )
                }
            }
            .onError { error   ->
                _state.update {
                    it.copy(
                        isloading = false,
                        getPopularBooks = emptyList(),
                        errorMessagens = error.toUiText(),

                        )
                }
            }


    }





















}