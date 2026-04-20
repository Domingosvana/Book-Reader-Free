package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_profile

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.BookRepository
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_libraryscreen.components.LibraryScreenTopBar
import com.example.bookreadanddownloadforfree.ui.theme.BookReadAndDownloadForFreeTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreenRoot(
    viewModel: ProfileViewModel ,
    modifier: Modifier
) {

    val state by viewModel.state.collectAsStateWithLifecycle()


    ProfileScreen(
        state = state,

    )

}

@Composable
fun ProfileScreen(
    state: BookProfileState,

) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

    ) {

Column(
    verticalArrangement = Arrangement.spacedBy(20.dp)
) {
    LibraryScreenTopBar(
        title =stringResource(id = R.string.your_profile)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 20.dp, end = 20.dp, top = 16.dp),

        ) {



        ProfileProperty(
            R.string.e_mail,state.userEmail
        )
        ProfileProperty( R.string.numberfavorite,state.totalDeFavorite)
        ProfileProperty( R.string.dowloandnumber,"00")



    }

}



    }







}









@Composable
fun    ProfileProperty(@StringRes key:Int, value: Any) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = stringResource(id = key),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(text = value.toString(),
            color = MaterialTheme.colorScheme.inverseSurface
        )

    }
    Divider(modifier = Modifier

        .padding(vertical = 14.dp),thickness = 0.8.dp,
        color =MaterialTheme.colorScheme.surfaceBright
    )



}



/*

@Preview
@Composable
private fun  ProfileScreendarkTheme() {

    BookReadAndDownloadForFreeTheme(darkTheme = true) {
        ProfileScreenRoot()
    }


}

 */




class ProfileViewModel(
    private val bookRepository: BookRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

): ViewModel(){

    private val _state = MutableStateFlow(BookProfileState())
    // Use asStateFlow() para que a UI não consiga modificar o estado diretamente
    val state = _state.asStateFlow()


    init {
        observeRepositories()
    }



    private fun observeRepositories() {
        viewModelScope.launch {
            val currentUser = firebaseAuth.currentUser
            val email = currentUser?.email ?: "Usuário não logado"
            bookRepository.countTotalDeFavorite()
                .collect { total ->
                    _state.update { it.copy(
                        userEmail = email,
                        totalDeFavorite = total  // Evita erro de index se a lista estiver vazia
                    )}
                }
        }





        /*
        private fun observeRepositories() {
    viewModelScope.launch {
        combine(
            bookRepository.countTotalDeFavorite(),
            flowOf(firebaseAuth.currentUser?.email ?: "") // Transforma em Flow
        ) { total, email ->
            _state.update { it.copy(
                userEmail = email,
                totalDeFavorite = total.firstOrNull() ?: 0
            )}
        }.collect() // Apenas inicia a coleta
    }
}







class ProfileViewModel(
    private val bookRepository: BookRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(BookProfileState())
    // Use asStateFlow() para que a UI não consiga modificar o estado diretamente
    val state = _state.asStateFlow()

    init {
        observeRepositories()
    }

    private fun observeRepositories() {
        viewModelScope.launch {
            // 1. Pega os dados atuais do usuário logado no Firebase
            val currentUser = firebaseAuth.currentUser
            val email = currentUser?.email ?: "Usuário não logado"
            val name = currentUser?.displayName ?: "Sem nome"
            val photo = currentUser?.photoUrl?.toString() ?: ""

            // 2. Observa o Flow de favoritos do banco de dados
            bookRepository.countTotalDeFavorite()
                .collect { total ->
                    // 3. Atualiza TUDO de uma vez só
                    _state.update { it.copy(
                        userEmail = email,
                        userName = name,
                        userPhoto = photo,
                        totalDeFavorite = total
                    )}
                }
        }
    }
}



         */





    }






}


data class BookProfileState(
    val totalDeFavorite: Int =0,
    var userEmail: String="Sem email",
    val userName:String="Sem nome",
    val userPhoto:String="Sem foto"
)