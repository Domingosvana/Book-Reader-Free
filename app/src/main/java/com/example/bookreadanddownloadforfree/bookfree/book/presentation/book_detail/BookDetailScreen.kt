package com.example.zlib.bookpedia.book.presentation.book_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailAction
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailState
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailViewModel
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_dark_inverseOnSurface
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_dark_onSecondaryContainer
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_dark_outlineVariant
import com.example.bookreadanddownloadforfree.ui.theme.md_theme_light_scrim

import com.example.bookreadanddownloadforfree.ui.theme.md_theme_light_tertiary
import com.example.zlib.bookpedia.book.presentation.book_detail.components.BlurredImageBackground
import com.example.zlib.bookpedia.book.presentation.book_detail.components.BookChip
import com.example.zlib.bookpedia.book.presentation.book_detail.components.ChipSize
import com.example.zlib.bookpedia.book.presentation.book_detail.components.TitledContent
import kotlinx.coroutines.launch

import kotlin.math.round


@Composable
fun BookDetailScreenRoot(
    viewModel: BookDetailViewModel,
    onBackClicks: () -> Unit,
){
    val state: BookDetailState by viewModel.state.collectAsStateWithLifecycle()

    BookDetailScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is BookDetailAction.OnBackClickDetail -> onBackClicks()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )


}
















@Composable
private fun BookDetailScreen(
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val searchResultsListState = rememberLazyListState()
    val favoriteBooksListState = rememberLazyListState()
    val pagerState = rememberPagerState(initialPage = state.selectedTabIndex) { 2 }

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
                onAction(BookDetailAction.OnTabSelected(page))
            }
        }
    }

    // Se os resultados mudarem, rola para o topo
    LaunchedEffect(state.book) {
        if (state.book != null) {
            searchResultsListState.animateScrollToItem(0)
        }
    }
/*
    BlurredImageBackground(
        coverUrl = state.book?.coverUrl,
        isFavorite = state.isFavorite,
        onFavoriteClick = { onAction(BookDetailAction.OnFavoriteClickDetail) },
       // onBackClicks = { onAction(BookDetailAction.OnBackClickDetail) },
        modifier = Modifier.fillMaxSize(),
    ) {
        // 🔹 Aqui está o segredo: uma única rolagem para tudo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // <--- Toda a tela agora rola
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ------------------------
            // Cabeçalho do Livro
            // ------------------------
            if (state.book != null) {
                Text(
                    text = state.book.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = state.book.authors.joinToString(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ------------------------
                // Tabs
                // ------------------------
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 700.dp),
                   // containerColor = md_theme_dark_onSecondaryContainer,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = md_theme_dark_outlineVariant,
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        )
                    }
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                        modifier = Modifier.weight(1f),
                        //selectedContentColor = md_theme_dark_inverseOnSurface,
                      //  unselectedContentColor = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = stringResource(R.string.search_results),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                        modifier = Modifier.weight(1f),
                        selectedContentColor = md_theme_light_scrim,
                        unselectedContentColor = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = stringResource(R.string.favorites),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ------------------------
                // Conteúdo das Tabs
                // ------------------------
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ){pageIndex ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                        when (pageIndex) {
                            0 -> {

                                Box(){
                                    Column() {

                                        state.book.averageRating?.let { rating ->
                                            TitledContent(title = stringResource(R.string.rating)) {
                                                BookChip {
                                                    Text(text = "averageRating:  ${round(rating * 10) / 10.0}")
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = md_theme_light_tertiary
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(12.dp))

                                        state.book.numEditions?.let { pageCount ->
                                            TitledContent(title = stringResource(R.string.pages)) {
                                                BookChip {
                                                    Text(text = pageCount.toString())
                                                }
                                            }
                                        }


                                        Spacer(Modifier.height(12.dp))


                                        if (state.book.languages != null) {
                                            TitledContent(
                                                title = stringResource(R.string.languages),
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                FlowRow(
                                                    horizontalArrangement = Arrangement.Center,
                                                    modifier = Modifier.wrapContentSize(Alignment.Center)
                                                ) {
                                                    state.book.languages.forEach { language ->
                                                        BookChip(
                                                            size = ChipSize.SMALL,
                                                            modifier = Modifier.padding(2.dp)
                                                        ) {
                                                            Text(
                                                                text = language.uppercase(),
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }



                                        Spacer(Modifier.height(12.dp))


                                    }
                                }



                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {


                                }




                            }

                            1 -> {
                                Text(
                                    text = "Conteúdo da aba Favoritos",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = stringResource(R.string.synopsis),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 24.dp, bottom = 8.dp)
                                )

                                if (state.isLanding) {
                                    CircularProgressIndicator()
                                } else {
                                    Text(
                                        text = if (state.book.description.isNullOrBlank()) {
                                            stringResource(R.string.description_unavailable)
                                        } else {
                                            state.book.description
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Justify,
                                        color = if (state.book.description.isNullOrBlank()) {
                                            Color.Black.copy(alpha = 0.4f)
                                        } else Color.Black,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }


                            }
                        }

                    }

                }


            }
        }


    }

 */

}
