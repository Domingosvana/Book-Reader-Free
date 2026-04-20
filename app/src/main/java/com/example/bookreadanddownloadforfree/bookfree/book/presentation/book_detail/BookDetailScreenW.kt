package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail



import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
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
fun BookDetailScreenRootw(
    viewModel: BookDetailViewModel,
    onBackClicks: () -> Unit,
){
    val state: BookDetailState by viewModel.state.collectAsStateWithLifecycle()

    BookDetailScreenW(
        state = state,
        onAction = { action ->
            when(action) {
                is BookDetailAction.OnBackClickDetail ->  onBackClicks()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )


}

//book_detail/Bookdetailscreenw)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookDetailScreenW(
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = state.selectedTabIndex) { 2 }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Sincronização e Side Effects (Mantidos conforme sua lógica)
    LaunchedEffect(state.selectedTabIndex) {
        if (pagerState.currentPage != state.selectedTabIndex) {
            pagerState.animateScrollToPage(state.selectedTabIndex)
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        onAction(BookDetailAction.OnTabSelected(pagerState.currentPage))
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            BookDetailScreenTopBar(onBackClicks = { onAction(BookDetailAction.OnBackClickDetail) })
        },
        containerColor = MaterialTheme.colorScheme.background // Cor dinâmica de fundo
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            state.book?.let { book ->
                BlurredImageBackground(
                    book = state.book,
                    isFavorite = state.isFavorite,
                    onFavoriteClick = { onAction(BookDetailAction.OnFavoriteClickDetail) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DetailHeader(book = book)

                    DownloadButton(
                        book = book,
                        onOpenSheet = { showBottomSheet = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sistema de Abas com Cores Dinâmicas
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = MaterialTheme.colorScheme.background, // Fundo dinâmico
                        contentColor = MaterialTheme.colorScheme.background, // Texto dinâmico
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                color = MaterialTheme.colorScheme.onBackground // Indicador com a cor primária do tema
                            )
                        }
                    ) {
                        val tabs = listOf(R.string.info, R.string.synopsis)
                        tabs.forEachIndexed { index, titleRes ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                                text = {
                                    Text(
                                        text = stringResource(titleRes),
                                        // Cor muda se selecionado ou não
                                        color = if (pagerState.currentPage == index)
                                            MaterialTheme.colorScheme.onBackground
                                        else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 400.dp),
                        verticalAlignment = Alignment.Top
                    ) { pageIndex ->
                        when (pageIndex) {
                            0 -> TabInfoConteudo(book = book)
                            1 -> TabSinopseConteudo(description = book.description)
                        }
                    }
                }
            }
        }
    }
    // ... DownloadBottomSheet se mantém

    if (showBottomSheet) {
        DownloadBottomSheet(
            sheetState = sheetState,
            isOpen = showBottomSheet,
            book = state.book,
            onDownloadClicked = { /* logica */ },
            onDismissRequest = { showBottomSheet = false }
        )
    }
}













@Composable
fun DownloadButton(book: Book, onOpenSheet: () -> Unit) {
    val context = LocalContext.current
    val isFree = book.downloadUrl != null && book.isFree

    Button(
        onClick = {
            if (isFree) onOpenSheet()
            else context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(book.acsTokenLink ?: "https://books.google.com")))
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright, // Cor principal
            contentColor = MaterialTheme.colorScheme.onBackground  // Cor do texto sobre a principal
        )
    ) {
        Text(if (isFree) "Opções de Download" else "Ver no Site Oficial")
        Spacer(Modifier.width(8.dp))
        Icon(if (isFree) Icons.Default.Download else Icons.AutoMirrored.Filled.MenuBook, null)
    }
}



@Composable
fun TabInfoConteudo(book: Book) {
    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Use .toString() em campos que podem ser números
        InfoRow(label = stringResource(R.string.publisher), value = book.publisher ?: "---")

        InfoRow(
            label = stringResource(R.string.anoDePublicacao),
            value = book.publishedYear?.toString() ?: "---"
        )

        InfoRow(
            label =stringResource(R.string.edicoes_paginas) ,
            value = book.numEditions?.toString() ?: "---"
        )

        InfoRow(
            label = stringResource(R.string.formato),
            value = book.format?.uppercase() ?: "---"
        )

        if (book.languages.isNotEmpty()) {
            InfoRow(
                label = stringResource(R.string.idioma),
                value = book.languages.joinToString(", ") .uppercase()?: "---"
            )

        }

    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}







@Composable
fun DetailHeader(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título do Livro
        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground, // Texto principal
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Autores
        Text(
            text = book.authors.joinToString(", "),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Linha de Info Rápida (Ano | Idioma | Formato)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ano
            Text(
                text = book.publishedYear ?: "N/A",
                style = MaterialTheme.typography.bodyMedium
            )

            VerticalDivider()

            // Idioma
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = book.languages.joinToString(", ").uppercase() ?: "---",
                    style = MaterialTheme.typography.bodyMedium
                )


            }

            VerticalDivider()

            // Formato/Tamanho
            Text(
                text = book.format?.uppercase() ?: "PDF",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun VerticalDivider() {
    Text(
        text = "|",
        modifier = Modifier.padding(horizontal = 12.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}




@Composable
fun TabSinopseConteudo(description: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.synopsis),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (!description.isNull(blank = true)) {
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp, // Aumenta o espaçamento entre linhas para melhor leitura
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // Estado vazio caso o livro não tenha sinopse
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text =stringResource(R.string.nenhuma_sinopse_disponivel_para_este_exemplar) ,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// Função de extensão auxiliar para limpar o código
fun String?.isNull(blank: Boolean): Boolean {
    return if (blank) this.isNullOrBlank() else this == null
}





/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookDetailScreenW(
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit
) {
    val scrollState = rememberScrollState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val searchResultsListState = rememberLazyListState()
    val favoriteBooksListState = rememberLazyListState()
    val pagerState = rememberPagerState(initialPage = state.selectedTabIndex) { 2 }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(0) }

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










    Scaffold(
        topBar = { BookDetailScreenTopBar(onBackClicks = { onAction(BookDetailAction.OnBackClickDetail) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            //  REMOVIDO height(700.dp) - O componente agora é dinâmico
            BlurredImageBackground(
                coverUrl = state.book?.coverUrl,
                isFavorite = state.isFavorite,
                onFavoriteClick = { onAction(BookDetailAction.OnFavoriteClickDetail) },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Conteúdo que fica abaixo da capa mas dentro do fundo
                if (state.book != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.book.title,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.book.authors.joinToString(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row de Informações (Ano, Idioma, Formato)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
//.padding(16.dp)
                            //.background(Color.Red.copy(alpha = 0.2f))
                            //.align(Alignment.CenterHorizontally)
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = state.book.publishedYear ?: "N/A")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "|")
                        Spacer(modifier = Modifier.width(4.dp))

                        Row(horizontalArrangement = Arrangement.Center) {
                            Icon(
                                Icons.Filled.Language, contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            state.book.languages.let{ languages ->
                                Text(text =languages.toString() )
                            }

                        }



                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "|")
                        //Spacer(modifier = Modifier.width(4.dp))


                        Row(horizontalArrangement = Arrangement.End) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = state.book.format ?: "", maxLines = 1)
                          //  Text(text = state.book.retailPrice.toString(), maxLines = 1)
                        }


                    }


                    /*
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = state.book.publishedYear ?: "N/A")
                        Text(text = "|")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Language,
                                contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "PT")
                        }
                        Text(text = "|")
                        Text(text = state.book.format ?: "PDF", maxLines = 1)
                    }



                    */

                    Spacer(modifier = Modifier.height(16.dp))

                    // ... dentro do Column do Scaffold
                    if (state.book != null) {
                        val context = LocalContext.current
                        val hasDownload = state.book.downloadUrl != null && state.book.isFree

                        Button(
                            onClick = {
                                if (hasDownload) {
                                    showBottomSheet = true
                                } else {
                                    // FALLBACK: Se não tem download, abre o site (Google/Open Library)
                                    // ✅ CORREÇÃO: Fallback caso o link seja nulo ou vazio
                                    val linkSeguro = state.book?.acsTokenLink ?: state.book?.acsTokenLink ?: "https://books.google.com"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkSeguro))
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasDownload) MaterialTheme.colorScheme.primary else Color(0xFF0277BD)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (hasDownload) "Opções de Download" else "Ler no Site Oficial",
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = if (hasDownload) Icons.Filled.Download else Icons.Default.MenuBook,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))


                }


                // ------------------------
                // Tabs
                // ------------------------








                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier
                        .fillMaxWidth()
                    , // Permite que o Pager tenha altura,
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
                        modifier = Modifier,
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
                        modifier = Modifier,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 400.dp)
                        .wrapContentHeight()
                ) { pageIndex ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                        when (pageIndex) {
                            0 -> {

                                TabInfoConteudo(state = state)





                            }

                            1 -> {
                              //  Text(
                                //    text = "Conteúdo da aba Favoritos",
                                  //  style = MaterialTheme.typography.bodyMedium
                               // )

                                Text(
                                    text = stringResource(R.string.synopsis),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                       // .padding(top = 24.dp, bottom = 8.dp)
                                )

                               // if (state.isLanding) {
                               //     CircularProgressIndicator()
                               // } else {
                                    // A DESCRIÇÃO AGORA APARECE LOGO ABAIXO SEM ESPAÇO VAZIO
                                    if (state.book?.description != null) {
                                        Text(
                                            text = "Sinopse",
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                        )
                                        Text(
                                            text = state.book.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(16.dp),
                                            textAlign = TextAlign.Justify
                                        )
                                 //   }


                                }


                            }
                        }

                    }

                }




















            }


    // ✅
        }
    }

    if (showBottomSheet && state.book != null) {
        DownloadBottomSheet(
            sheetState = sheetState,
            isOpen = showBottomSheet,
            book = state.book,
            onDownloadClicked = { formato ->
                // Aqui você chama seu DownloadManager
                showBottomSheet = false
            },
            onDismissRequest = { showBottomSheet = false }
        )
    }



}*/


@Preview
@Composable
private fun BlurredImageBackgroundpreviewW() {
    BookDetailScreenW(
        state = BookDetailState(),
        onAction = {}
    )
}


























@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun BookDetailScreenTopBar(
    onBackClicks: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    //var dominantColor by remember { mutableStateOf(MaterialTheme.colorScheme.surfaceBright) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surfaceBright,
        ),
     navigationIcon = {
         IconButton(onClick  =onBackClicks ){
             Icon(
                 imageVector = Icons.Default.ArrowBack,
                 contentDescription = stringResource(id = R.string.back)
             )
         }
     } ,
        title = {""}
    )

}





/*
 Row(
                        modifier = Modifier
                            .fillMaxWidth()
//.padding(16.dp)
                            //.background(Color.Red.copy(alpha = 0.2f))
                            //.align(Alignment.CenterHorizontally)
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = book.publishedYear ?: "N/A")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "|")
                        Spacer(modifier = Modifier.width(4.dp))

                        Row(horizontalArrangement = Arrangement.Center){
                            Icon(Icons.Filled.Language, contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.5f),
                                modifier =Modifier.size(20.dp))
                            // Spacer(modifier = Modifier.width(0.5.dp))
                            Text(text = "Desconhecido")
                        }



                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "|")
                        //Spacer(modifier = Modifier.width(4.dp))


                        Row(horizontalArrangement = Arrangement.End){
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = book.format ?: "df,2.34MB",maxLines = 1)
                            Text(text = book.retailPrice.toString(), maxLines = 1)
                        }



                    }

 */



// Funções auxiliares para organizar o código
@Composable
fun TabInfoConteudo(state: BookDetailState) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {


    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)){

        Text(text = stringResource(R.string.Ano),textAlign = TextAlign.Start, modifier = Modifier)
        Spacer(Modifier.weight(1f))

        state.book?.publishedYear?.let { publishedYear ->
            Text(text = publishedYear,modifier = Modifier)



        }
    }




    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)){

        Text(text = stringResource(R.string.numEditions),textAlign = TextAlign.Start, modifier = Modifier)
        Spacer(Modifier.weight(1f))

        state?.book?.numEditions?:"".let { publishedYear ->
            Text(text = publishedYear,modifier = Modifier)



        }
    }



    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)){

        Text(text = stringResource(R.string.publisher),textAlign = TextAlign.Start, modifier = Modifier)
        Spacer(Modifier.weight(1f))

        state.book?.publisher?.let { publishedYear ->
            Text(text = publishedYear,modifier = Modifier)



        }
    }


    /*stringResource(R.string.semInformacao)*/


    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)){

        Text(text = stringResource(R.string.Id),textAlign = TextAlign.Start, modifier = Modifier)
        Spacer(Modifier.weight(1f))

        state.book?.id?.let { id ->
            Text(text = id,modifier = Modifier)



        }
    }




    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)){

        Text(text = stringResource(R.string.Ano),textAlign = TextAlign.Start, modifier = Modifier)
        Spacer(Modifier.weight(1f))

        state.book?.languages?.toString()?.let { publishedYear ->
            Text(text = publishedYear,modifier = Modifier)



        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)){

        Text(text = stringResource(R.string.Ano),textAlign = TextAlign.Start, modifier = Modifier)
        Spacer(Modifier.weight(1f))

        state.book?.publishedYear?.let { publishedYear ->
            Text(text = publishedYear,modifier = Modifier)



        }
    }























    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)){

        Text(text = stringResource(R.string.ratingsCount),textAlign = TextAlign.Start, modifier = Modifier)
        Spacer(Modifier.width(40.dp))

        state.book?.ratingsCount?:"".let { publishedYear ->
            Text(text = publishedYear,modifier = Modifier)



        }
    }


    Spacer(Modifier.height(12.dp))
    // ... outros chips (páginas, linguagens)

    Row(){
        if (state.book?.languages != null) {
            TitledContent(
                title = stringResource(R.string.languages),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                ) {
                    state.book?.languages?.forEach { language ->
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
    }








    }
}

@Composable
fun TabSinopseConteudo(state: BookDetailState) {
    Column {
        if (state.book?.description != null) {
            Text(
                text = state.book.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text("Nenhuma sinopse disponível.")
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DownloadBottomSheet(
    sheetState: SheetState,
    isOpen: Boolean,
    book: Book?, // Recebe o livro selecionado
    onDownloadClicked: (String) -> Unit, // Passa o formato selecionado
    onDismissRequest: () -> Unit
) {
    if (isOpen && book != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismissRequest,
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                    Text(
                        text =stringResource(R.string.opcoes_de_download) ,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider() // No M3 usa-se HorizontalDivider
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp), // Espaço para não colar no fundo
                contentPadding = PaddingValues(16.dp)
            ) {
                // ✅ CORREÇÃO: Usamos 'item' para o conteúdo fixo do livro
                item {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Exemplo: Se o livro tem apenas um formato disponível
                item {
                    DownloadOptionItem(
                        format = book.format ?: "PDF",
                        size = "2.34 MB", // Exemplo fixo ou vindo do seu modelo
                        onClick = { onDownloadClicked(book.format ?: "PDF") }
                    )
                }

                // Se você tiver uma lista de formatos, usaria items(book.formats) { ... }
            }
        }
    }
}

@Composable
fun DownloadOptionItem(
    format: String,
    size: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(text = format.uppercase(), style = MaterialTheme.typography.titleMedium)
                    Text(text = size, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(
                text = "BAIXAR",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
/*
@Composable
fun ReadingActions(book: Book) {
    val context = LocalContext.current

    Row(modifier = Modifier.fillMaxWidth()) {
        if (book.downloadUrl != null) {
            // Caso 1: Temos o arquivo!
            Button(onClick = { /* Lógica de Download */ }) {
                Text("BAIXAR LIVRO")
            }
        } else if (book.infoUrl != null) {
            // Caso 2: Não temos o arquivo, mas temos o link do site
            OutlinedButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(book.infoUrl))
                context.startActivity(intent)
            }) {
                Text("LER NO SITE OFICIAL")
            }
        }
    }
}

 */