package com.example.bookreadanddownloadforfree.bookfree.app

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.MainScreenRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.BookCountViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.ScreenCountRoot
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.SplashDestination
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.SplashViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.navigation
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

@Composable
fun App(
    viewModel: SplashViewModel = koinViewModel()
) {
    val splashDestination by viewModel.destination.collectAsState()

    MaterialTheme {

        val navController = rememberNavController()

        if (splashDestination is SplashDestination.Loading) {
            Box(modifier = Modifier.fillMaxSize())
        } else {
            Scaffold { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Route.BookGraph,
                    modifier = Modifier.padding(padding)
                ) {
                    navigation<Route.BookGraph>(
                        startDestination = if (splashDestination is SplashDestination.Home)
                            Route.Main else Route.Count
                    ) {

                        composable<Route.Count> {
                            val viewModel = koinViewModel<BookCountViewModel>()
                            ScreenCountRoot(
                                viewModel = viewModel,
                                onSignInSuccess = {
                                    navController.navigate(Route.Main)
                                }
                            )
                        }

                        composable<Route.Main> {
                            MainScreenRoot()
                        }
                    }
                }
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}