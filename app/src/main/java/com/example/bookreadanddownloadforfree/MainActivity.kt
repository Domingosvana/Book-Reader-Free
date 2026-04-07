package com.example.bookreadanddownloadforfree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bookreadanddownloadforfree.bookfree.app.App
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.SplashViewModel
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.FilterScreen
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.BookListScreenRoot
import com.example.bookreadanddownloadforfree.ui.theme.BookReadAndDownloadForFreeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        // A instalação deve vir ANTES do super.onCreate
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Esta condição "prende" a splash enquanto isLoading for true
        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }
        setContent {
            BookReadAndDownloadForFreeTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    App()
                    //FilterScreen()
                }
            }

        }

       // CoroutineScope(Dispatchers.IO).launch {
         //   delay(2000)
           // viewModel.isLoading.value = false
       // }

    }
}

