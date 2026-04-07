package com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.BookListAction
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.components.BookListSearch
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.components.BookSearchBar
import org.koin.androidx.compose.koinViewModel


@Composable
fun SearchScreenRoot(
    viewModel: BookSearchViewModel = koinViewModel(),
    OnBookClickSearch:(Book) -> Unit,
    onButtonSearchBack:() -> Unit,
    OnNavigateFilterScreen:() -> Unit

){
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreen(


        onAction = { action ->
            when (action) {

                is BookSearchAction.OnBookClick -> OnBookClickSearch(action.book)

                is BookSearchAction.OnButtonSearchBack -> onButtonSearchBack()

                is BookSearchAction.OnNavigateFilterScreen -> OnNavigateFilterScreen()


                //   is BookSearchAction.OnFavoriteClickSearch ->{}
                // else -> Unit
            }
            viewModel.onActionSearch(action)

        },
        state = state,

    )



}

















@Composable
fun SearchScreen(
    state: BookSearchState,
    onAction: (BookSearchAction)-> Unit,
  //  book:Book

) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val searchResultsListState = rememberLazyListState()

    // Crie um Set de Strings combinadas para busca rápida
   // val favoriteKeys = remember(state.favoriteIds) {
   ////     state.favoriteIds
  //  }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        BookSearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = {onAction(BookSearchAction.OnSearchQueryChange(it))},
            onImeSearch ={keyboardController?.hide()} ,
            onClickPerfil = {onAction(BookSearchAction.OnNavigateFilterScreen) },
            onButtonSearchBack = {onAction(BookSearchAction.OnButtonSearchBack)}
        )

        Spacer(Modifier.height(12.dp))






    Surface(modifier = Modifier
        .fillMaxWidth()
        ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(4.dp),
            //horizontalAlignment = Alignment.CenterHorizontally

        ){
           //

            when{
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator(color = Color.Red)
                    }
                }
                state.errorMessageSearch != null ->{
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.errorMessageSearch.asString(),
                            color = Color.LightGray,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }


                state.searchResults.isEmpty() ->{
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                               text = stringResource(R.string.search_results),
                               modifier = Modifier.padding(vertical = 12.dp)
                             )
                    }
                }

                else ->{
                    BookListSearch(
                        book = state.searchResults,
                        onBookClick = {onAction(BookSearchAction.OnBookClick(it))},
                        FavoriteClick = { book -> onAction(BookSearchAction.OnFavoriteClickSearch(book)) },

                        isFavorite = state.favoriteIds,
                        modifier = Modifier.weight(1f),
                        scrollState = searchResultsListState
                    )
                }









            }

        }

    }


    //


    }

}














@Composable
@Preview (showBackground = true)
fun SearchScreenPrevie() {

    SearchScreen(
        state = BookSearchState(
            searchQuery = "",
            searchResults = emptyList(),
            isLoading = false,
            //isFavorite = setOfNotNull(),
            errorMessageSearch = null
        ),
        onAction = {},
    )


}






