package com.example.bookreadanddownloadforfree.bookfree.book.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInterestDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewInterest(interest: UserInterestEntity): Long

    @Query("""
        UPDATE UserInterestEntity 
        SET count = count + 1, lastInteracted = :timestamp 
        WHERE term = :term
    """)
    suspend fun incrementInterest(term: String, timestamp: Long = System.currentTimeMillis())

    // Busca os interesses mais fortes (mais clicados) para usarmos na recomendação
    @Query("SELECT term FROM UserInterestEntity ORDER BY count DESC LIMIT 10")
    suspend fun getTopInterests(): List<String>

    @Query("SELECT COUNT(*) FROM UserInterestEntity")
    suspend fun getTotalInterestsCount(): Int
}