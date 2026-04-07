package com.example.bookreadanddownloadforfree.bookfree.core.koin_id




import android.app.Application
import androidx.room.Database
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.room.Room
import com.example.zlib.bookpedia.book.data.database.BookDatabase
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            get<Application>(), // pega o Application context do Koin
            BookDatabase::class.java,
            BookDatabase.DB_NAME
        )
            .setDriver(BundledSQLiteDriver()) // aqui você usa o driver bundled
            .fallbackToDestructiveMigration()
            .build()

    }

    single { get<BookDatabase>().bookDao }
    single { get<BookDatabase>().searchBookDao }
    single { get<BookDatabase>().bookPopularDao }
    single { get<BookDatabase>().userInterestDao }
    // Declara a BookLocalStore como um singleton
    //single { BookLocalStore(androidContext()) }


}
