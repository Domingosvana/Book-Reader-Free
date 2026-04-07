package com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen

import android.content.Context

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit


import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
// ESTE ABAIXO É O MAIS IMPORTANTE:
//import androidx.datastore.preferences.core.get




import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.BookRepository
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.domain.InterestType
import com.example.bookreadanddownloadforfree.bookfree.core.domian.onError
import com.example.bookreadanddownloadforfree.bookfree.core.domian.onSuccess
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.toUiText
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.String


class BookSearchViewModel (
    private  val bookRepository: BookRepository,
    private val savedStateHandle: SavedStateHandle,
    private val bookLocalStore: BookLocalStore

):ViewModel(){
  //  private val bookId = savedStateHandle.toRoute<Route.BookSearch>().id


        private var cachedBooks = emptyList<Book>()

        private var searchJob: Job? = null




    private val _statefilter = MutableStateFlow(BookFilterState())
    val statefilter =  _statefilter.asStateFlow()



        private val _state = MutableStateFlow(BookSearchState())
        val state =  _state.asStateFlow()
            //val state = _state.onStart { ... }.stateIn(...)
            //
            //Esse é o fluxo que você expõe publicamente para a UI observar.
            //
            //O onStart é chamado quando alguém começa a coletar o estado,
            //ou seja, quando a tela é aberta pela primeira vez.
            //
            //Dentro dele:
            .onStart {
                if (cachedBooks.isEmpty()){
                    observeSearchQuery()
                }

            }
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
        // 1. No init (Como as chaves são carregadas do Banco)
        init {
            viewModelScope.launch {
                bookLocalStore.filterStateFlow.collect{savedFilter ->
                    _statefilter.update {savedFilter}
                    filtrarLivros()
                }

            }



            bookRepository
                .getFavoriteBookAll()
                .onEach { favorites ->
                    _state.update {
                        it.copy(
                            favoriteIds = favorites.map { book ->
                                //  Use trim() para evitar espaços invisíveis
                                "${book.id}_${book.languages.firstOrNull()?.trim() ?: ""}"
                            }.toSet()
                        )
                    }
                }
                .launchIn(viewModelScope)
        }



    fun onActionSearch(action: BookSearchAction){

            when(action){

                is  BookSearchAction.OnSearchQueryChange ->{
                    _state.update {it.copy(searchQuery = action.query)}
                }

                is BookSearchAction.OnBookClick ->{
                    // MOMENTO 1: O utilizador clicou no livro para ver detalhes
                    viewModelScope.launch {
                        action.book.authors.firstOrNull()?.let { author ->
                            bookRepository.recordInteraction(author, InterestType.AUTHOR)
                        }
                    }
                }
                is BookSearchAction.OnFavoriteClickSearch -> {
                    viewModelScope.launch {
                        val book = action.book
                        val lang = book.languages.firstOrNull()?.trim() ?: ""
                        val currentKey = "${book.id}_$lang"

                        // DEBUG: Use isto para ver no Logcat por que o IF falha
                         println("DEBUG: Procurando chave: $currentKey")
                         println("DEBUG: Chaves existentes: ${state.value.favoriteIds}")

                        val isFavorite = state.value.favoriteIds.contains(currentKey)

                        if (isFavorite) {
                            //  Agora ele vai entrar aqui para remover!
                            bookRepository.deleteFavoriteBook(book.id, lang)
                        } else {
                            bookRepository.markAsFavorite(book)
                        }
                    }
                }





                /*
                                is BookSearchAction.OnFavoriteClickSearch -> {
                                    viewModelScope.launch {
                                        val book = action.book

                                        val isAlreadyFavorite =
                                            bookRepository.getFavoriteBookAll()
                                                .first()
                                                .any { it.id == book.id }

                                        if (isAlreadyFavorite) {
                                            bookRepository.deleteFavoriteBook(book.id)
                                        } else {
                                            bookRepository.markAsFavorite(book)
                                        }
                                    }
                                }

                 */


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
        @OptIn(FlowPreview::class)
        private fun observeSearchQuery(){
            state
                .map{it.searchQuery}
                .distinctUntilChanged()
                .debounce(500L)
                .onEach { query ->
                    when{
                        query.isBlank() ->{
                            _state.update { it.copy(
                                errorMessageSearch = null,
                                searchResults = cachedBooks
                            ) }
                        }
                        query.length >= 2 ->{
                            searchJob?.cancel()
                            searchJob= searchBooks(query)
                        }

                    }


                }


                .launchIn(viewModelScope)

        }

    fun onActionFilter(action: BookFilterAction){
        when(action){

            is BookFilterAction.onYearSelectedMax -> {
                _statefilter.update { it.copy(selectedYearDeMax = action.year) }
              //  saveFilters() // Grava no disco
               // filtrarLivros()
            }

            is BookFilterAction.onYearSelectedMin ->{
                _statefilter.update{it.copy(selectedYearDeMin = action.year)}
               // saveFilters() // Grava no disco
               // filtrarLivros()
            }

            is BookFilterAction.onLanguagesChanged ->{
                _statefilter.update{it.copy(selectedLanguages = action.languages)}
                //saveFilters() // Grava no disco
              //  filtrarLivros()
            }

            is BookFilterAction.Clear -> {
                viewModelScope.launch {
                    bookLocalStore.saveFilterState(BookFilterState())
                }
                _statefilter.update{BookFilterState() }
              //  saveFilters() // Grava no disco
               // filtrarLivros()
            }

            is BookFilterAction.onButtonFilterBack -> {
                saveFilters() // Grava no disco
                filtrarLivros()
            }




        }
    }



    // Função auxiliar para não repetir código
    private fun saveFilters() {
        viewModelScope.launch {
            bookLocalStore.saveFilterState(_statefilter.value)
        }
    }



     fun filtrarLivros(){
        val FiltrosAtuais = _statefilter.value

            //vamos comecar pela lista original
        var listafiltrado = cachedBooks


        //filtro de ano maximo
        FiltrosAtuais.selectedYearDeMax?.let { max ->
            listafiltrado = listafiltrado.filter{livro ->
                val anolivro = livro.publishedYear?.take(4)?.toIntOrNull()?:0
                anolivro <= max

            }

        }

        //filtro de ano minimo
        FiltrosAtuais.selectedYearDeMin?.let { min ->
            listafiltrado = listafiltrado.filter{livro ->
                val anolivro = livro.publishedYear?.take(4)?.toIntOrNull()?:0
                anolivro >= min
            }
        }

        //filtros de idioma
        if(FiltrosAtuais.selectedLanguages.isNotEmpty()) {
            listafiltrado = listafiltrado.filter { livro ->
                //agora verificamos se uma dioma esta selecionado na lista

                livro.languages.any() { language ->
                    FiltrosAtuais.selectedLanguages.contains(language)

                }
            }
        }

        //atualizamo o estado da tela de busca

        _state.update {
            it.copy(
                searchResults = listafiltrado,
                errorMessageSearch = null,
                isLoading = false

            )
        }













    }



        private fun searchBooks(query:String)=viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            bookRepository.searchBooks(query)
                .onSuccess { searchBooks ->




                            cachedBooks = searchBooks

                            filtrarLivros()



                    if (searchBooks.isNotEmpty()){
                        viewModelScope.launch {
                            bookRepository.recordInteraction(
                                term = query,
                                type = InterestType.SEARCH_TERM
                            )
                        }
                    }

                }

                .onError { error->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            searchResults = emptyList(),
                            errorMessageSearch = error.toUiText()
                        )
                    }
                }


        }

















    }









//private val  DataStoreFilter =  BookFilterState()

private val Context.dataStore by preferencesDataStore(name = "filter_preferences")

class BookLocalStore(private val context: Context) {

    private object FilterKeys {
        val YEAR_MIN = intPreferencesKey("year_min")
        val YEAR_MAX = intPreferencesKey("year_max")
        val LANGUAGES = stringSetPreferencesKey("languages")
    }
    suspend fun saveFilterState(state: BookFilterState) {
        context.dataStore.edit { preferences ->
            // Se for nulo, removemos a chave para representar o estado "vazio"
            state.selectedYearDeMin?.let { preferences[FilterKeys.YEAR_MIN] = it } ?: preferences.remove(FilterKeys.YEAR_MIN)
            state.selectedYearDeMax?.let { preferences[FilterKeys.YEAR_MAX] = it } ?: preferences.remove(FilterKeys.YEAR_MAX)

            // Converte List para Set para a DataStore aceitar
            preferences[FilterKeys.LANGUAGES] = state.selectedLanguages.toSet()
        }
    }

    // Leitura contínua em tempo real
    val filterStateFlow: Flow<BookFilterState> = context.dataStore.data
        .map { preferences ->
            mapBookFilter(preferences)
        }

    // Função auxiliar privada (não precisa de ser suspend porque o map já trata disso)
    private fun mapBookFilter(preferences: androidx.datastore.preferences.core.Preferences): BookFilterState {
        val languageSet = preferences[FilterKeys.LANGUAGES]
        return BookFilterState(
            // Se o valor não existir (null), devolvemos o nulo para o estado inicial
            selectedYearDeMin = preferences[FilterKeys.YEAR_MIN] ,
            selectedYearDeMax = preferences[FilterKeys.YEAR_MAX],
            selectedLanguages = languageSet?.toSet()?: emptySet()
        )
    }
}