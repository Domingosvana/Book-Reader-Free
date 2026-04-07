package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


class SplashViewModel (
   // private val checkSessionUseCase: CheckSessionUseCase,
    // Se usares Koin, o FirebaseAuth pode ser injetado aqui
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
    ) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination = _destination.asStateFlow()


   // private val _hasSessionState = MutableStateFlow<Boolean?>(null)
   // val hasSession: StateFlow<Boolean?> = _hasSessionState

    init {
        viewModelScope.launch {
            // REDUZA O TEMPO AQUI (Ex: 1500ms = 1.5 segundos)
            delay(1000)
            _isLoading.value = false
        }

            checkAuthentication()

    }



    private fun checkAuthentication(){
       /* checkSessionUseCase().onEach { authenticated ->
            _hasSessionState.value = authenticated
            Log.d("SplashViewModel", "checkAuthentication: $authenticated")
        }.launchIn(viewModelScope)

        */

        viewModelScope.launch {
            // 1. O Firebase verifica se existe uma sessão ativa no telemóvel
            val currentUser = firebaseAuth.currentUser

            // 2. Decidimos o destino com base no utilizador
            if (currentUser != null) {
                _destination.value = SplashDestination.Home
            } else {
                _destination.value = SplashDestination.Auth
            }

            // 3. Pequeno delay para a Splash não "piscar" muito rápido
            delay(1000)

            // 4. Liberta a Splash Screen para a MainActivity prosseguir
            _isLoading.value = false
        }




    }



    /*
    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY]!!.applicationContext
                val service = KingBurguerService.create()
                val localStorage = KingBurguerLocalStore(application)
                val repository = KingBurguerRepository(service, localStorage)
                SplashViewModel(repository)
            }
        }
    }

     */
}

// No teu SplashViewModel.kt ou num ficheiro de estados
sealed class SplashDestination {
    object Loading : SplashDestination()
    object Auth : SplashDestination()    // Vai para a ScreenCount (Login)
    object Home : SplashDestination()    // Vai para a Main (Lista de Livros)
}