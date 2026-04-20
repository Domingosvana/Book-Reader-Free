package com.example.bookreadanddownloadforfree.bookfree.book.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface BookDao {


    @Upsert
    suspend fun upsert(books: BookEntity)

    @Query("SELECT * FROM BookEntity ORDER BY favoritedAt DESC")
    fun getFavoriteBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM BookEntity  WHERE id = :id ORDER BY favoritedAt DESC")
    suspend fun getBookFavoriteById(id:String): BookEntity?


    @Query("DELETE  FROM BookEntity WHERE id = :id AND languages = :languages")
    suspend fun  deletedFavoriteBook(id: String, languages: String)


    @Query("SELECT  COUNT(*)   FROM BookEntity")
    fun countTotalDeFavorite(): Flow<Int>

    //@Query("SELECT * FROM BookEntity")
    //suspend fun  deletedAllFavoriteBook(): Flow<List<BookEntity>>


    @Query("""
    SELECT * FROM BookEntity 
    WHERE EXISTS (
        SELECT 1 FROM UserInterestEntity 
        WHERE (BookEntity.authors LIKE '%' || term || '%' 
           OR BookEntity.title LIKE '%' || term || '%')
    )
    LIMIT 10
""")
    suspend fun getRecommendationsFromFavorites(): List<BookEntity>

}