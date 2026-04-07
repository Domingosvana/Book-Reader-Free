package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.ui.theme.gradient6
import org.koin.androidx.compose.koinViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.componets.BookListPopular
import kotlinx.coroutines.launch
// "key,title,author_name,first_publish_year,isbn,cover_i,edition_count,subject,ia"
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.componets.BookSearchBarNavigate
import com.example.bookreadanddownloadforfree.ui.theme.BookReadAndDownloadForFreeTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun BookListScreenRoot(
    viewModel: BookListViewModel = koinViewModel(),
    onBookClick: (Book) -> Unit,
    onClickNavigate: () -> Unit,
    modifier: Modifier
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    BookListScreen(
        onAction = { action ->
            when(action){
                is BookListAction.OnBookClick ->onBookClick(action.book)

                is BookListAction.OnClickNavigates -> onClickNavigate()

                else -> Unit
            }
            viewModel.onAction(action)
        },
        state = state
    )
}



















@Composable
fun BookListScreen(
    state: BookListState,
    onAction: (BookListAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchResultsListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // pagerState inicializado com a aba do estado (apenas para começar)
    val pagerState = rememberPagerState(initialPage =state.selectedTabIndex ) { 2 }
    val RECOMENDACOESSTATE = rememberLazyListState()
    val POPULARESSTATE = rememberLazyListState()

    // Quando abrir a tela ou quando o ViewModel alterar selectedTabIndex por fora,
    // garante que o pager esteja nessa página (scroll imediato, sem animação).
    LaunchedEffect(state.selectedTabIndex) {
        if (pagerState.currentPage != state.selectedTabIndex) {
            pagerState.scrollToPage(state.selectedTabIndex)
        }
    }

    // Observa mudanças reais no pager (swipe / animação concluída)
    // e atualiza o ViewModel apenas se necessário.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page != state.selectedTabIndex) {
                onAction(BookListAction.OnTabSelected(page))
            }
        }
    }

    // Se os resultados mudarem, rola para o topo
    LaunchedEffect(state.getPopularBooks) {
        if (state.getPopularBooks.isNotEmpty()) {
            searchResultsListState.animateScrollToItem(0)
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.linearGradient(gradient6))
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔎 Barra de pesquisa sempre no topo
        BookSearchBarNavigate(
            onClickPerfil ={},
            onClickNavigate = {
                onAction(BookListAction.OnClickNavigates)
            }
        )



        // Decisão: Tabs ou Resultados

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color =  MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TabRow(
                            selectedTabIndex = pagerState.currentPage,
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .widthIn(max = 700.dp)
                                .fillMaxWidth(),
                            containerColor =  MaterialTheme.colorScheme.background,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                )
                            }
                        ) {
                            Tab(
                                selected = pagerState.currentPage ==0,
                                onClick = {
                                    // anima o pager; snapshotFlow vai persistir a mudança no ViewModel
                                    coroutineScope.launch { pagerState.animateScrollToPage(0) }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = MaterialTheme.colorScheme.background) // 🔴 Fundo do tab
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.recomendacoes ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            Tab(
                                selected = pagerState.currentPage ==1,
                                onClick = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(1) }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = MaterialTheme.colorScheme.background) // ⚪ Fundo do tab
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text =stringResource(id = R.string.populares ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                        }

                        Spacer(modifier = Modifier.height(4.dp))

                       HorizontalPager(
                           state = pagerState,
                           modifier = Modifier
                               .fillMaxWidth()
                               .weight(1f)
                       ){
                           pagerIndex->
                           Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center){
                               when(pagerIndex){
                                   0 ->{
                                       if (state.isloading){
                                           CircularProgressIndicator()

                                       }
                                       else{
                                           when{

                                               state.isloading ->{
                                                   CircularProgressIndicator()
                                               }


                                       state.getRecommendedBooks.isEmpty() ->{
                                           Text(
                                               text = stringResource(R.string.sem_livros_recomendados),
                                              textAlign = TextAlign.Center,
                                               style = MaterialTheme.typography.headlineSmall,
                                               color = MaterialTheme.colorScheme.error
                                           )

                                       }


                                               state.errorMessagens != null -> {
                                                   Text(
                                                       state.errorMessagens.asString(),
                                                       textAlign = TextAlign.Center,
                                                       style = MaterialTheme.typography.labelMedium,
                                                   )
                                               }



                                        else ->{

                                            when{
                                                state.isloading ->{
                                                    CircularProgressIndicator()
                                                }
                                                state.errorMessagens != null -> {
                                                    Text(
                                                        state.errorMessagens.asString(),
                                                        textAlign = TextAlign.Center,
                                                        style = MaterialTheme.typography.headlineSmall,
                                                    )
                                                }

                                                else ->{
                                                    BookListPopular(
                                                        book = state.getRecommendedBooks,
                                                        onBookClickPopulares = {onAction(BookListAction.OnBookClick(it))}
                                                    )
                                                }
                                                }


                                            }



                                           }
                                       }



                                   }





                                   1 -> {
                                       if(state.isloading){
                                           CircularProgressIndicator(
                                               color = MaterialTheme.colorScheme.surfaceBright

                                           )
                                       }


                                       else {
                                           when{
                                               state.errorMessagens != null -> {
                                                   Text(
                                                       text = state.errorMessagens.asString(),
                                                       textAlign = TextAlign.Center,
                                                       style = MaterialTheme.typography.headlineSmall,
                                                       color = MaterialTheme.colorScheme.surfaceBright
                                                   )
                                               }


                                               else -> {
                                                   BookListPopular(
                                                       book = state.getPopularBooks,
                                                       onBookClickPopulares = {onAction(BookListAction.OnBookClick(it))}
                                                   )
                                               }
                                           }


                                       }







                                       }
                                   }
                               }


                           }
                       }
















                    }
                }
            }









@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DarkBookListScreenPreview( ){
    BookReadAndDownloadForFreeTheme(dynamicColor = false, darkTheme = true){
        BookListScreenRoot(
            onBookClick = {},
            // viewModel = viewModel(),
            onClickNavigate = {},
            viewModel = koinViewModel(),
            modifier = Modifier
        )
    }

}






@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BookListScreenPreview( ){
    BookReadAndDownloadForFreeTheme(dynamicColor = false, darkTheme = false){
        BookListScreenRoot(
            onBookClick = {},
            // viewModel = viewModel(),
            onClickNavigate = {},
                    viewModel = koinViewModel(),
            modifier = Modifier
        )
    }

}










