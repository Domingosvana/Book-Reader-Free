package com.example.bookreadanddownloadforfree.bookfree.book.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class MainViewModel (
   /// private val localStore: KingBurguerLocalStore,
): ViewModel() {
/*
    private val _uiState = MutableStateFlow(false) // false -> permanecer | true -> DEVE SAIR

    val uiStorage: StateFlow<Boolean> = _uiState.asStateFlow()


    fun logout(){
        viewModelScope.launch {
            localStore.updateUserCredential(UserCredentisls())
            _uiState.value = true
        }
    }



    fun reset(){
        _uiState.value = false
    }

/*
    companion object{
        val factory = viewModelFactory {
            initializer{
                val application = this[APPLICATION_KEY]!!.applicationContext
                val localStore = KingBurguerLocalStore(application)
                MainViewModel(localStore)
            }

            }
        }

 */

 */



}