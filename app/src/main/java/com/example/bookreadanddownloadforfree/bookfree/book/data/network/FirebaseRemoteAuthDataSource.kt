import com.example.bookreadanddownloadforfree.bookfree.book.data.network.RemoteAuthDataSource
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.GoogleAccount
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
// import com.google.firebase.auth.ktx.auth  ← remove se usares getInstance()

//import com.example.bookreadanddownloadforfree.bookfree.book.data.network.RemoteAuthDataSource
// a

import kotlinx.coroutines.tasks.await

class FirebaseRemoteAuthDataSource(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : RemoteAuthDataSource {

    override suspend fun signInWithGoogle(idToken: String): AppResult<GoogleAccount, DataError.Remote> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user

            if (user != null) {
                AppResult.Success(
                    GoogleAccount(
                        token = idToken,
                        displayName = user.displayName ?: "Usuário",
                        photoUrl = user.photoUrl?.toString(),
                        email = user.email ?: "Email não encontrado"
                    )
                )

            } else {
                AppResult.Failure(DataError.Remote.UNKNOWN)
            }
        } catch (e: Exception) {
            // Aqui podes mapear os erros específicos do Firebase para o teu DataError.Remote
            AppResult.Failure(DataError.Remote.SERVER)
        }
    }

    override suspend fun signOut(): AppResult<Unit, DataError.Remote> {
        firebaseAuth.signOut()
        return AppResult.Success(Unit)
    }
}