package com.example.bookreadanddownloadforfree.bookfree.book.data.network

import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.GoogleBooksResponseDto
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.GoogleAccount
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError

interface RemoteAuthDataSource {
    // Usamos o token que o Google nos dá para autenticar no Firebase
    suspend fun signInWithGoogle(
        idToken: String
    ): AppResult<GoogleAccount, DataError.Remote>

    suspend fun signOut(): AppResult<Unit, DataError.Remote>
}
