package com.example.bookreadanddownloadforfree.bookfree.book.data.repository

import androidx.room.Query
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.domain.BookOpenLibrary
import com.example.bookreadanddownloadforfree.bookfree.book.domain.InterestType
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError
import com.example.bookreadanddownloadforfree.bookfree.core.domian.EmptyAppResult
import kotlinx.coroutines.flow.Flow

interface BookRepository {


    ///SEARCH-BOOK
    suspend fun searchBookOpenLibrary(query: String): AppResult<List<Book>, DataError.Remote>

    suspend fun getBookDescription(bookId: String): AppResult<String?, DataError>


    suspend fun searchGutendex(query: String): AppResult<List<Book>, DataError.Remote>

    suspend fun searchGoogleBooks(query: String): AppResult<List<Book>, DataError.Remote>


    suspend fun searchBooks(query: String): AppResult<List<Book>, DataError.Remote>

///POPULAR BOOCKS

    suspend fun  getAllPopularBook( query: String,period:String): AppResult<List<Book>, DataError.Remote>

    suspend fun  getPopularBookOpenLibrary(query: String): AppResult<List<Book>, DataError.Remote>

    suspend fun  getPopularGoogleBooks(query: String): AppResult<List<Book>, DataError.Remote >




    //FAVORITOS
    fun getFavoriteBookAll(): Flow<List<Book>>

    fun countTotalDeFavorite():Flow<Int>




    fun isBookFavorite(id: String): Flow<Boolean>

    suspend fun markAsFavorite(book: Book): EmptyAppResult<DataError.Local>

    suspend fun deleteFavoriteBook(id: String,language: String)//: EmptyAppResult<DataError.Local>



    //suspend fun deleteAllFavoriteBook():Flow<List<Book>>//EmptyAppResult<DataError.Local>


// ... (todas as outras funções que já tens: Search, Popular, Favoritos) ...

    // --- RECOMENDAÇÕES E INTERESSES ---

    /**
     * Regista uma interação (clique ou pesquisa) para alimentar as recomendações.
     * @param term O nome do autor ou o título do livro.
     * @param type O tipo de interesse (AUTHOR, CATEGORY, SEARCH_TERM).
     */
    suspend fun recordInteraction(term: String, type: InterestType): EmptyAppResult<DataError.Local>

    /**
     * Obtém a lista de livros recomendados baseada nos interesses guardados.
     */
    suspend fun getRecommendedBooks(): AppResult<List<Book>, DataError.Local>

    /**
     * Verifica se o utilizador já tem algum histórico para mostrar recomendações.
     */
    suspend fun hasUserInterests(): Boolean

   // suspend fun getRecommendationsFromFavorites(): AppResult<List<Book>, DataError.Local>


}