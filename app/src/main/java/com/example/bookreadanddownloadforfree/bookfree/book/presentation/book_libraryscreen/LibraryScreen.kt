package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow

import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.components.BookListFavorite
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.components.LibraryScreenTopBar
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.BookSearchAction
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.components.BookListSearch
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_dark_background
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_light_tertiary
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun BookLibraryScreenRoot(
    viewModel: BookLibraryViewModel = koinViewModel(),
    onBookClick: (Book) -> Unit,
    modifier:Modifier = Modifier

){
    val state by viewModel.state.collectAsStateWithLifecycle()

    BookLibraryScreen(
     //   state = state,
        modifier = modifier,
        onAction = { action ->
            when(action){
                is  BookLibraryAction.OnclickBook -> onBookClick(action.book)

                else -> Unit
            }
            viewModel.onAction(action)
        },
        state = state
    )
}

















@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookLibraryScreen(
    state: BookLibraryState,
    modifier: Modifier,
    onAction: (BookLibraryAction) -> Unit

    ){

        val coroutineScope = rememberCoroutineScope()

    // pagerState inicializado com a aba do estado (apenas para começar)

        val favoriteListState = rememberLazyListState()


    val pagerState = rememberPagerState(
        initialPage = state.selectedTabIndex,
        pageCount = { 2 } // <- forma correta com parâmetro nomeado
    )


    val favoriteBookLibrary = rememberLazyListState()

    val downloadBookLibrary = rememberLazyListState()

    // Quando abrir a tela ou quando o ViewModel alterar selectedTabIndex por fora,
    // garante que o pager esteja nessa página (scroll imediato, sem animação).

    LaunchedEffect(state.selectedTabIndex){
        if(pagerState.currentPage != state.selectedTabIndex){
            pagerState.scrollToPage(state.selectedTabIndex)
        }
    }


    // Observa mudanças reais no pager (swipe / animação concluída)
    // e atualiza o ViewModel apenas se necessário.

    LaunchedEffect(pagerState){
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page != state.selectedTabIndex){
            //
            }
        }

    }

// Se os resultados mudarem, rola para o topo
    LaunchedEffect(state.favorites) {
        if (state.favorites.isNotEmpty()) {
            favoriteBookLibrary.animateScrollToItem(0)
        }
    }





    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Biblioteca",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor =MaterialTheme.colorScheme.surfaceBright
            )
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor =MaterialTheme.colorScheme.background,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                )
            }
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier

                    ){
                        Text(
                            text = "FAVORITOS",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(6.dp))

                      let { total ->
                            Text(
                                text = "(${state.totalDeFavorite})",
                                color = MaterialTheme.colorScheme.onBackground
                            )



                            /*book.averageRating?.let { rating ->
                    Row(modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${round(rating * 10) / 10.0}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }

                             */
                        }

                    }

                }
            }

            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DOWNLOAD",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> {

                    if (state.favorites.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_favorite_books),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    } else {

                        BookListFavorite(
                            book = state.favorites,
                            onBookClick = {onAction(BookLibraryAction.OnclickBook(it))},
                            FavoriteClick = { book -> onAction(BookLibraryAction.OnFavoriteClick(book)) },
                            isFavorite = true,
                            modifier = Modifier.weight(1f),
                            scrollState = favoriteListState
                        )

                    }


                   // Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  //      Text("Seus livros favoritos", color = Color.White)
                   // }
                }
                1 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Livros baixados", color = Color.White)
                    }
                }
            }
        }
    }
}



































@Composable

fun BookLibraryScreenPreview(){

}

