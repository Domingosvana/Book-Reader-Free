package com.example.zlib.bookpedia.book.data.database



import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Room

import android.content.Context
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookDao
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookPopularDao
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookPopularEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.SearchBookDao
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.SearchBookEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.StringListTypeConverter
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.UserInterestDao
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.UserInterestEntity


@Database(
    entities = [BookEntity::class, SearchBookEntity::class, BookPopularEntity::class, UserInterestEntity::class],
    version = 12,
    exportSchema = false // ✅ importante ativar se quiser exportar o schema
)
@TypeConverters(StringListTypeConverter::class)
abstract class BookDatabase : RoomDatabase() {
    abstract val bookDao: BookDao
    abstract  val searchBookDao: SearchBookDao

    abstract  val bookPopularDao: BookPopularDao

    abstract  val userInterestDao: UserInterestDao

    companion object {
        const val DB_NAME = "book.db"

        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getInstance(context: Context): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    DB_NAME
                ).fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}











/*
@Database(
    entities = [BookEntity::class],
    version = 1
)
@TypeConverters(
    StringListTypeConverter::class
)
@ConstructedBy(BookDatabaseConstructor::class)
abstract class FavoriteBookDatabase: RoomDatabase() {
    abstract val favoriteBookDao: FavoriteBookDao

    companion object {
        const val DB_NAME = "book.db"
    }
}

 */