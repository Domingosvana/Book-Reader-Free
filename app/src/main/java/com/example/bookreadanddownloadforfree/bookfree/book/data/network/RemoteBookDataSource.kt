package com.example.bookreadanddownloadforfree.bookfree.book.data.network

import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.BookWorkDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.GoogleBooksResponseDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.GutendexResponseDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.SearchResponseDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.TrendingResponseDto
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError

//package com.example.bookreadanddownloadforfree.bookfree.book.data.network

import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.GoogleAccount
//import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
//import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError


interface RemoteBookDataSource {
    suspend fun searchBookOpenLibrary(
        query: String,
        resultLimit: Int? = null,
        page: Int,
       // idiomas:String
    ): AppResult<SearchResponseDto, DataError.Remote>

    suspend fun getBookDetails(bookWorkId: String):  AppResult<BookWorkDto, DataError.Remote>


    suspend fun searchGutendex(
        query: String,
        resultLimit: Int? =null,
        page: Int
    ): AppResult<GutendexResponseDto, DataError.Remote>




    suspend fun searchGoogleBooks(
        query: String,
        resultLimit: Int? =null,
        page: Int
    ): AppResult<GoogleBooksResponseDto, DataError.Remote>



    suspend fun getPopularBookOpenLibrary(
            period: String,
            resultLimit: Int?,
            page: Int
    ):AppResult<TrendingResponseDto, DataError.Remote>




    suspend fun  getPopularGoogleBooks(
        query: String,
        resultLimit: Int? = null,
        page: Int
    ): AppResult<GoogleBooksResponseDto, DataError.Remote>


    //suspend fun searchBooksCombined(query: String): AppResult<List<Book>, DataError.Remote>




        // Usamos o token que o Google nos dá para autenticar no Firebase
      /*  suspend fun signInWithGoogle(
            idToken: String
        ): AppResult<GoogleAccount, DataError.Remote>

        suspend fun signOut(): AppResult<Unit, DataError.Remote>

       */




}