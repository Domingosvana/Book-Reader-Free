import com.example.bookreadanddownloadforfree.bookfree.book.data.network.RemoteAuthDataSource
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.GoogleAccount
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
// import com.google.firebase.auth.ktx.auth  ← remove se usares getInstance()

//import com.example.bookreadanddownloadforfree.bookfree.book.data.network.RemoteAuthDataSource
// a

import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseRemoteAuthDataSource(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : RemoteAuthDataSource {

    override suspend fun signInWithGoogle(idToken: String): AppResult<GoogleAccount, DataError.Remote> {
        return withContext(Dispatchers.IO) { // Move o processamento para a thread de rede/banco
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val user = authResult.user

                if (user != null) {
                    AppResult.Success(
                        GoogleAccount(
                            token = idToken,
                            displayName = user.displayName ?: "Usuário",
                            photoUrl = user.photoUrl?.toString(),
                            email = user.email ?: ""
                        )
                    )
                } else {
                    AppResult.Failure(DataError.Remote.UNKNOWN)
                }
            } catch (e: Exception) {
                AppResult.Failure(DataError.Remote.SERVER)
            }
        }
    }

    override suspend fun signOut(): AppResult<Unit, DataError.Remote> {
        firebaseAuth.signOut()
        return AppResult.Success(Unit)
    }
}