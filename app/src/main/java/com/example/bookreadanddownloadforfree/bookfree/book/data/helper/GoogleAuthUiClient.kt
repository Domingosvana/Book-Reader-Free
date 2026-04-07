package com.example.bookreadanddownloadforfree.bookfree.book.data.helper

import android.content.Context
import android.credentials.GetCredentialException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest

import com.ango.foodhub.ui.constid.GoogleServiceClientID
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthUiClient(
    // Remova o 'val context' do construtor se quiser, ou mantenha apenas o manager
    private val credentialManager: androidx.credentials.CredentialManager
) {
    // Agora a função recebe o contexto da Activity aqui!
    suspend fun signIn(activityContext: android.content.Context): String? {
        return try {
            val request = buildSignInRequest(activityContext)

            // USAMOS O CONTEXTO DA ACTIVITY AQUI
            val result = credentialManager.getCredential(activityContext, request)

            val credential = result.credential
            if (credential is androidx.credentials.CustomCredential &&
                credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                val googleIdTokenCredential = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)
                googleIdTokenCredential.idToken
            } else null
        } catch (e: Exception) {
            android.util.Log.e("GoogleAuth", "Erro: ${e.message}")
            null
        }
    }

    private fun buildSignInRequest(context: android.content.Context): androidx.credentials.GetCredentialRequest {
        val googleIdOption = com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(com.ango.foodhub.ui.constid.GoogleServiceClientID)
            .setAutoSelectEnabled(true)
            .build()

        return androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }
}