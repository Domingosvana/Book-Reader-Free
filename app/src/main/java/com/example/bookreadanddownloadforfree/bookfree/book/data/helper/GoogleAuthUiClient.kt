package com.example.bookreadanddownloadforfree.bookfree.book.data.helper









/*
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.ango.foodhub.ui.constid.GoogleServiceClientID
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleAuthUiClient(
    private val credentialManager: CredentialManager
) {

    suspend fun signIn(context: Context): String? = withContext(Dispatchers.Main) {
        try {
            val request = buildSignInRequest()
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                googleIdTokenCredential.idToken
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("GoogleAuth", "Erro no signIn: ${e.message}", e)
            null
        }
    }

    private fun buildSignInRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(GoogleServiceClientID) // teu client ID
            .setAutoSelectEnabled(true)           // ← Muito importante para reduzir telas
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }
}

 */





import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.ango.foodhub.ui.constid.GoogleServiceClientID
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
/*
class GoogleAuthUiClient(
    private val credentialManager: CredentialManager,

) {
    // Removido withContext(Dispatchers.Main) daqui para não travar a UI Thread antes da hora
    suspend fun signIn(context: Context): String? {
        return try {
            val request = buildSignInRequest()
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                googleIdTokenCredential.idToken
            } else {
                null
            }
        } catch (e: Exception) {
            // Se o usuário clicar fora ou voltar, relançamos para a Coroutine saber que parou
            if (e is CancellationException) throw e
            Log.e("GoogleAuth", "Erro no signIn: ${e.message}")
            null
        }
    }



    private fun buildSignInRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()

            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(GoogleServiceClientID)
            .setAutoSelectEnabled(true) // Mude para FALSE para evitar "No credentials available"
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }


}

 */


class GoogleAuthUiClient(
    private val credentialManager: CredentialManager
) {
    suspend fun signIn(context: Context): String? {
        return try {
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(
                    GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(GoogleServiceClientID)
                        .setAutoSelectEnabled(false) // Deixe FALSE para o usuário ver a conta
                        .build()
                )
                .build()

            // A chamada precisa ser explicitamente na Main para não haver delay de renderização
            val result = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                credentialManager.getCredential(context = context, request = request)
            }

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } else null
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }
    }
}