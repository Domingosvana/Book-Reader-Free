package com.example.bookreadanddownloadforfree.bookfree.book.data.repository

import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.GoogleAccount
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): AppResult<GoogleAccount, DataError.Remote>
    suspend fun signOut(): AppResult<Unit, DataError.Remote>
}