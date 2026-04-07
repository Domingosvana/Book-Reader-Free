package com.example.bookreadanddownloadforfree.bookfree.book.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertAll(books:List<SearchBookEntity>)

    // Use crases `` em volta de query para o Room saber que é o nome da coluna
    @Query("SELECT * FROM search_books WHERE `query` = :query ORDER BY position ASC")
    suspend fun getBooksByQuery(query: String): List<SearchBookEntity>

    @Query("SELECT * FROM search_books WHERE id = :id")
    suspend fun getDescriptionById(id: String): SearchBookEntity?

    @Query("DELETE FROM search_books WHERE cachedAt < :limit")
    suspend fun deleteExpired(limit:Long)

    @Query("""
    SELECT * FROM search_books
    WHERE title LIKE '%' || :term || '%' OR authors LIKE '%' || :term || '%'
    ORDER BY cachedAt DESC 
    LIMIT :limit
""")
    suspend fun getSearchBooksBySpecificTerm(term: String, limit: Int = 5): List<SearchBookEntity>
}