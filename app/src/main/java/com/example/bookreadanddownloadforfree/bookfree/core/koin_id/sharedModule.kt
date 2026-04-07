package com.example.bookreadanddownloadforfree.bookfree.core.koin_id


import FirebaseRemoteAuthDataSource
import androidx.lifecycle.SavedStateHandle
import com.example.bookreadanddownloadforfree.bookfree.book.data.helper.GoogleAuthUiClient
import com.example.bookreadanddownloadforfree.bookfree.book.data.network.KtorRemoteBookDataSource
import com.example.bookreadanddownloadforfree.bookfree.book.data.network.RemoteAuthDataSource
import com.example.bookreadanddownloadforfree.bookfree.book.data.network.RemoteBookDataSource
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.AuthRepository
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.AuthRepositoryImpl
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.BookRepository
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.BookRepositoryImpI
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.MainViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.BookCountViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.SplashViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail.BookDetailViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.FilterViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.BookLibraryViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.BookListViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.SelectedBookViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.BookLocalStore
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.BookSearchViewModel
import com.example.bookreadanddownloadforfree.bookfree.core.data.HttpClientFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {

    // HttpClientEngine
    single<HttpClientEngine> { OkHttp.create() }
    // HttpClient
    single{ HttpClientFactory.create(get()) }


    single { BookLocalStore(androidContext()) }

    // 1. O DataSource (Firebase)
    single<RemoteAuthDataSource> { FirebaseRemoteAuthDataSource() }

    single<AuthRepository> { AuthRepositoryImpl(get()) }


    // DataSource -> Repository
    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>()
    singleOf(::BookRepositoryImpI).bind<BookRepository>()

// 3. O Helper para a UI (Precisa do Context e do CredentialManager)
    // 3. O Helper para a UI
    single { androidx.credentials.CredentialManager.create(androidContext()) }
    single {
        GoogleAuthUiClient(

            credentialManager = get<androidx.credentials.CredentialManager>()
        )
    }


    //FirebaseAuth para autenticacao
    single { com.google.firebase.auth.FirebaseAuth.getInstance() }


    // ViewModel
    viewModelOf(::BookListViewModel)
    viewModelOf(::SelectedBookViewModel)
    viewModelOf(::BookSearchViewModel,
        )

    viewModel { (handle: SavedStateHandle) ->
        BookDetailViewModel(
            handle,
            get()
        )
    }

    viewModel { (handle: SavedStateHandle) ->
        BookLibraryViewModel(
            handle,
            get(),

        )
    }




    viewModelOf(::BookDetailViewModel)
   // viewModelOf(::SelectedBookViewModel)

    viewModelOf(::BookLibraryViewModel)

viewModelOf(::FilterViewModel)

    viewModelOf(::MainViewModel)

    viewModel {
        BookCountViewModel(
            authRepository = get(),
            googleAuthUiClient = get()
        )
    }

    viewModelOf(::SplashViewModel)



   // viewModelOf(::BookCountViewModel(get(), get()))

}