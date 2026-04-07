package com.example.bookreadanddownloadforfree.bookfree.book.data.repository

import com.example.bookreadanddownloadforfree.bookfree.book.data.network.RemoteAuthDataSource
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.GoogleAccount
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError

class AuthRepositoryImpl(
    private val remoteAuthDataSource: RemoteAuthDataSource
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): AppResult<GoogleAccount, DataError.Remote> {
        return remoteAuthDataSource.signInWithGoogle(idToken)
    }

    override suspend fun signOut(): AppResult<Unit, DataError.Remote> {
        return remoteAuthDataSource.signOut()
    }
}