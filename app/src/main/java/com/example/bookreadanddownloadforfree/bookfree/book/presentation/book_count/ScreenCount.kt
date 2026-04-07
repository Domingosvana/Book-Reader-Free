package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.data.helper.GoogleAuthUiClient
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.AuthRepository
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.onSuccess
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.UiText
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.toUiText
import com.example.bookreadanddownloadforfree.ui.theme.BookReadAndDownloadForFreeTheme
import kotlinx.coroutines.launch


@Composable
fun ScreenCountRoot(
    viewModel: BookCountViewModel,
    onSignInSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = viewModel.state

    LaunchedEffect(state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            onSignInSuccess()
        }
    }

    ScreenCount(
        isLoading = state.isLoading,
        onAction = { action ->
            when(action) {
                BookCount.OnGoogleClick -> {
                    viewModel.onAction(BookCount.OnGoogleClick) // Ativa o loading

                    scope.launch {
                        // O segredo está aqui: passar o 'context' que o Compose providencia
                        // que, nesta hierarquia, é a Activity.
                        val idToken = viewModel.googleAuthUiClient.signIn(context)

                        if (idToken != null) {
                            viewModel.onGoogleSignInResult(idToken)
                        } else {
                            // Se falhar ou cancelar, você pode resetar o loading no ViewModel
                            // criando uma action específica ou chamando com string vazia
                            viewModel.onGoogleSignInResult("")
                        }
                    }
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}





@Composable
fun ScreenCount(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false, // Novo parâmetro
    onAction: (BookCount) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(100.dp))

                imagetitle(
                    image = R.drawable.ic_launcher_foreground,
                    title = "Book Free"
                )

                Spacer(modifier = Modifier.weight(1f)) // Usa weight para empurrar o botão para baixo

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    GroupSocialButtons(
                        onGoogleClick = { onAction(BookCount.OnGoogleClick)
                                        Log.i("GOOGLE", "Clicou no Google")
                                        },
                        modifier = Modifier.padding(bottom = 30.dp),
                    )
                }
            }
        }

        // Se estiver carregando, mostra um progresso sobre a tela
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)), // Escurece levemente
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}














@Composable
fun  imagetitle(
     @DrawableRes image:Int,
     title:String,
     modifier: Modifier = Modifier

) {


    Column(
        modifier = Modifier

                .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            imageVector = ImageVector.vectorResource(id = image),
            contentDescription = title,

        )

        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            style = MaterialTheme.typography.titleMedium
        )

    }




    
    
    

    
}





@Composable
fun  ButtomCount(
    @DrawableRes icon:Int,
    title:String,
    onClickCountGoogle: () -> Unit,
    modifier: Modifier = Modifier

) {


    Column(
        modifier = Modifier

            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        IconButton(
            onClick = onClickCountGoogle,
            shape = RoundedCornerShape(50.dp)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = icon),
                contentDescription = title,

                )


        }

    }









}

























@Composable
fun GroupSocialButtons(
    color: Color = Color.White,
    //onFacebookClick: () -> Unit,
    onGoogleClick: () -> Unit,

  //  function: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column( modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,

        ) {

        }



        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {

            SocialButton(
                icon = R.drawable.ic_google,
                title = R.string.sign_with_google,
                onClick =  {onGoogleClick()}
            )
        }







    }

}


@Preview
@Composable
private fun GroupSocialButtonspreview() {
    GroupSocialButtons(
      //  onFacebookClick = {},
        onGoogleClick = {}
    )
}








@Composable
fun SocialButton(
    icon: Int, title: Int, onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        border = ButtonDefaults.outlinedButtonBorder,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(32.dp),
    ) {
        Row(
            modifier = Modifier.height(38.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(id = title),
                color = Color.Black
            )
        }
    }




}











@Preview(showSystemUi = true, showBackground =true )
@Composable
private fun prevScreenCount() {
    BookReadAndDownloadForFreeTheme(dynamicColor = false, darkTheme = false){
        ScreenCount(){}
    }

}



@Preview(showSystemUi = true, showBackground =true )
@Composable
private fun DarkprevScreenCount() {
    BookReadAndDownloadForFreeTheme(dynamicColor = false, darkTheme = true){
        ScreenCount(){}
    }

}


class BookCountViewModel(
    private val authRepository: AuthRepository,
    val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {


    // O estado que a UI vai observar
    var state by mutableStateOf(BookCountState())
        private set

    fun onAction(action: BookCount) {
        when (action) {
            is BookCount.OnGoogleClick -> {
                state = state.copy(isLoading = true, errorMessage = null)
                // A lógica de disparar a janela do Google
                // continuará no ScreenCountRoot (UI),
                // pois precisa do Context e do CredentialManager.
            }
            BookCount.NavigateToMainScreen -> {
                // Lógica para limpar mensagens após navegar
                state = state.copy(isSignInSuccessful = false)
            }
            else -> Unit
        }
    }

    /**
     * Esta é a função principal que o ScreenCountRoot vai chamar
     * assim que obtiver o idToken do Google.
     */
    fun onGoogleSignInResult(idToken: String) {
        state = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            // 1. Inicia o carregamento
            state = state.copy(isLoading = true, errorMessage = null)

            // 2. Chama o Repositório para validar no Firebase
            val result = authRepository.signInWithGoogle(idToken)



            // 3. Processa o resultado
            state = when (result) {
                is AppResult.Success-> {
                    state.copy(
                        isLoading = false,
                        isSignInSuccessful = true,
                        userAccount = result.data,
                        errorMessage = null
                    )
                }
                is AppResult.Failure -> {
                    state.copy(
                        isLoading = false,
                        isSignInSuccessful = false,
                        errorMessage = result.error.toUiText()
                    )
                }
            }
        }
    }




   // fun onAction(action: BookCount) {



   // }




}







sealed interface BookCount {

    data object OnGoogleClick: BookCount

    object NavigateToMainScreen: BookCount


    data object OnFacebookClick: BookCount

   // data class GoogleSignInClick(val requiredAuthorized: Boolean = false) : BookCount

}



data class BookCountState (
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val errorMessage: UiText? = null,
    val userAccount: GoogleAccount? = null
)



data class GoogleAccount(
    val token: String,
    val displayName: String,
    // val email:String,
    val photoUrl: String?,
    val email: String
)

data class OAuthRequest(
    val token: String,
    val provider: String,
)



