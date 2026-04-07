package com.example.bookreadanddownloadforfree.bookfree.app

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.BookListScreenRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.BookListViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.BookSearchViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.SearchScreenRoot
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.navigation
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.MainScreenRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.BookCountViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.ScreenCountRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.SplashDestination
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.SplashViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailAction
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailScreenRootw
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.FilterScreenRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.FilterViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.BookLibraryScreenRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.BookLibraryViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.SelectedBookViewModel

@Composable
fun App(
    viewModel: SplashViewModel = koinViewModel()

) {
    val splashDestination by viewModel.destination.collectAsState()

    MaterialTheme {

        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination?.route

        // Verificamos se a Splash já terminou de decidir
            if (splashDestination is SplashDestination.Loading) {
                // Enquanto decide, mostramos uma tela vazia (a Splash nativa está por cima)
                Box(modifier = Modifier.fillMaxSize())
            }
        else{

                Scaffold(


                    /*bottomBar = {
                        // Mostra o BottomBar apenas nas telas principais
                        if (currentDestination == Route.BookList::class.qualifiedName ||
                            currentDestination == Route.BookSearch::class.qualifiedName ||
                            currentDestination == Route.BookLibrary::class.qualifiedName
                        ) {
        /*
                            BottomNavigationBar(
                                currentDestination = currentBackStack?.toRoute<Route>(),
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Route.BookGraph) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            )

         */



        /*
                            BottomNavigationBar(
                                currentDestination = currentDestination,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Route.BookGraph::class.qualifiedName!!) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            )

         */


                        }





                    }*/



                ) { padding ->
                    NavHost(
                        navController = navController,
                        // 1. O NavHost sempre começa apontando para o Grafo principal
                        startDestination = Route.BookGraph,
                        modifier = Modifier.padding(padding)
                    ) {
                        // 2. O sub-grafo é que decide qual tela mostrar primeiro
                        navigation<Route.BookGraph>(
                            startDestination =if (splashDestination is SplashDestination.Home) Route.Main else Route.Count,// Route.Count
                        ) {






                            composable<Route.Count> {
                                val viewModel = koinViewModel<BookCountViewModel>()
                                ScreenCountRoot(
                                    viewModel = viewModel,
                                    onSignInSuccess = {navController.navigate(Route.Main)},
                                )
                            }




                            composable<Route.Main> {
                                MainScreenRoot(

                                )
                            }

                            composable<Route.BookList> {
                                val viewModel = koinViewModel<BookListViewModel>()
                                val selectedBookViewModel = it.sharedKoinViewModel<SelectedBookViewModel>(navController)

                                LaunchedEffect(key1=  true) {
                                    selectedBookViewModel.onSelectedBook(null)
                                }

                                BookListScreenRoot(
                                    viewModel = viewModel,
                                    onBookClick = { book ->
                                        selectedBookViewModel.onSelectedBook(book)
                                        navController.navigate(Route.BookDetail(book.id))

//
                                    },
                                    onClickNavigate = {
                                        navController.navigate(Route.BookSearch)
                                    },
                                    modifier = Modifier,
                                )
                            }


                            //     fe80::9ca9:c4ff:fe28:e219
                            //     2c0f:f888:a180:2a98:9ca9:c4ff:fe28:e219
                            //     2c0f:f888:a180:2a98:b8e8:ecc8:44ec:31aa
                            //     192.168.0.236


                            composable<Route.BookDetail> {
                                val viewModel = koinViewModel<BookDetailViewModel>()

                                val selectedBookViewModel =
                                    it.sharedKoinViewModel<SelectedBookViewModel>(navController)
                                val selectedBook = selectedBookViewModel.selectedBook.collectAsStateWithLifecycle()

                                LaunchedEffect(selectedBook) {
                                    selectedBook?.let {
                                        viewModel.onAction(BookDetailAction.OnSelectedBookChange(selectedBook))
                                    }

                                }

                                BookDetailScreenRootw(
                                    viewModel = viewModel,
                                    onBackClicks ={
                                        navController.navigateUp()
                                    }
                                )

                            }



                            // 🔒 Telas futuras (comentadas para não quebrar)
                            composable<Route.BookLibrary> {
                                val viewModel = koinViewModel<BookLibraryViewModel>()

                                // Usa o mesmo ViewModel compartilhado entre as telas
                                val selectedBookViewModel = it.sharedKoinViewModel<SelectedBookViewModel>(navController)

                                LaunchedEffect(true) {
                                    selectedBookViewModel.onSelectedBook(null)
                                }

                                BookLibraryScreenRoot(
                                    viewModel = viewModel,
                                    onBookClick = { book ->
                                        // Define o livro selecionado
                                        selectedBookViewModel.onSelectedBook(book)
                                        // Navega para o detalhe
                                        navController.navigate(Route.BookDetail(book.id))
                                    },
                                )
                            }







                            composable<Route.BookSearch> {
                                val viewModel = koinViewModel<BookSearchViewModel>()

                                val selectedBokkViewModel = it.sharedKoinViewModel<SelectedBookViewModel>(navController)

                                val selectedVieModel = it.sharedKoinViewModel<FilterViewModel>(navController)


                                LaunchedEffect(key1 = true) {
                                    selectedBokkViewModel.onSelectedBook(null)
                                }


                                SearchScreenRoot(
                                    viewModel = viewModel,
                                    OnBookClickSearch = { book ->
                                        selectedBokkViewModel.onSelectedBook(book)
                                        navController.navigate(Route.BookDetail(book.id))


                                    },
                                    onButtonSearchBack = {

                                        navController.navigateUp()//(Route.BookList::class.qualifiedName!!)
                                    },
                                    OnNavigateFilterScreen = {

                                        navController.navigate(Route.Filter)
                                    }
                                )
                            }


                            composable<Route.Filter>{entry ->
                                // Encontramos a entrada (entry) da tela de busca que está "atrás" desta
                                val parentEntry = remember(entry){
                                    navController.getBackStackEntry<Route.BookSearch>()
                                }

                                //  Pedimos o ViewModel ao Koin, mas passamos a tela de busca como "dona"
                                val viewModel = koinViewModel<BookSearchViewModel>(
                                    viewModelStoreOwner = parentEntry
                                )
                                // val selectedVieModel = it.sharedKoinViewModel<FilterViewModel>(navController)


                                FilterScreenRoot(
                                    viewModel = viewModel,
                                    OnNavigateFilterBack = {
                                        navController.navigateUp()

                                    },
                                    OnButtonFilterBack ={
                                        navController.popBackStack()
                                    }
                                )
                            }




                            // composable<Route.Profile> { ProfileScreen() }
                        }
                    }
                }



        }


    }
}








/*
@Composable
fun BottomNavigationBar(
    currentDestination: Route?,
    onNavigate: (Route) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination is Route.BookList,
            onClick = { onNavigate(Route.BookList) },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Biblioteca") },
            label = { Text("Biblioteca") }
        )

        NavigationBarItem(
            selected = currentDestination is Route.BookLibrary,
            onClick = { onNavigate(Route.BookLibrary) }, // use um id vazio se ainda não precisar
            icon = { Icon(Icons.Default.Bookmarks, contentDescription = "Meus livros") },
            label = { Text("Meus livros") }
        )

        NavigationBarItem(
            selected = currentDestination is Route.Profile,
            onClick = { /* futuro */ },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}


 */















/*
@Composable
fun BottomNavigationBar(
    currentDestination: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(60.dp) // ✅ reduz a altura SEM quebrar
    ) {

        NavigationBarItem(
            selected = currentDestination == Route.BookList::class.qualifiedName,
            onClick = { onNavigate(Route.BookList::class.qualifiedName!!) },
            icon = {
                Icon(Icons.Default.MenuBook, contentDescription = "Biblioteca", modifier = Modifier.size(20.dp) // ✅ reduz ícone
                ) },
            label = { Text(text = "Biblioteca", fontSize = 10.sp) // ✅ reduz texto)
            },
            alwaysShowLabel = true
        )

        NavigationBarItem(
            selected = currentDestination == Route.BookLibrary::class.qualifiedName,
            onClick = { onNavigate(Route.BookLibrary::class.qualifiedName!!) },
            icon = { Icon(Icons.Default.Bookmarks, contentDescription = "Meus livros", modifier = Modifier.size(20.dp))
            },
            label = { Text(text = "Meus livros", fontSize = 10.sp) },
            alwaysShowLabel = true
        )

        NavigationBarItem(selected = currentDestination == "Profile", onClick = { /* futuro */ }, icon = {
                Icon(Icons.Default.Person, contentDescription = "Perfil", modifier = Modifier.size(20.dp)) },
            label = {
                Text(text = "Perfil", fontSize = 10.sp)},
            alwaysShowLabel = true)
    }
}
*/




@Composable
 inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}
