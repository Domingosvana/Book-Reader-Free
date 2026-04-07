package com.example.bookreadanddownloadforfree.bookfree.book.data.repository

//package com.example.bookreadanddownloadforfree.bookfree.book.data.repository

import androidx.sqlite.SQLiteException
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookDao
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookPopularDao
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookPopularEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.SearchBookDao
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.UserInterestDao
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.UserInterestEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.cachedAtBookPopularEntity

import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.getFavoriteBookAll
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.markAsFavorite
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.searchBookese
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.toBookPopularEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.toDomain

import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.toDomainSearchGoogleBookDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.toDomainSearchGutenberg
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.toDomainTrending
import com.example.bookreadanddownloadforfree.bookfree.book.data.mappers.toSearchEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.network.RemoteBookDataSource
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.domain.InterestType
import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError
import com.example.bookreadanddownloadforfree.bookfree.core.domian.EmptyAppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.map
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepositoryImpI(
    private val remoteBookDataSource: RemoteBookDataSource,

    private  val bookDao: BookDao,

    private val searchBookDao: SearchBookDao,

    private val bookPopularDao: BookPopularDao,

    private val userInterestDao: UserInterestDao

) : BookRepository {


    companion object {
        private const val CACHE_DURATION_MILLIS = 3 * 24 * 60 * 60 * 1000L // 3 dias
   //     private const val CACHE_DURATION_MILLIS = 3 * 24 * 60 * 60 * 1000L // 3 dias
    //  private const val CACHE_DURATION_MILLIS_POPULAR = 7 * 24 * 60 * 60 * 1000L // 7 dias

        private const val CACHE_DURATION_MILLIS_POPULAR = 10 * 1000L //TEXT

        private const val CACHE_CURTO = 2 * 60 * 1000L // 2 minutos (Incompleto)
        private const val CACHE_LONGO = 7 * 24 * 60 * 60 * 1000L // 7 dias (Completo)


    }


    /**
     * 🚀 Busca combinada em paralelo entre todas as APIs (OpenLibrary, Gutendex e GoogleBooks)
     */
    override suspend fun searchBooks(query: String): AppResult<List<Book>, DataError.Remote> =
        coroutineScope {

            // DECLARAÇÃO: Deve estar no topo do bloco
            val sanitizedQuery = query.trim().lowercase()

            // 1. Limpeza preventiva (Opcional: remove tudo que expirou antes de buscar)
            val expirationLimit = System.currentTimeMillis() - CACHE_DURATION_MILLIS
            searchBookDao.deleteExpired(expirationLimit)

            // 2. Verificar se existe no cache local
            val cachedBooks = searchBookDao.getBooksByQuery(sanitizedQuery)

            // Se houver dados e eles não expiraram (o deleteExpired já ajuda aqui)
            if (cachedBooks.isNotEmpty()) {
                return@coroutineScope AppResult.Success(cachedBooks.map { it.toDomain() })
            }

            // 3. Se não houver cache, buscar nas APIs em paralelo


            try {
                // Executa as 3 requisições em paralelo
                val openLibraryDeferred =
                    async { remoteBookDataSource.searchBookOpenLibrary(query, 40, 1) }




                val gutendexDeferred = async { remoteBookDataSource.searchGutendex(query, 50, 1) }
                val googleDeferred = async { remoteBookDataSource.searchGoogleBooks(query, 20, 1) }

                // Espera todas terminarem
                val openResult = openLibraryDeferred.await()
                //val gutenResult = gutendexDeferred.await()
                val googleResult = googleDeferred.await()

                val allBooks = mutableListOf<Book>()

                // 🧩 Open Library
                if (openResult is AppResult.Success) {
                    allBooks.addAll(openResult.data.results.map { it.searchBookese() })
                }

                // 📚 Gutendex
                // if (gutenResult is AppResult.Success) {
                //     allBooks.addAll(gutenResult.data.results.map { it.toDomainSearchGutenberg() })
                // }

                // 🔎 Google Books
                if (googleResult is AppResult.Success) {
                    allBooks.addAll(googleResult.data.results?.map { it.toDomainSearchGoogleBookDto() }
                        ?: emptyList())
                }

                // 🔁 Remove duplicados pelo título
                // val distinctBooks = allBooks.distinctBy { it.title.lowercase() }
// ✅ 4. SALVAR NO CACHE (Faltava isso aqui na sua função searchBooks!)
                if (allBooks.isNotEmpty()) {
                    val entitiesToCache = allBooks.toSearchEntity(sanitizedQuery)   //  ORREÇÃO AQUI: Usa a função de extensão na LISTA completa//  .map { it.toSearchEntity(sanitizedQuery) }retiramos a mapriacao atraves do nosso entity ja esta mapeiado ou atraves da lista
                    searchBookDao.insertAll(entitiesToCache)
                    // ADICIONE ISSO AQUI PARA TESTAR:
                  //  kotlinx.coroutines.delay(100000) // Faz o app "esperar" 1 minuto
                }









                /*val booksFromDb = searchBookDao.getBooksByQuery(sanitizedQuery)
                AppResult.Success(booksFromDb.map { it.toDomain() })

                 */
                // No searchBooks (perto da linha 118)
                val booksFromDb = searchBookDao.getBooksByQuery(sanitizedQuery)
                    //.distinctBy { it.id } // 👈 ISSO AQUI resolve o crash do LazyColumn
                return@coroutineScope AppResult.Success(booksFromDb.map { it.toDomain() })
               // AppResult.Success(data = allBooks)//distinctBooks
            } catch (e: Exception) {
                AppResult.Failure(DataError.Remote.UNKNOWN)
            }
        }

   // override suspend fun getAllPopularBook(): AppResult<List<Book>, DataError.Remote> {
    //    TODO("Not yet implemented")
   // }


/*
    override suspend fun getAllPopularBook(
        query: String,
        period: String,
    ): AppResult<List<Book>, DataError.Remote> = coroutineScope {

        // 1. Limpeza de expirados (Teus 10 segundos de teste ou 7 dias)
        val expirationLimit = System.currentTimeMillis() - CACHE_DURATION_MILLIS_POPULAR
        bookPopularDao.deleteExpired(expirationLimit)

        // 2. Verifica o que já temos
        val sourcesNoBanco = bookPopularDao.getAvailableSources()
        println("DEBUG: Fontes encontradas no Banco: $sourcesNoBanco")
        val precisaOpenLibrary = !sourcesNoBanco.contains("OpenLibrary")
        val precisaGoogle = !sourcesNoBanco.contains("GoogleBooks")
        println("DEBUG: Precisa de OpenLibrary? $precisaOpenLibrary")
        println("DEBUG: Precisa de Google? $precisaGoogle")
        // 3. Se já temos as DUAS fontes, não gastamos internet!
        if (!precisaOpenLibrary && !precisaGoogle) {
            val cacheCompleto = bookPopularDao.getAllPopularBook()
            return@coroutineScope AppResult.Success(cacheCompleto.map { it.toBookPopularEntity() })
        }

        // 4. Se chegámos aqui, falta alguma API. Vamos buscar SÓ a que falta.
        try {

            val livrosParaSalvar = mutableListOf<BookPopularEntity>()

            if (openResult is AppResult.Success) {
                val books = openResult.data.works.map { it.toDomainTrending() }
                // Forçamos a fonte aqui para não ter erro de comparação depois
                livrosParaSalvar.addAll(books.map { it.cachedAtBookPopularEntity().copy(source = "OpenLibrary") })
            }

            if (googleResult is AppResult.Success) {
                val books = googleResult.data.results?.map { it.toDomainSearchGoogleBookDto() } ?: emptyList()
                // Forçamos a fonte aqui
                livrosParaSalvar.addAll(books.map { it.cachedAtBookPopularEntity().copy(source = "GoogleBooks") })
            }

            if (livrosParaSalvar.isNotEmpty()) {
                println("DEBUG: Inserindo no banco ${livrosParaSalvar.size} livros")
                bookPopularDao.insertAll(livrosParaSalvar)
            }

/*
            val novosLivros = mutableListOf<Book>()

            // Lança as buscas apenas se necessário
            val openLibraryDeferred = if (precisaOpenLibrary) {
                async { remoteBookDataSource.getPopularBookOpenLibrary(period) }
            } else null

            val googleDeferred = if (precisaGoogle) {
                async { remoteBookDataSource.getPopularGoogleBooks(query, 40, 1) }
            } else null

            // Aguarda e processa OpenLibrary
            openLibraryDeferred?.await()?.let { result ->
                if (result is AppResult.Success) {
                    novosLivros.addAll(result.data.works.map { it.toDomainTrending() })
                }
            }

            // Aguarda e processa Google Books
            googleDeferred?.await()?.let { result ->
                if (result is AppResult.Success) {
                    novosLivros.addAll(result.data.results?.map { it.toDomainSearchGoogleBookDto() } ?: emptyList())
                }
            }

            // 5. Guarda no banco os novos dados (o localId automático garante que não apaga os outros)
            if (novosLivros.isNotEmpty()) {
                val entities = novosLivros.map { it.cachedAtBookPopularEntity() }
                bookPopularDao.insertAll(entities)
            }

            // 6. Retorna a união de tudo o que está no banco agora
            val cacheFinal = bookPopularDao.getAllPopularBook()
            AppResult.Success(cacheFinal.map { it.toBookPopularEntity() })

        }
*/
        catch (e: Exception) {
            // Se a internet falhar mas tivermos algum cache parcial, mostramos o que há
            val cacheExistente = bookPopularDao.getAllPopularBook()
            if (cacheExistente.isNotEmpty()) {
                AppResult.Success(cacheExistente.map { it.toBookPopularEntity() })
            } else {
                AppResult.Failure(DataError.Remote.UNKNOWN)
            }

        }




    }

 */
















/*

    override suspend fun getAllPopularBook(
        query: String,
        period: String
    ): AppResult<List<Book>, DataError.Remote> = coroutineScope {

        val expirationLimit = System.currentTimeMillis() - CACHE_DURATION_MILLIS_POPULAR
        bookPopularDao.deleteExpired(expirationLimit)

        val cachedEntities = bookPopularDao.getAllPopularBook()

        val temGoogle = cachedEntities.any { it.source == "GoogleBooks" }
        val temOpen = cachedEntities.any { it.source == "OpenLibrary" }

        if (temGoogle && temOpen) {
            return@coroutineScope AppResult.Success(cachedEntities.map { it.toBookPopularEntity() })
        }

        try {
            val openLibraryDeferred = async { remoteBookDataSource.getPopularBookOpenLibrary(period) }
            val googleDeferred = async { remoteBookDataSource.getPopularGoogleBooks(query, 50, 1) }

            val openResult = openLibraryDeferred.await()
            val googleResult = googleDeferred.await()

            // 💡 Criamos uma lista de ENTIDADES para salvar a prioridade
            val entitiesToInsert = mutableListOf<BookPopularEntity>()
            // Criamos uma lista de DOMÍNIO para retornar à UI
            val allBooksForUi = mutableListOf<Book>()

            // --- PROCESSA OPEN LIBRARY ---
            if (openResult is AppResult.Success) {
                val domainBooks = openResult.data.works.map { it.toDomainTrending() }
                allBooksForUi.addAll(domainBooks)

                // 📍 AQUI ENTRA O MAPINDEXED:
                entitiesToInsert.addAll(domainBooks.mapIndexed { index, book ->
                    book.cachedAtBookPopularEntity().copy(
                        priority = index,
                        source = "OpenLibrary"
                    )
                })
            }

            // --- PROCESSA GOOGLE BOOKS ---
            if (googleResult is AppResult.Success) {
                val domainBooks = googleResult.data.results?.map { it.toDomainSearchGoogleBookDto() } ?: emptyList()
                allBooksForUi.addAll(domainBooks)

                // 📍 AQUI TAMBÉM (Somamos o index ao tamanho da lista anterior para manter a sequência):
                val offset = entitiesToInsert.size
                entitiesToInsert.addAll(domainBooks.mapIndexed { index, book ->
                    book.cachedAtBookPopularEntity().copy(
                        priority = offset + index,
                        source = "GoogleBooks"
                    )
                })
            }

            if (entitiesToInsert.isNotEmpty()) {
                bookPopularDao.deleteAll()
                bookPopularDao.insertAll(entitiesToInsert)
            }

            AppResult.Success(allBooksForUi)

        } catch (e: Exception) {
            AppResult.Success(cachedEntities.map { it.toBookPopularEntity() })
        }
    }

    */




    // Defina as constantes no topo da classe

    override suspend fun getAllPopularBook(
        query: String,
        period: String
    ): AppResult<List<Book>, DataError.Remote> = coroutineScope {

        // 1. Verificar fontes disponíveis e decidir expiração
        val sourcesNoBanco = bookPopularDao.getAvailableSources()
        val temGoogle = sourcesNoBanco.contains("GoogleBooks")
        val temOpen = sourcesNoBanco.contains("OpenLibrary")

        val duracaoCache = if (temGoogle && temOpen) CACHE_LONGO else CACHE_CURTO
        val expirationLimit = System.currentTimeMillis() - duracaoCache

        // 2. Limpeza
        bookPopularDao.deleteExpired(expirationLimit)

        // 3. Buscar o que sobrou no banco
        val cachedEntities = bookPopularDao.getAllPopularBook()
            .distinctBy { it.id } // Garante que no retorno do cache não haja IDs repetidos
        // 4. REGRA: Se o cache estiver completo, entrega o que está no BANCO
        if (cachedEntities.isNotEmpty() && temGoogle && temOpen) {

            return@coroutineScope AppResult.Success(cachedEntities.map { it.toDomain() })
        }

        // 5. Se não tiver no banco (ou incompleto), busca na API
        try {
            val openLibraryDeferred = async { remoteBookDataSource.getPopularBookOpenLibrary(period = "weekly",100,2) }
            val googleDeferred = async { remoteBookDataSource.getPopularGoogleBooks(query, 1, 1) }

            val openResult = openLibraryDeferred.await()
            val googleResult = googleDeferred.await()

            val allBooksFromApi = mutableListOf<Book>()

            if (openResult is AppResult.Success) {
                allBooksFromApi.addAll(openResult.data.works.map { it.toDomainTrending() })

            }

            if (googleResult is AppResult.Success) {
                allBooksFromApi.addAll(googleResult.data.results?.map { it.toDomainSearchGoogleBookDto() } ?: emptyList())
            }

            if (allBooksFromApi.isNotEmpty()) {
                // Mapeia para Entidades (o flatMap vai separar os idiomas aqui)
                val entitiesToCache = allBooksFromApi.toBookPopularEntity()
                  //  .distinctBy { it.id }
                bookPopularDao.deleteAll()
                bookPopularDao.insertAll(entitiesToCache)

            }

            // 🔥 REGRA FINAL: Nunca retorne 'allBooksFromApi'.
            // Busque novamente do BANCO após o insert para garantir a separação de idiomas.
            val finalDataFromDb = bookPopularDao.getAllPopularBook()
                .distinctBy { it.id }
            AppResult.Success(finalDataFromDb.map { it.toDomain() })

        } catch (e: Exception) {
            // Se falhar a rede, tenta entregar o que restou no banco
            val recoveryEntities = bookPopularDao.getAllPopularBook()
                .distinctBy { it.id }
            if (recoveryEntities.isNotEmpty()) {
                AppResult.Success(recoveryEntities.map { it.toDomain() })

            } else {
                AppResult.Failure(DataError.Remote.UNKNOWN)
            }
        }
    }

//Como agora usas o source para decidir o tempo de cache, sempre que adicionares uma terceira
// API (por exemplo, a do New York Times), basta adicioná-la na verificação: val temNYT = sourcesNoBanco.contains("NYT")
// val temTudo = temGoogle && temOpen && temNYT









/*

    override suspend fun getAllPopularBook(
        query: String,
        period: String,

    ): AppResult<List<Book>, DataError.Remote> = coroutineScope {
     //   val sanitizedpopular = query.trim()

        // 1. Limpeza preventiva (Opcional: remove tudo que expirou antes de buscar)
        val expirationLimit = System.currentTimeMillis() - CACHE_DURATION_MILLIS_POPULAR
        bookPopularDao.deleteExpired(expirationLimit)

        // 2. Verificar se existe no cache local
        val cachedBooks = bookPopularDao.getAllPopularBook()

        // Se houver dados e eles não expiraram (o deleteExpired já ajuda aqui)
        if (cachedBooks.isNotEmpty()) {
            return@coroutineScope AppResult.Success(cachedBooks.map { it.toBookPopularEntity() })
        }






        try {
            val openLibraryDeferred =
                async { remoteBookDataSource.getPopularBookOpenLibrary(period = "weekly") }

            val googleDeferred = async { remoteBookDataSource.getPopularGoogleBooks(query, 50, 1) }

            val openlibraryResult = openLibraryDeferred.await()
            val googleResult = googleDeferred.await()

            val allBook = mutableListOf<Book>()

            if (openlibraryResult is AppResult.Success) {
                allBook.addAll(openlibraryResult.data.works.map { it.toDomainTrending() })

            }


            if (googleResult is AppResult.Success) {
                allBook.addAll(googleResult.data.results?.map { it.toDomainSearchGoogleBookDto() }
                    ?: emptyList())

            }

// --- INJEÇÃO NO BANCO (MANTENDO TUDO) ---
            // 4. Injeta os dados no Banco de Dados (Cache)
          //  val sanitizedQuery = query.trim().lowercase()
          //  if (allBook.isNotEmpty()) {
                // Como você quer manter duplicados, não usamos o distinctBy
            //    val entitiesToCache = allBook.map { book ->
           //         book.toSearchEntity(sanitizedQuery) // Seu mapper com timestamp
           //     }
          //      searchBookDao.insertAll(entitiesToCache)
          //  }
            if (allBook.isNotEmpty()){
                val entitiesToCachePopular = allBook.map{it.cachedAtBookPopularEntity()}

                bookPopularDao.insertAll(entitiesToCachePopular)

                // Log para conferires no Logcat se aqui são 5 ou 50+
                println("DEBUG: Inserindo ${entitiesToCachePopular.size} livros no banco de populares")
            }


            AppResult.Success(data = allBook)
        } catch (e: Exception) {
            AppResult.Failure(DataError.Remote.UNKNOWN)
        }


    }

*/






// Dentro do teu BookRepositoryImpl.kt

    override suspend fun recordInteraction(
        term: String,
        type: InterestType
    ): EmptyAppResult<DataError.Local> {
        return try {
            val timestamp = System.currentTimeMillis()

            // 1. Tenta inserir o interesse como novo (count inicial é 1)
            val result = userInterestDao.insertNewInterest(
                UserInterestEntity(
                    term = term,
                    type = type.name, // Convertemos o Enum para String
                    lastInteracted = timestamp
                )
            )

            // 2. Se o resultado for -1L, significa que já existe, então incrementamos
            if (result == -1L) {
                userInterestDao.incrementInterest(term, timestamp)
            }

            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Failure(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getRecommendedBooks(): AppResult<List<Book>, DataError.Local> {
        return try {
            // 1. Pega os Top 5 interesses mais recentes/fortes
            val topInterests = userInterestDao.getTopInterests() // Aquela função que retorna List<String>

            if (topInterests.isEmpty()) {
                return AppResult.Success(emptyList())
            }

            val finalRecommendations = mutableListOf<Book>()

            // 2. Para cada interesse, busca uma fatia de livros
            for (interest in topInterests) {
                val fromPopular = bookPopularDao.getBooksBySpecificTerm(interest, limit = 5)
                    .map { it.toBookPopularEntity() }

                val fromSearch = searchBookDao.getSearchBooksBySpecificTerm(interest, limit = 10)
                    .map { it.toDomain() }

                finalRecommendations.addAll(fromPopular)
                finalRecommendations.addAll(fromSearch)
            }

            // 3. Adiciona os Favoritos (sempre importantes)
            val favorites = bookDao.getRecommendationsFromFavorites()
                .map { it.getFavoriteBookAll(
                    selectedLanguage = String()
                ) }

            // 4. Une tudo, remove duplicados e mantém a ordem
            // (Os interesses mais recentes estarão no topo porque o topInterests já vem ordenado)
            val result = (favorites + finalRecommendations)
                .distinctBy { it.id }
                .take(50)

            AppResult.Success(result)
        } catch (e: Exception) {
            AppResult.Failure(DataError.Local.UNKNOWN)
        }
    }


    /*
    override suspend fun getRecommendedBooks(): AppResult<List<Book>, DataError.Local> {
        return try {
            // 1. O DAO faz o cruzamento inteligente entre interesses e populares
            val entities = bookPopularDao.getPersonalizedRecommendations()

            // 2. Mapeamos as entidades para o nosso modelo de domínio Book
            val domainBooks = entities.map { it.toBookPopularEntity() }

            AppResult.Success(domainBooks)
        } catch (e: Exception) {
            AppResult.Failure(DataError.Local.UNKNOWN)
        }
    }*/

    override suspend fun hasUserInterests(): Boolean {
        return try {
            // Verifica se o contador de termos no banco é maior que zero
            userInterestDao.getTotalInterestsCount() > 0
        } catch (e: Exception) {
            false
        }
    }

   // override suspend fun getRecommendationsFromFavorites(): AppResult<List<Book>, DataError.Local> {
     //   TODO("Not yet implemented")
   // }


    override suspend fun getPopularBookOpenLibrary(query: String): AppResult<List<Book>, DataError.Remote> =

        remoteBookDataSource.getPopularBookOpenLibrary(period = "weekly",100,2)
            .map { dto -> dto.works.map { it.toDomainTrending() } }


    override suspend fun getPopularGoogleBooks(query: String): AppResult<List<Book>, DataError.Remote> =
        remoteBookDataSource.getPopularGoogleBooks(query, 50, 1)
            .map { dto -> dto.results?.map { it.toDomainSearchGoogleBookDto() } ?: emptyList() }




    // Métodos individuais (caso precise de buscas isoladas)
    override suspend fun searchBookOpenLibrary(query: String): AppResult<List<Book>, DataError.Remote> =
        remoteBookDataSource.searchBookOpenLibrary(query, 50, 1)
            .map { dto -> dto.results.map { it.searchBookese() } }





    override suspend fun getBookDescription(bookId: String): AppResult<String?, DataError> {
        // Busca em qualquer tabela de cache (Search ou Popular)
        val cachedDescription = searchBookDao.getDescriptionById(bookId)

        if (cachedDescription != null) return AppResult.Success(cachedDescription.description)

        // Se não tem cache, busca remoto
        val remoteResult = remoteBookDataSource.getBookDetails(bookId)

        if (remoteResult is AppResult.Success) {
            // Opcional: Atualizar o banco com a descrição nova para futuras consultas
            // searchBookDao.updateDescription(bookId, remoteResult.data.description)
        }

        return remoteResult.map { it.description }
    }

















    override suspend fun searchGutendex(query: String): AppResult<List<Book>, DataError.Remote> =
        remoteBookDataSource.searchGutendex(query, 50, 1)
            .map { dto -> dto.results.map { it.toDomainSearchGutenberg() } }

    override suspend fun searchGoogleBooks(query: String): AppResult<List<Book>, DataError.Remote> =
        remoteBookDataSource.searchGoogleBooks(query, 50, 1)
            .map { dto -> dto.results?.map { it.toDomainSearchGoogleBookDto() } ?: emptyList() }


//as informacaes vem do banco de dados


    // 1. Corrigir a leitura dos favoritos
    override fun getFavoriteBookAll(): Flow<List<Book>> {
        return bookDao
            .getFavoriteBooks()
            .map { bookEntities ->
                bookEntities.map { entity ->
                    // ✅ PASSAR o idioma que vem do banco, não um vazio!
                    entity.toDomain()
                }
            }
    }

    override fun countTotalDeFavorite(): Flow<Int> {
        return bookDao.countTotalDeFavorite()
    }

    override fun isBookFavorite(id: String): Flow<Boolean> {
        return bookDao
            .getFavoriteBooks()
            .map { bookEntities -> bookEntities.any { it.id == id } }

    }

    // 2. Corrigir a marcação como favorito
    override suspend fun markAsFavorite(book: Book): EmptyAppResult<DataError.Local> {
        return try {
            // ✅ Garantir que o mapper 'toEntity' pegue o idioma atual do objeto Book
            val entity = book.markAsFavorite(
              //  selectedLanguage = selectedLanguage
            )
            bookDao.upsert(entity)
            AppResult.Success(Unit)
        } catch (e: SQLiteException) {
            AppResult.Failure(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteFavoriteBook(id: String,language: String) {
        bookDao.deletedFavoriteBook(id,language)
    }

    //  override suspend fun deleteAllFavoriteBook(): Flow<List<Book>> {
    //     TODO("Not yet implemented")
    // }


    /*
    companion object {
        private const val CACHE_DURATION =
            3 * 24 * 60 * 60 * 1000L
    }

    private  fun isExpired(cachedAt: Long):Boolean{
        return  System.currentTimeMillis() - cachedAt > CACHE_DURATION
    }


override suspend fun searchBooks(query: String):AppResult<List<Book>, DataError.Remote>{
    val cachedBooks = searchBookDao.getBooksByQuery(query)

    if (cachedBooks.isNotEmpty() && !isExpired(cachedBooks.first().cachedAt)){
        return AppResult.Success(cachedBooks.map { it.toDomain() })

    }

    // cache vazio ou expirado -> chama API
    val apiBooks = api.searchBooks(query)
    val now = System.currentTimeMillis()

    searchBookDao.insertAll(
        apiBooks.map{
            SearchBookEntity(
                id = it.id,
                query = query,
                title = it.title,
                authors = it.authors,
                publishedYear = it.publishedYear,
                publisher = TODO(),
                coverUrl = TODO(),
                description = TODO(),
                languages = TODO(),
                averageRating = TODO(),
                ratingsCount = TODO(),
                numEditions = TODO(),
                format = TODO(),
                downloadUrl = TODO(),
                previewUrl = TODO(),
                retailPrice = TODO(),
                isFree = TODO(),
                source = TODO(),
                printType = TODO(),
                contentVersion = TODO(),
                translators = TODO(),
                copyright = TODO(),
                cachedAt = TODO(),
            )
        }
    )

    return AppResult.Success(apiBooks.map { it.toDomain() })

}



}

 */







}








/**
     * 🔍 Busca geral — tenta todas as fontes até encontrar resultados.

   override suspend fun searchBooks(query: String): AppResult<List<Book>, DataError.Remote> {
        // 1️⃣ Open Library
        val openLibraryResult = remoteBookDataSource.searchBookOpenLibrary(
            query = query,
            resultLimit = 50,
            page = 1
        ).map { dto -> dto.results.map { it.searchBookese() } }

        // Se OpenLibrary tiver resultados, ótimo.
        if (openLibraryResult is AppResult.Success && openLibraryResult.data.isNotEmpty()) {
            return openLibraryResult
        }



        // 3️⃣ Google Books
        val googleResult = remoteBookDataSource.searchGoogleBooks(
            query = query,
            resultLimit = 50,
            page = 1
        ).map { dto ->
            dto.results?.map { it.toDomainSearchGoogleBookDto() } ?: emptyList()
        }

        if (googleResult is AppResult.Success && googleResult.data.isNotEmpty()) {
            return googleResult
        }

        // 2️⃣ Gutendex
        val gutenbergResult = remoteBookDataSource.searchGutendex(
            query = query,
            resultLimit = 50,
            page = 1
        ).map { dto -> dto.results.map { it.toDomainSearchGutenberg() } }

        if (gutenbergResult is AppResult.Success && gutenbergResult.data.isNotEmpty()) {
            return gutenbergResult
        }


        // 🔚 Nenhum resultado em nenhuma API
        return AppResult.Success(emptyList())
    }

    // Mantém compatibilidade caso precise das buscas separadas
    override suspend fun searchBookOpenLibrary(query: String): AppResult<List<Book>, DataError.Remote> =
        searchBooks(query)



    override suspend fun searchGoogleBooks(query: String): AppResult<List<Book>, DataError.Remote> =
        searchBooks(query)

    override suspend fun searchGutendex(query: String): AppResult<List<Book>, DataError.Remote> =
        searchBooks(query)

 */


/*
FATAL EXCEPTION: main (Explain with AI)
                                                                                                    Process: com.example.bookreadanddownloadforfree, PID: 19654
                                                                                                    java.lang.IllegalArgumentException: Key "OL17930368W" was already used. If you are using LazyColumn/Row please make sure you provide a unique key for each item.
                                                                                                    	at androidx.compose.ui.internal.InlineClassHelperKt.throwIllegalArgumentException(InlineClassHelper.kt:36)
                                                                                                    	at androidx.compose.ui.layout.LayoutNodeSubcompositionsState.subcompose(SubcomposeLayout.kt:1055)
                                                                                                    	at androidx.compose.ui.layout.LayoutNodeSubcompositionsState$Scope.subcompose(SubcomposeLayout.kt:926)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScopeImpl.measure-0kLqBqw(LazyLayoutMeasureScope.kt:124)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasureProvider.getAndMeasure-jy6DScQ(LazyStaggeredGridMeasure.kt:1259)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasureKt.measure(LazyStaggeredGridMeasure.kt:570)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasureKt.measureStaggeredGrid-C6celF4(LazyStaggeredGridMeasure.kt:183)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasurePolicyKt$rememberStaggeredGridMeasurePolicy$1$1.invoke-0kLqBqw(LazyStaggeredGridMeasurePolicy.kt:109)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasurePolicyKt$rememberStaggeredGridMeasurePolicy$1$1.invoke(LazyStaggeredGridMeasurePolicy.kt:63)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutKt$LazyLayout$1$2$1.invoke-0kLqBqw(LazyLayout.kt:78)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutKt$LazyLayout$1$2$1.invoke(LazyLayout.kt:76)
                                                                                                    	at androidx.compose.ui.layout.LayoutNodeSubcompositionsState$createMeasurePolicy$1.measure-3p2s80s(SubcomposeLayout.kt:754)
                                                                                                    	at androidx.compose.ui.node.InnerNodeCoordinator.measure-BRTryo0(InnerNodeCoordinator.kt:128)
                                                                                                    	at androidx.compose.ui.graphics.SimpleGraphicsLayerModifier.measure-3p2s80s(GraphicsLayerModifier.kt:642)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutBeyondBoundsModifierNode.measure-3p2s80s(LazyLayoutBeyondBoundsModifierLocal.kt:118)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.foundation.layout.PaddingNode.measure-3p2s80s(Padding.kt:401)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.foundation.layout.FillNode.measure-3p2s80s(Size.kt:721)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate$performMeasureBlock$1.invoke(MeasurePassDelegate.kt:171)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate$performMeasureBlock$1.invoke(MeasurePassDelegate.kt:170)
                                                                                                    	at androidx.compose.runtime.snapshots.Snapshot$Companion.observe(Snapshot.kt:2495)
                                                                                                    	at androidx.compose.runtime.snapshots.SnapshotStateObserver$ObservedScopeMap.observe(SnapshotStateObserver.kt:464)
                                                                                                    	at androidx.compose.runtime.snapshots.SnapshotStateObserver.observeReads(SnapshotStateObserver.kt:248)
                                                                                                    	at androidx.compose.ui.node.OwnerSnapshotObserver.observeReads$ui_release(OwnerSnapshotObserver.kt:124)
                                                                                                    	at androidx.compose.ui.node.OwnerSnapshotObserver.observeMeasureSnapshotReads$ui_release(OwnerSnapshotObserver.kt:107)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate.performMeasure-BRTryo0$ui_release(MeasurePassDelegate.kt:424)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate.remeasure-BRTryo0(MeasurePassDelegate.kt:472)
                                                                                                    	at androidx.compose.ui.node.LayoutNode.remeasure-_Sx5XlM$ui_release(LayoutNode.kt:1212)
                                                                                                    	at androidx.compose.ui.node.MeasureAndLayoutDelegate.doRemeasure-sdFAvZA(MeasureAndLayoutDelegate.kt:367)
                                                                                                    	at androidx.compose.ui.node.MeasureAndLayoutDelegate.measureAndLayout-0kLqBqw(MeasureAndLayoutDelegate.kt:466)
2025-12-30 17:39:52.404  6178-6178  AndroidRuntime          pid-6178                             E  FATAL EXCEPTION: main (Explain with AI)
                                                                                                    Process: com.example.bookreadanddownloadforfree, PID: 6178
                                                                                                    java.lang.IllegalArgumentException: Key "OL17930368W" was already used. If you are using LazyColumn/Row please make sure you provide a unique key for each item.
                                                                                                    	at androidx.compose.ui.internal.InlineClassHelperKt.throwIllegalArgumentException(InlineClassHelper.kt:36)
                                                                                                    	at androidx.compose.ui.layout.LayoutNodeSubcompositionsState.subcompose(SubcomposeLayout.kt:1055)
                                                                                                    	at androidx.compose.ui.layout.LayoutNodeSubcompositionsState$Scope.subcompose(SubcomposeLayout.kt:926)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScopeImpl.measure-0kLqBqw(LazyLayoutMeasureScope.kt:124)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasureProvider.getAndMeasure-jy6DScQ(LazyStaggeredGridMeasure.kt:1259)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasureKt.measure(LazyStaggeredGridMeasure.kt:570)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasureKt.measureStaggeredGrid-C6celF4(LazyStaggeredGridMeasure.kt:183)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasurePolicyKt$rememberStaggeredGridMeasurePolicy$1$1.invoke-0kLqBqw(LazyStaggeredGridMeasurePolicy.kt:109)
                                                                                                    	at androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridMeasurePolicyKt$rememberStaggeredGridMeasurePolicy$1$1.invoke(LazyStaggeredGridMeasurePolicy.kt:63)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutKt$LazyLayout$1$2$1.invoke-0kLqBqw(LazyLayout.kt:78)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutKt$LazyLayout$1$2$1.invoke(LazyLayout.kt:76)
                                                                                                    	at androidx.compose.ui.layout.LayoutNodeSubcompositionsState$createMeasurePolicy$1.measure-3p2s80s(SubcomposeLayout.kt:754)
                                                                                                    	at androidx.compose.ui.node.InnerNodeCoordinator.measure-BRTryo0(InnerNodeCoordinator.kt:128)
                                                                                                    	at androidx.compose.ui.graphics.SimpleGraphicsLayerModifier.measure-3p2s80s(GraphicsLayerModifier.kt:642)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutBeyondBoundsModifierNode.measure-3p2s80s(LazyLayoutBeyondBoundsModifierLocal.kt:118)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.foundation.layout.PaddingNode.measure-3p2s80s(Padding.kt:401)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.foundation.layout.FillNode.measure-3p2s80s(Size.kt:721)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate$performMeasureBlock$1.invoke(MeasurePassDelegate.kt:171)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate$performMeasureBlock$1.invoke(MeasurePassDelegate.kt:170)
                                                                                                    	at androidx.compose.runtime.snapshots.Snapshot$Companion.observe(Snapshot.kt:2495)
                                                                                                    	at androidx.compose.runtime.snapshots.SnapshotStateObserver$ObservedScopeMap.observe(SnapshotStateObserver.kt:464)
                                                                                                    	at androidx.compose.runtime.snapshots.SnapshotStateObserver.observeReads(SnapshotStateObserver.kt:248)
                                                                                                    	at androidx.compose.ui.node.OwnerSnapshotObserver.observeReads$ui_release(OwnerSnapshotObserver.kt:124)
                                                                                                    	at androidx.compose.ui.node.OwnerSnapshotObserver.observeMeasureSnapshotReads$ui_release(OwnerSnapshotObserver.kt:107)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate.performMeasure-BRTryo0$ui_release(MeasurePassDelegate.kt:424)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate.remeasure-BRTryo0(MeasurePassDelegate.kt:472)
                                                                                                    	at androidx.compose.ui.node.LayoutNode.remeasure-_Sx5XlM$ui_release(LayoutNode.kt:1212)
                                                                                                    	at androidx.compose.ui.node.MeasureAndLayoutDelegate.doRemeasure-sdFAvZA(MeasureAndLayoutDelegate.kt:367)
                                                                                                    	at androidx.compose.ui.node.MeasureAndLayoutDelegate.measureAndLayout-0kLqBqw(MeasureAndLayoutDelegate.kt:466)
--------- beginning of system
2025-12-31 00:27:08.591  9655-9899  libc                    com...le.bookreadanddownloadforfree  A  Fatal signal 11 (SIGSEGV), code 2 (SEGV_ACCERR), fault addr 0x6b6f6f42 in tid 9899 (Studio:ins_io3 ), pid 9655 (downloadforfree)
2025-12-31 00:27:09.120 10018-10018 DEBUG                   crash_dump32                         A  pid: 9655, tid: 9899, name: Studio:ins_io3  >>> com.example.bookreadanddownloadforfree <<<
--------- beginning of main
2025-12-31 00:27:09.155 10018-10018 DEBUG                   crash_dump32                         A        #01 pc 000aeaf4  /data/app/com.example.bookreadanddownloadforfree-RduzZtTyd0FfAlC7zXMV8w==/base.apk!libsqliteJni.so (offset 0x4c0000)
2025-12-31 00:27:17.819  1043-1157  InputDispatcher         system_server                        E  channel 'c08a3ba com.example.bookreadanddownloadforfree/com.example.bookreadanddownloadforfree.MainActivity (server)' ~ Channel is unrecoverably broken and will be disposed!
2025-12-31 00:27:22.524  1043-1471  InputDispatcher         system_server                        E  Window handle Window{500a621 u0 Application Error: com.example.bookreadanddownloadforfree} has no registered input channel
2025-12-31 00:27:30.719 10191-10243 Finsky                  com.android.vending                  E  [36960] VerifyApps: Required split types check failed for com.example.bookreadanddownloadforfree. (Explain with AI)
                                                                                                    java.lang.IllegalStateException: Install path is null
                                                                                                    	at com.google.android.finsky.verifier.impl.installtime.VerifyRequiredSplitTypesInstallTask.mf(PG:715)
                                                                                                    	at com.google.android.finsky.verifier.impl.installtime.VerifyInstallTask.mf(PG:1583)
                                                                                                    	at ayni.run(PG:83)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
                                                                                                    	at bkae.run(PG:57)
                                                                                                    	at java.lang.Thread.run(Thread.java:919)
2025-12-31 00:27:30.721 10191-10247 Finsky                  com.android.vending                  E  [36963] VerifyApps: Required split types check failed for com.example.bookreadanddownloadforfree. (Explain with AI)
                                                                                                    java.lang.IllegalStateException: Install path is null
                                                                                                    	at com.google.android.finsky.verifier.impl.installtime.VerifyRequiredSplitTypesInstallTask.mf(PG:715)
                                                                                                    	at com.google.android.finsky.verifier.impl.installtime.VerifyInstallTask.mf(PG:1583)
                                                                                                    	at ayni.run(PG:83)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
                                                                                                    	at bkae.run(PG:57)
                                                                                                    	at java.lang.Thread.run(Thread.java:919)
2025-12-31 00:27:30.721 10191-10285 Finsky                  com.android.vending                  E  [36980] VerifyApps: Required split types check failed for com.example.bookreadanddownloadforfree. (Explain with AI)
                                                                                                    java.lang.IllegalStateException: Install path is null
                                                                                                    	at com.google.android.finsky.verifier.impl.installtime.VerifyRequiredSplitTypesInstallTask.mf(PG:715)
                                                                                                    	at com.google.android.finsky.verifier.impl.installtime.VerifyInstallTask.mf(PG:1583)
                                                                                                    	at ayni.run(PG:83)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
                                                                                                    	at bkae.run(PG:57)
                                                                                                    	at java.lang.Thread.run(Thread.java:919)
2025-12-31 00:27:30.754 10191-10285 Finsky                  com.android.vending                  E  [36980] VerifyApps V31SignatureVerification: Failed to collect certificates for the package: com.example.bookreadanddownloadforfree using APK Signature Scheme v3
2025-12-31 00:27:30.755 10191-10247 Finsky                  com.android.vending                  E  [36963] VerifyApps V31SignatureVerification: Failed to collect certificates for the package: com.example.bookreadanddownloadforfree using APK Signature Scheme v3
2025-12-31 00:27:30.768 10191-10243 Finsky                  com.android.vending                  E  [36960] VerifyApps V31SignatureVerification: Failed to collect certificates for the package: com.example.bookreadanddownloadforfree using APK Signature Scheme v3
---------------------------- PROCESS STARTED (10489) for package com.example.bookreadanddownloadforfree --------
 */