package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count




import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.UiText
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreadanddownloadforfree.bookfree.book.data.helper.GoogleAuthUiClient
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.AuthRepository
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.presentation.toUiText
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
        onGoogleClick = {
            // Disparo imediato usando o escopo da UI (estilo Ahmed)
            scope.launch {
                viewModel.setLoading(true)
                try {
                    val idToken = viewModel.googleAuthUiClient.signIn(context)
                    viewModel.onGoogleSignInResult(idToken ?: "")
                } catch (e: Exception) {
                    viewModel.setLoading(false)
                }
            }
        }
    )
}

@Composable
fun ScreenCount(
    isLoading: Boolean = false,
    onGoogleClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(100.dp))

                ImageTitle(
                    image = R.drawable.ic_launcher_foreground,
                    title = "Book Free"
                )

                Spacer(modifier = Modifier.height(280.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    GroupSocialButtons(
                        isLoading = isLoading,
                        onGoogleClick = onGoogleClick,
                        modifier = Modifier.padding(bottom = 30.dp),
                    )
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ImageTitle(
    @DrawableRes image: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(12.dp),
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
fun GroupSocialButtons(
    isLoading: Boolean = false,
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            SocialButton(
                icon = R.drawable.ic_google,
                title = R.string.sign_with_google,
                onClick = onGoogleClick,
                enabled = !isLoading
            )
        }
    }
}

@Composable
fun SocialButton(
    icon: Int,
    title: Int,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        border = ButtonDefaults.outlinedButtonBorder,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.LightGray
        ),
        shape = RoundedCornerShape(32.dp),
        enabled = enabled
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
                color = if (enabled) Color.Black else Color.Gray
            )
        }
    }
}







class BookCountViewModel(
    private val authRepository: AuthRepository,
    val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    var state by mutableStateOf(BookCountState())
        private set

    fun setLoading(loading: Boolean) {
        state = state.copy(isLoading = loading, errorMessage = null)
    }

    fun onGoogleSignInResult(idToken: String) {
        if (idToken.isBlank()) {
            state = state.copy(isLoading = false)
            return
        }

        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            state = when (result) {
                is AppResult.Success -> {
                    state.copy(
                        isLoading = false,
                        isSignInSuccessful = true,
                        userAccount = result.data
                    )
                }
                is AppResult.Failure -> {
                    state.copy(
                        isLoading = false,
                        errorMessage = result.error.toUiText()
                    )
                }
            }
        }
    }
}

data class BookCountState(
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val errorMessage: UiText? = null,
    val userAccount: GoogleAccount? = null
)

// O objeto BookCount pode ser mantido para outras navegações se necessário
sealed interface BookCount {
    data object OnGoogleClick : BookCount
    data object NavigateToMainScreen : BookCount
}

data class GoogleAccount(
    val token: String,
    val displayName: String,
    val photoUrl: String?,
    val email: String
)


/*
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.data.helper.GoogleAuthUiClient
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.AuthRepository
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
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
                    viewModel.onAction(BookCount.OnGoogleClick)
                    scope.launch {
                        try {

                            val idToken = viewModel.googleAuthUiClient.signIn(context)
                            viewModel.onGoogleSignInResult(idToken ?: "")
                        } catch (e: Exception) {
                            // Se der erro ou cancelamento, destrava a UI
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
    isLoading: Boolean = false,
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

                ImageTitle(
                    image = R.drawable.ic_launcher_foreground,
                    title = "Book Free"
                )

                Spacer(modifier = Modifier.height(280.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    GroupSocialButtons(
                        isLoading = isLoading, // Passando o estado de loading
                        onGoogleClick = { onAction(BookCount.OnGoogleClick) },
                        modifier = Modifier.padding(bottom = 30.dp),
                    )
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ImageTitle(
    @DrawableRes image: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(12.dp),
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
fun GroupSocialButtons(
    isLoading: Boolean = false,
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            SocialButton(
                icon = R.drawable.ic_google,
                title = R.string.sign_with_google,
                onClick = onGoogleClick,
                enabled = !isLoading // Botão desabilitado se estiver carregando
            )
        }
    }
}

@Composable
fun SocialButton(
    icon: Int,
    title: Int,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        border = ButtonDefaults.outlinedButtonBorder,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.LightGray
        ),
        shape = RoundedCornerShape(32.dp),
        enabled = enabled
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
                color = if (enabled) Color.Black else Color.Gray
            )
        }
    }
}

// --- VIEW MODEL ---

class BookCountViewModel(
    private val authRepository: AuthRepository,
    val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    var state by mutableStateOf(BookCountState())
        private set

    fun onAction(action: BookCount) {
        when (action) {
            is BookCount.OnGoogleClick -> {
                state = state.copy(isLoading = true, errorMessage = null)
            }
            BookCount.NavigateToMainScreen -> {
                state = state.copy(isSignInSuccessful = false)
            }
            else -> Unit
        }
    }

    fun onGoogleSignInResult(idToken: String) {
        // Se o token for vazio, apenas desliga o loading
        if (idToken.isBlank()) {
            state = state.copy(isLoading = false)
            return
        }

        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)

            state = when (result) {
                is AppResult.Success -> {
                    state.copy(
                        isLoading = false,
                        isSignInSuccessful = true,
                        userAccount = result.data
                    )
                }
                is AppResult.Failure -> {
                    state.copy(
                        isLoading = false,
                        errorMessage = result.error.toUiText()
                    )
                }
            }
        }
    }
}
// --- MODELS E INTERFACES ---

sealed interface BookCount {
    data object OnGoogleClick : BookCount
    object NavigateToMainScreen : BookCount
    data object OnFacebookClick : BookCount
}

data class BookCountState(
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val errorMessage: UiText? = null,
    val userAccount: GoogleAccount? = null
)

data class GoogleAccount(
    val token: String,
    val displayName: String,
    val photoUrl: String?,
    val email: String
)

 */