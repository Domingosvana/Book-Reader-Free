package com.example.bookreadanddownloadforfree.bookfree.book.data.network

import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.BookWorkDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.GoogleBooksResponseDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.GutendexResponseDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.SearchResponseDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.TrendingResponseDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.searchBookese
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.toDomainSearchGoogleBookDto
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_count.GoogleAccount
import com.example.bookreadanddownloadforfree.bookfree.core.data.safeCall
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import kotlinx.coroutines.async

import kotlinx.coroutines.coroutineScope








private const val BASE_URL = "https://openlibrary.org"

private const val baseUrl = "https://gutendex.com/books?"
private const val GOOGLE_BOOKS_API_KEY = "AIzaSyA_2m4aztbdI0HAxL32Kt6qYZBimFvTGGM"
private const val GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/volumes"

private const val GOOGLE_BOOKS_BASE_URL_BESTSELLER = "https://www.googleapis.com/books/v1/volumes?q=bestseller&key=AIzaSyA_2m4aztbdI0HAxL32Kt6qYZBimFvTGGM"

//GET https://www.googleapis.com/books/v1/volumes?q=bestseller&key=yourAPIKey

//https://www.googleapis.com/books/v1/volumes?q=love&key=AIzaSyA_2m4aztbdI0HAxL32Kt6qYZBimFvTGGM

class KtorRemoteBookDataSource(
    private val httpClient: HttpClient
) : RemoteBookDataSource {

    override suspend fun searchBookOpenLibrary(
        query: String,
        resultLimit: Int?,
        page: Int,
        // idiomas:String
    ): AppResult<SearchResponseDto, DataError.Remote> {
        return safeCall<SearchResponseDto>(
            execute = {
                httpClient.get("$BASE_URL/search.json") {
                    //val idiomasParam = idiomas
                    // parameter("lang", idiomas)
                    parameter("q", query)
                    parameter("limit", resultLimit)
                    // Adiciona TODOS os idiomas suportados
                    ///
                    //      parameter("language", "eng")
                    //   }
                    parameter(
                        "fields",
                        "key,title,author_name,author_key,cover_edition_key,cover_i,ratings_average,ratings_count,first_publish_year,language,number_of_pages_median,edition_count"
                    )


                }
            }
        )

    }

    override suspend fun getBookDetails(bookWorkId: String):  AppResult<BookWorkDto, DataError.Remote> {
        return safeCall<BookWorkDto> {
            httpClient.get(
                urlString = "${BASE_URL}/works/$bookWorkId.json"
            )
        }
    }



    override suspend fun searchGutendex(
        query: String,
        resultLimit: Int?,
        page: Int
    ): AppResult<GutendexResponseDto, DataError.Remote> {
        return safeCall<GutendexResponseDto>(
            execute = {
                httpClient.get(baseUrl) {
                    //val idiomasParam = idiomas
                    // parameter("lang", idiomas)
                    parameter("q", query)
                    parameter("limit", resultLimit)
                    // Adiciona TODOS os idiomas suportados
                    ///
                    //      parameter("language", "eng")
                    //   }
                    //  parameter("fields","key,title,author_name,author_key,cover_edition_key,cover_i,ratings_average,ratings_count,first_publish_year,language,number_of_pages_median,edition_count")


                }
            }
        )
    }





    override suspend fun searchGoogleBooks(
        query: String,
        resultLimit: Int?,
        page: Int
    ): AppResult<GoogleBooksResponseDto, DataError.Remote> {
        return safeCall<GoogleBooksResponseDto>(
            execute = {
                httpClient.get(GOOGLE_BOOKS_BASE_URL) {
                    parameter("q", query)
                    parameter("key", GOOGLE_BOOKS_API_KEY)
                    parameter("maxResults", 20)

//                    parameter("fields", "items(id,volumeInfo(title,authors,description,imageLinks/thumbnail))")

                }
            }
        )
    }

    override suspend fun getPopularBookOpenLibrary(
        period: String,
        resultLimit: Int?,
        page: Int
    ): AppResult<TrendingResponseDto, DataError.Remote> {
        return safeCall<TrendingResponseDto>(
            execute = {
                httpClient.get("$BASE_URL/trending/$period.json") {
                  //  parameter("limit", resultLimit)
                }
            }
        )

    }

    override suspend fun getPopularGoogleBooks(
        query: String,
        resultLimit: Int?,
        page: Int
    ): AppResult<GoogleBooksResponseDto, DataError.Remote> {
        return safeCall<GoogleBooksResponseDto> (
            execute = {
                httpClient.get(GOOGLE_BOOKS_BASE_URL) {
                    parameter("q",query.ifBlank {"bestseller"})
                    parameter("key",GOOGLE_BOOKS_API_KEY)
                    parameter("maxResults",1)

                }
            }
        )
    }
/*
    override suspend fun signInWithGoogle(idToken: String): AppResult<GoogleAccount, DataError.Remote> {
        return safeCall<GoogleAccount> {
            // Endpoint do Google para validar o ID Token
            httpClient.post("https://oauth2.googleapis.com/tokeninfo") {
                parameter("id_token", idToken)
            }
        }
    }

    override suspend fun signOut(): AppResult<Unit, DataError.Remote> {
        // Para o Ktor/Web é apenas limpar os tokens locais
        return AppResult.Success(Unit)
    }

 */

}


// parameter("fields", "key,title,author_name,author_key,cover_edition_key,cover_i,ratings_average,ratings_count,first_publish_year,language,number_of_pages_median,edition_count,isbn")
//


/*class BookRepositoryImpl(
    private val openLibrary: OpenLibraryDataSource,
    private val googleBooks: GoogleBooksDataSource,
    private val gutenberg: GutenbergDataSource,
    private val itBook: ItBookDataSource,
    private val libgen: LibgenDataSource
) : BookRepository {

    override suspend fun searchBooks(query: String): List<Book> {
        val baseBooks = openLibrary.searchBooks(query)

        val enrichedBooks = baseBooks.map { book ->
            var enriched = book

            // idioma ausente → tentar Google Books
            if (enriched.language.isNullOrBlank()) {
                enriched = googleBooks.enrichBook(enriched)
            }

            // formato ausente → tentar Gutenberg
            if (enriched.format.isNullOrBlank()) {
                enriched = gutenberg.enrichBook(enriched)
            }

            // se for técnico e ainda faltam dados → tentar ItBook
            if (isTechnicalBook(enriched.title) && enriched.downloadUrl.isNullOrBlank()) {
                enriched = itBook.enrichBook(enriched)
            }

            // fallback final → Libgen
            if (enriched.downloadUrl.isNullOrBlank()) {
                enriched = libgen.enrichBook(enriched)
            }

            enriched
        }

        // eliminar duplicados
        return enrichedBooks.distinctBy { it.title.lowercase() }
    }

    private fun isTechnicalBook(title: String): Boolean {
        val keywords = listOf("python", "java", "kotlin", "react", "linux", "sql", "docker")
        return keywords.any { title.lowercase().contains(it) }
    }
}

 */
