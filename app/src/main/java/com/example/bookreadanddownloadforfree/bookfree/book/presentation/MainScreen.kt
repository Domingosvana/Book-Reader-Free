package com.example.bookreadanddownloadforfree.bookfree.book.presentation

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.app.Route
import com.example.bookreadanddownloadforfree.bookfree.app.sharedKoinViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailAction
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailScreenRootw
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.FilterScreenRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.BookLibraryScreenRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.BookLibraryViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.BookListScreenRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.BookListViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.SelectedBookViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_profile.ProfileScreenRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_profile.ProfileViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.BookSearchViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.SearchScreenRoot
import org.koin.androidx.compose.koinViewModel


@Composable
fun MainScreenRoot (
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()
    ) {


    MainScreen()



}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen (modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()

    val destination = currentBackStack?.destination
    //val currentRoute = navBackStackEntry?.destination?.route


//para visualizar o titulo da tela ou da navegacao
    var titleTopBarId by remember { mutableIntStateOf (R.string.Biblioteca) }

    LaunchedEffect(key1 = currentBackStack){

        if (destination?.hasRoute<Route.BookList>() == true) {
            titleTopBarId = R.string.Biblioteca
        } else if (destination?.hasRoute<Route.BookLibrary>() == true) {
            titleTopBarId = R.string.Meus_livros
        }
        else if (destination?.hasRoute<Route.Profile>() == true){

        }

    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(


            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    //currentDestination = currentDestination,
                    onNavigationChanged = { route -> }
                )
               // { title ->
//titleTopBarId = title
                 //     }


            }








){
    contentPadding ->
MainContentScreen(
    navController = navController,
    contentPadding = contentPadding
)

}





}









}












data class NavigationItem(
@StringRes val title: Int,
val icon: ImageVector,
val router: Route
)



@Composable
fun BottomNavigationBar(
    navController: NavHostController,
//  currentDestination: String?,
    onNavigationChanged:(String) -> Unit
) {

val navigationItem = listOf(
    NavigationItem(
    title = R.string.Biblioteca,
    icon = Icons.Default.MenuBook,
    router = Route.BookList
),
    NavigationItem(
    title = R.string.Meus_livros,
    icon = Icons.Default.Bookmarks,
    router = Route.BookLibrary
),
    NavigationItem(
    title = R.string.Perfil,
    icon = Icons.Default.Person,
    router = Route.Profile
)
)





NavigationBar(
    modifier = Modifier.height(60.dp), // ✅ reduz a altura SEM quebrar
    containerColor = MaterialTheme.colorScheme.background
) {
val navBackStackEntry by navController.currentBackStackEntryAsState() //.currentBackStackEntryAsState()
val currentRoute = navBackStackEntry?.destination

navigationItem.forEach {item ->
NavigationBarItem(
    selected =  navBackStackEntry?.destination?.hasRoute(item.router::class)?:false, //currentRoute == item.router ,//currentDestination == Route.BookList::class.qualifiedName,
    onClick ={
        // Verifica se já não estamos na rota para evitar re-empilhar
        val isSelected = navBackStackEntry?.destination?.hasRoute(item.router::class) ?: false
        if(!isSelected){
            navController.navigate(item.router) { // PASSE O OBJETO DIRETAMENTE, NÃO toString()
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            // Se precisar passar o nome para o Log ou Callback:
            onNavigationChanged(item.router::class.qualifiedName ?: "")
        }
    } ,//{ onNavigationChanged(Route.BookList::class.qualifiedName!!) },
            icon = {
        Icon(
            imageVector = item.icon,
            contentDescription = stringResource(item.title)
        ) },
    label = { Text(stringResource(item.title)) // ✅ reduz texto)
    },
    //alwaysShowLabel = true
    colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.surfaceBright,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
)
}




/*
NavigationBarItem(
selected = currentDestination == Route.BookList::class.qualifiedName,
onClick = { onNavigationChanged(Route.BookList::class.qualifiedName!!) },
icon = {
    Icon(Icons.Default.MenuBook, contentDescription = "Biblioteca", modifier = Modifier.size(20.dp) // ✅ reduz ícone
    ) },
label = { Text(text = "Biblioteca", fontSize = 10.sp) // ✅ reduz texto)
},
alwaysShowLabel = true
)

NavigationBarItem(
selected = currentDestination == Route.BookLibrary::class.qualifiedName,
onClick = { onNavigationChanged(Route.BookLibrary::class.qualifiedName!!) },
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
*/




}









}





















@Composable
fun MainContentScreen(
    navController: NavHostController,
    contentPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Route.BookGraph,
        modifier = Modifier // Removi o padding daqui para controlar dentro de cada tela se necessário
    ) {
        navigation<Route.BookGraph>(
            startDestination = Route.BookList
        ) {
            // --- TELA: BIBLIOTECA (LISTA GERAL) ---
            composable<Route.BookList> { entry ->
                val viewModel = koinViewModel<BookListViewModel>()
                val selectedBookViewModel = entry.sharedKoinViewModel<SelectedBookViewModel>(navController)

                // REMOVIDO: onSelectedBook(null) para não apagar o estado ao voltar

                BookListScreenRoot(
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding()),
                    onBookClick = { book ->
                        selectedBookViewModel.onSelectedBook(book)
                        navController.navigate(Route.BookDetail(book.id))
                    },
                    onClickNavigate = {
                        navController.navigate(Route.BookSearch)
                    }
                )
            }

            // --- TELA: MEUS LIVROS (LIBRARY) ---
            composable<Route.BookLibrary> { entry ->
                val viewModel = koinViewModel<BookLibraryViewModel>()
                val selectedBookViewModel = entry.sharedKoinViewModel<SelectedBookViewModel>(navController)

                BookLibraryScreenRoot(
                    viewModel = viewModel,
                    modifier = Modifier.padding(
                        top = contentPadding.calculateTopPadding(),
                        bottom = contentPadding.calculateBottomPadding()
                    ),
                    onBookClick = { book ->
                        selectedBookViewModel.onSelectedBook(book)
                        navController.navigate(Route.BookDetail(book.id))
                    }
                )
            }

            composable<Route.Profile> {
                val viewModel = koinViewModel<ProfileViewModel>()

                ProfileScreenRoot(

                    modifier = Modifier.padding(
                        top = contentPadding.calculateTopPadding(),
                        bottom = contentPadding.calculateBottomPadding()
                    ),
                    viewModel = viewModel
                )
            }



            // --- TELA: DETALHES ---
            composable<Route.BookDetail> { entry ->
                val viewModel = koinViewModel<BookDetailViewModel>()
                val selectedBookViewModel = entry.sharedKoinViewModel<SelectedBookViewModel>(navController)

                // Coleta o livro selecionado do ViewModel compartilhado
                val selectedBookState by selectedBookViewModel.selectedBook.collectAsStateWithLifecycle()

                // Sincroniza o detalhe sempre que o livro mudar
                LaunchedEffect(selectedBookState) {
                    selectedBookState?.let { book ->
                        viewModel.onAction(BookDetailAction.OnSelectedBookChange(book))
                    }
                }






                BookDetailScreenRootw(
                    viewModel = viewModel,
                    onBackClicks = {
                        navController.navigateUp()
                    }
                )
            }

            // --- TELA: PESQUISA ---
            composable<Route.BookSearch> { entry ->
                val viewModel = koinViewModel<BookSearchViewModel>()
                val selectedBookViewModel = entry.sharedKoinViewModel<SelectedBookViewModel>(navController)

                SearchScreenRoot(
                    viewModel = viewModel,
                    OnBookClickSearch = { book ->
                        selectedBookViewModel.onSelectedBook(book)
                        navController.navigate(Route.BookDetail(book.id))
                    },
                    onButtonSearchBack = {
                        navController.navigateUp()
                    },
                    OnNavigateFilterScreen = {
                        navController.navigate(Route.Filter)
                    }
                )
            }

            // --- TELA: FILTRO ---
            composable<Route.Filter> { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry<Route.BookSearch>()
                }
                val viewModel = koinViewModel<BookSearchViewModel>(
                    viewModelStoreOwner = parentEntry
                )

                FilterScreenRoot(
                    viewModel = viewModel,
                    OnNavigateFilterBack = {
                        navController.navigateUp()
                    },
                    OnButtonFilterBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}









