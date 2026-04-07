package com.example.bookreadanddownloadforfree.bookfree.book.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BookPopularDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertAll(books:List<BookPopularEntity>)

    @Query("SELECT * FROM BookPopularEntity WHERE priority")
    suspend fun getAllPopularBook(): List<BookPopularEntity>

    //@Query("SELECT * FROM BookPopularEntity ORDER BY priority ASC, cachedAtPopular DESC")
  //  suspend fun getAllPopularBook(): List<BookPopularEntity>

    // 💡 NOVA: Retorna a lista de fontes presentes (ex: ["GoogleBooks", "OpenLibrary"])
    @Query("SELECT DISTINCT source FROM BookPopularEntity")
    suspend fun getAvailableSources(): List<String>


    @Query("DELETE FROM BookPopularEntity WHERE cachedAtPopular < :limit")
    suspend fun deleteExpired(limit:Long)

    @Query("DELETE FROM BookPopularEntity")
    suspend fun deleteAll()
   // @Transaction // Garante que se um falhar, nada muda (segurança de dados)
   // suspend fun refreshPopularBooks(books: List<BookPopularEntity>) {
   //     deleteAllPopular()
   //     insertAll(books)
   // }








    @Query("""
    SELECT * FROM BookPopularEntity 
    WHERE title LIKE '%' || :term || '%' OR authors LIKE '%' || :term || '%'
    ORDER BY priority ASC 
    LIMIT :limit
""")
    suspend fun getBooksBySpecificTerm(term: String, limit: Int = 5): List<BookPopularEntity>
/*
    @Query("""
    SELECT * FROM BookPopularEntity 
    WHERE (
        -- Verifica se o autor ou título contém qualquer um dos TOP 3 interesses do usuário
        EXISTS (
            SELECT 1 FROM UserInterestEntity 
            WHERE (BookPopularEntity.authors LIKE '%' || term || '%'
               OR BookPopularEntity.title LIKE '%' || term || '%')
            ORDER BY count DESC LIMIT 3
        )
    )
    ORDER BY priority ASC 
    LIMIT 50
""")

 */
//    suspend fun getPersonalizedRecommendations(): List<BookPopularEntity>

  //  suspend fun getPersonalizedRecommendations(): List<BookPopularEntity>





}
//UserInterestEntity






