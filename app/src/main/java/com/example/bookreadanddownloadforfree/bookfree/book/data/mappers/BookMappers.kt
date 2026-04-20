


package com.example.bookreadanddownloadforfree.bookfree.book.data.mappers

import android.util.Log
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookEntity

import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookPopularEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.SearchBookEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.UserInterestEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.bookGradients
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.GoogleBookDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.GutendexBookDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.SearchedBookOpenLibraryDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.TrendingBookDto
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.domain.InterestType
import com.example.bookreadanddownloadforfree.bookfree.book.domain.UserInterest
import java.util.Locale
import kotlin.collections.firstOrNull

// =============================================================================
// 1. DTO -> DOMAIN (Mappers de entrada das APIs)
// =============================================================================

fun SearchedBookOpenLibraryDto.searchBookese(): Book {

    // Escolhe o gradiente baseado no ID do livro antes de salvar

    val cleanId = id.removePrefix("/works/").removePrefix("/books/").trim()
    val idParaLeitura = ia?.firstOrNull()
    val linkFinal = if (idParaLeitura != null) {
        "https://archive.org/details/$idParaLeitura/mode/2up" // Modo Livro Aberto
    } else {
        "https://openlibrary.org/works/$cleanId"
    }
    return Book(
        id = id.substringAfterLast("/"),
        title = title,
        coverUrl = if (coverKey != null) {
            "https://covers.openlibrary.org/b/olid/${coverKey}-M.jpg"
        } else {
            "https://covers.openlibrary.org/b/id/${corverAlternativeKey}-L.jpg"
        },

        authors = authorName ?: emptyList(),
        publishedYear = firstPublishYear.toString(),
        description = null,
        averageRating = ratingAverage,
        ratingsCount = ratingsCount,
        numEditions = numEditions ?: 0,
        acsTokenLink = linkFinal,
        source = "OpenLibrary",
        languages = languages?.map { OpenLibraryLanguageMapper.toDisplayName(it) }
            ?: emptyList(), // ✅ Mapeia os códigos de idioma da API
        isFree = true,
        colors = listOf(),
    )
}

fun TrendingBookDto.toDomainTrending(): Book {

    // Escolhe o gradiente baseado no ID do livro antes de salvar




    val cleanId = id.removePrefix("/works/").removePrefix("/books/").trim()
    val idParaLeitura = ia?.firstOrNull()
    val linkFinal = if (idParaLeitura != null) {
        "https://archive.org/details/$idParaLeitura/mode/2up" // Modo Livro Aberto
    } else {
        "https://openlibrary.org/works/$cleanId"
    }
    return Book(
        id = id.substringAfterLast("/"),
        title = title,
        coverUrl = if (coverKey != null) {
            "https://covers.openlibrary.org/b/olid/${coverKey}-M.jpg"
        } else {
            "https://covers.openlibrary.org/b/id/${corverAlternativeKey}-L.jpg"
        },
        acsTokenLink = linkFinal,
        languages = languages?.map { OpenLibraryLanguageMapper.toDisplayName(it) } ?: emptyList(),
        authors = authorName ?: emptyList(),
        publishedYear = firstPublishYear.toString(),
        description = null,
        averageRating = ratingAverage,
        ratingsCount = ratingsCount,
        numEditions = numEditions ?: 0,
        source = "OpenLibrary",
        colors = listOf()

    )
}

fun GoogleBookDto.toDomainSearchGoogleBookDto(): Book {




    val imageLinks = volumeInfo.imageLinks
    val coverUrl = imageLinks?.extraLarge?.replace("http://", "https://")?.substringBefore("&edge")
        ?: imageLinks?.large?.replace("http://", "https://")?.substringBefore("&edge")
        ?: imageLinks?.thumbnail?.replace("http://", "https://")?.substringBefore("&edge")
        ?: "https://via.placeholder.com/200x300.png?text=No+Image"

    // ✅ MELHORIA: Hierarquia robusta para o link de leitura/info
    // O webReaderLink é o melhor para ler no navegador.
    val linkParaLeitura =volumeInfo.previewLink?:volumeInfo.infoLink ?: "https://books.google.com"//accessInfo?.webReaderLink
       // ?: volumeInfo.previewLink
        //?: volumeInfo.infoLink
       // ?: "https://books.google.com" // Fallback final para evitar o NullPointerException
val isFullBook = accessInfo?.viewability == "FULL" || accessInfo?.publicDomain == true
    Log.d("GOOGLE_LANG", "Título: ${volumeInfo.title} | Idioma Original: $volumeInfo.language")





    return Book(


        id = id,
        title = volumeInfo.title ?: "Título desconhecido",
        authors = volumeInfo.authors ?: emptyList(),
        description = volumeInfo.description ?: "",
        coverUrl = coverUrl,
        // O acsTokenLink é para DOWNLOAD (Amostra ou Full)
        downloadUrl =if(isFullBook){
            accessInfo?.pdf?.acsTokenLink ?: accessInfo?.epub?.acsTokenLink
        }
        else null, //,

        // ✅ O acsTokenLink no seu domínio parece estar sendo usado para o link de leitura
        // Certifique-se de que o campo no seu objeto 'Book' aceita String (e não nulo)
        acsTokenLink = linkParaLeitura.replace("http://", "https://"),

        isSample = accessInfo?.accessViewStatus == "SAMPLE",
        publishedYear = volumeInfo.publishedDate?.substringBefore("-"),
        languages = listOf(GoogleLanguageMapper.toDisplayName(volumeInfo.language)),
        source = "GoogleBooks",
        colors = listOf()

    )
}
//publishedDate
// =============================================================================
// 2. DOMAIN -> ENTITY (Mappers para o Banco de Dados)
// =============================================================================



/**
 * Converte a lista de resultados da pesquisa para o Banco, dividindo por idioma.
 */
fun List<Book>.toSearchEntity(query: String): List<SearchBookEntity> {

    return this
    .flatMapIndexed   { index, book ->
        // Garante que exista pelo menos um código de idioma
        val langNames = if (book.languages.isEmpty()) listOf("Portuguese") else book.languages

        /*// 2. Calculamos as cores para ESTE livro específico dentro do loop
        // Se o objeto 'book' já tiver cores, usamos elas. Se não, geramos pelo ID.
        val selectedColors = if (book.colors.isNotEmpty()) {
            book.colors
        } else {
            val gradientIndex = Math.abs(book.id.hashCode()) % bookGradients.size
            bookGradients[gradientIndex]
        }

         */



        langNames.map { langName ->
          //  val prettyLang = getLanguageDisplayName(llangNames)
            SearchBookEntity(
                id = book.id,
                languages = langName, // CHAVE PRIMÁRIA PARTE 1
                position = index,    // ✅ MANTÉM A ORDEM DA API
                query = query.lowercase().trim(),
                title = book.title , // ✅ Título formatado
                authors = book.authors,
                publishedYear = book.publishedYear,
                publisher = book.publisher,
                coverUrl = book.coverUrl,
                description = book.description,
                averageRating = book.averageRating,
                ratingsCount = book.ratingsCount,
                numEditions = book.numEditions,
                format = book.format,
                downloadUrl = book.downloadUrl,
                previewUrl = book.previewUrl,
                retailPrice = book.retailPrice,
                isFree = book.isFree,
                source = book.source,
                printType = book.printType,
                contentVersion = book.contentVersion,
                translators = book.translators,
                copyright = book.copyright,
                acsTokenLink = book.acsTokenLink,
                cachedAt = System.currentTimeMillis(),

            )
        }
    }
}

/**
 * BookPopularEntity.toBookPopularEntity
 * Converte a lista de livros populares para o Banco, dividindo por idioma.
 */
fun List<Book>.toBookPopularEntity(): List<BookPopularEntity> {

    return this
        .flatMapIndexed { index, book ->
        val langNames = if (book.languages.isEmpty()) listOf("Portuguese") else book.languages

            /*
// 2. Calculamos as cores para ESTE livro específico dentro do loop
        // Se o objeto 'book' já tiver cores, usamos elas. Se não, geramos pelo ID.
        val selectedColors = if (book.colors.isNotEmpty()) {
            book.colors
        } else {
            val gradientIndex = Math.abs(book.id.hashCode()) % bookGradients.size
            bookGradients[gradientIndex]
        }
            */

        langNames.map { langName ->

            //val prettyLang = getLanguageDisplayName(langName)
            BookPopularEntity(
                id = book.id,
                languages = langName, // CHAVE PRIMÁRIA PARTE 1
                priority = index,    // ✅ MANTÉM A ORDEM DE POPULARIDADE
                cachedAtPopular = System.currentTimeMillis(),
                title = book.title,
                authors = book.authors,
                publishedYear = book.publishedYear,
                publisher = book.publisher,
                coverUrl = book.coverUrl,
                description = book.description,
                averageRating = book.averageRating,
                ratingsCount = book.ratingsCount,
                numEditions = book.numEditions,
                format = book.format,
                downloadUrl = book.downloadUrl,
                previewUrl = book.previewUrl,
                retailPrice = book.retailPrice,
                isFree = book.isFree,
                source = book.source,
                printType = book.printType,
                contentVersion = book.contentVersion,
                translators = book.translators,
                copyright = book.copyright,
                 acsTokenLink= book.acsTokenLink,
                           )
        }
    }
}

// =============================================================================
// 3. ENTITY -> DOMAIN (Mappers de leitura do Banco)
// =============================================================================

fun SearchBookEntity.toDomain(): Book {
    return Book(
        id = id,
        title = title,
        authors = authors,
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
        languages = listOf(languages), // ✅ Reconstrói a lista com o idioma único
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        downloadUrl = downloadUrl,
        previewUrl = previewUrl,
        retailPrice = retailPrice,
        isFree = isFree,
        source = source,
        printType = printType,
        contentVersion = contentVersion,
        translators = translators,
        copyright = copyright,
        acsTokenLink= acsTokenLink,
        colors = listOf()
    )
        }




//fun Book.cachedAtBookPopularEntity():  BookPopularEntity{

fun Book.cachedAtBookPopularEntity():  BookPopularEntity{

    // Escolhe o gradiente baseado no ID do livro antes de salvar

    return BookPopularEntity(
        id = id,
        title = title,
        authors = authors,
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
        languages = languages.firstOrNull()?:"\"pt\"",
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        downloadUrl = downloadUrl,
        previewUrl = previewUrl,
        retailPrice = retailPrice,
        isFree = isFree,
        source = source,
        printType = printType,
        contentVersion = contentVersion,
        translators = translators,
        copyright = copyright,
        acsTokenLink= acsTokenLink,
        priority = 0, // 👈 NOVO: Salva a posição (0, 1, 2, 3...),
        cachedAtPopular = System.currentTimeMillis(),

    )
}





fun BookPopularEntity.toDomain(): Book {
    return Book(
        id = id,
        title = title,
        authors = authors,
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
        languages = listOf(languages),
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        downloadUrl = downloadUrl,
        previewUrl = previewUrl,
        retailPrice = retailPrice,
        isFree = isFree,
        source = source,
        printType = printType,
        contentVersion = contentVersion,
        translators = translators,
        copyright = copyright,
        acsTokenLink= acsTokenLink,
        colors = listOf()
    )
}

// =============================================================================
// 4. FAVORITOS E INTERESSES
//Book.markAsFavorite(): BookEntity
// =============================================================================
//Argument type mismatch: actual type is 'List<String>', but 'String' was expected.
fun Book.markAsFavorite( ): BookEntity {
    // Se o livro já tem cores (da pesquisa), usa elas. Se não, gera.



    return  BookEntity(
        id = this.id,
        title = title,
        authors = authors,
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
       // id = this.id,
        languages = this.languages.firstOrNull() ?: "pt", // Salva o idioma atua, // Aqui você pode decidir manter a lista ou o idioma atual
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        source = source,
        acsTokenLink= acsTokenLink,
        cachedAt = System.currentTimeMillis(),
        //downloadUrl = downloadUrl,
        //previewUrl = previewUrl,
        //retailPrice = retailPrice,
        //isFree = isFree,
        // printType = printType,
        //contentVersion = contentVersion,
        //translators = translators,
        //copyright = copyright,
        // val formats: Map<String, String>? = null,
        priority = 0, // 👈 NOVO: Salva a posição (0, 1, 2, 3...)
        favoritedAt = System.currentTimeMillis(),









    )
}








// Mapper: Do Banco de Dados para o Livro (Domínio)
fun BookPopularEntity.toBookPopularEntity():Book{
    return Book(

        id = id.toString(),

        title = title,
        authors = authors,
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
        languages = listOf(languages),
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        downloadUrl = downloadUrl,
        previewUrl = previewUrl,
        retailPrice = retailPrice,
        isFree = isFree,
        source = source,
        printType = printType,
        contentVersion = contentVersion,
        translators = translators,
        copyright = copyright,
        acsTokenLink=acsTokenLink,
        colors =listOf()
    )
}







fun BookEntity.getFavoriteBookAll(selectedLanguage: String): Book {

    return Book(
        id = id,
        colors =listOf() ,
        title = title,
        authors = authors, // ✅ não como string
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
        languages = listOf(selectedLanguage) ,
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        //downloadUrl = downloadUrl,
        //previewUrl = previewUrl,
        //retailPrice = retailPrice,
        //isFree = isFree,
        source = source,
        acsTokenLink= acsTokenLink,
        // printType = printType,
        //contentVersion = contentVersion,
        //translators = translators,
        //copyright = copyright,
        // val formats: Map<String, String>? = null,




    )
}

// Transforma BookEntity (Banco) em Book (UI)
fun BookEntity.toDomain(): Book {

    return Book(
        id = this.id,
        //colors = this.colors,
        languages = listOf(this.languages), // Transforma a String única do banco em lista para a UI
        title = this.title,
        authors = this.authors,
        coverUrl = this.coverUrl,
        source = this.source,
        publishedYear = this.publishedYear,
        publisher = this.publisher,
        description = this.description,
        averageRating = this.averageRating,
        ratingsCount = this.ratingsCount,
        numEditions = this.numEditions,
        format = this.format,

        translators = this.translators,
        copyright = this.copyright,
        acsTokenLink= acsTokenLink,
        colors = listOf()
       // priority = 0, // 👈 NOVO: Salva a posição (0, 1, 2, 3...)




    )
}









fun GutendexBookDto.toDomainSearchGutenberg(): Book {
// Adicione o cálculo do gradiente aqui

    return Book(
        id = id.toString(),
        title = title,
        authors = emptyList(),
        translators = emptyList(),
        languages = listOfNotNull(),
        copyright = copyright,
        // media_type = media_type,
        format = formats.toString(),
        downloadUrl = download_count.toString(),
        source = "Gutendex",
        publishedYear = null,
        publisher = null,
        coverUrl = "",
        description = null,
        averageRating = null,
        ratingsCount = null,
        numEditions = null,
        previewUrl = null,
        retailPrice = null,
        isFree = true,
        printType = null,
        contentVersion = null,
        colors =listOf()// ✅ Agora tem cor!
    )
}






fun UserInterestEntity.toDomain(): UserInterest {
    return UserInterest(
        term = term,
        count = count,
        type = InterestType.valueOf(type)
    )
}

fun UserInterest.toEntity(): UserInterestEntity {
    return UserInterestEntity(
        term = term,
        count = count,
        type = type.name,
        lastInteracted = System.currentTimeMillis()
    )
}

// =============================================================================
// 5. FUNÇÕES AUXILIARES (Tratamento de Idiomas)
// =============================================================================

fun getLanguageDisplayName(langCode: String?): String {
    if (langCode.isNullOrBlank()) return "PT"

    val cleanCode = langCode
        .substringAfterLast("/")
        .replace("[", "").replace("]", "").replace("\"", "")
        .trim()

    val finalCode = cleanCode.split(",").first().trim()

    return try {
        val locale = Locale(finalCode)
        val displayName = locale.getDisplayLanguage(Locale("pt"))
        if (displayName.isNotEmpty() && displayName != finalCode) {
            displayName.replaceFirstChar { it.uppercase() }
        } else {
            finalCode.uppercase()
        }
    } catch (e: Exception) {
        finalCode.uppercase()
    }
}








object GoogleLanguageMapper {

    private val map = mapOf(
        "en" to "English", "es"    to "Spanish", "fr" to "French", "de" to "German",
        "pt-BR" to "Portuguese", "it" to "Italian", "ru" to "Russian", "zh" to "Chinese (Mandarin)",
        "ja" to "Japanese", "ko"   to "Korean", "ar" to "Arabic", "hi" to "Hindi",
        "tr" to "Turkish", "nl"    to "Dutch", "sv" to "Swedish", "pl" to "Polish",
        "vi" to "Vietnamese", "th" to "Thai", "id" to "Indonesian", "ms" to "Malay",
        "fa" to "Persian (Farsi)", "uk" to "Ukrainian", "el" to "Greek", "he" to "Hebrew",
        "cs" to "Czech", "ro" to "Romanian", "hu" to "Hungarian", "da" to "Danish",
        "fi" to "Finnish", "no" to "Norwegian", "bg" to "Bulgarian", "hr" to "Croatian",
        "sr" to "Serbian", "sk" to "Slovak", "sl" to "Slovenian", "lt" to "Lithuanian",
        "lv" to "Latvian", "et" to "Estonian", "is" to "Icelandic", "af" to "Afrikaans",
        "sw" to "Swahili", "zu" to "Zulu", "am" to "Amharic", "bn" to "Bengali",
        "pa" to "Punjabi", "ta" to "Tamil", "te" to "Telugu", "ur" to "Urdu",
        "my" to "Burmese", "km" to "Khmer", "lo" to "Lao", "mn" to "Mongolian",
        "ka" to "Georgian", "hy" to "Armenian", "az" to "Azerbaijani", "kk" to "Kazakh",
        "uz" to "Uzbek", "ky" to "Kyrgyz", "tg" to "Tajik", "tk" to "Turkmen",
        "ps" to "Pashto", "ku" to "Kurdish", "sd" to "Sindhi", "ne" to "Nepali",
        "sa" to "Sanskrit", "bo" to "Tibetan", "ug" to "Uyghur", "ha" to "Hausa",
        "yo" to "Yoruba", "ig" to "Igbo", "so" to "Somali", "om" to "Oromo",
        "mg" to "Malagasy", "mi" to "Maori", "gn" to "Guaraní", "qu" to "Quechua",
        "ay" to "Aymara", "eu" to "Basque", "ca" to "Catalan", "gl" to "Galician",
        "cy" to "Welsh", "ga" to "Irish", "gd" to "Scottish Gaelic", "br" to "Breton",
        "sq" to "Albanian", "mk" to "Macedonian", "bs" to "Bosnian", "rw" to "Kinyarwanda"
    )
/*
    fun toDisplayName(code: String?): String {
        if (code.isNullOrBlank()) return "idioma Unknown $code"

        // Normaliza: "pt-BR" vira "pt"
        val cleanCode = code.take(4)

        return map[cleanCode] ?: " sem Unknown $code" // Mostra o código se falhar para saberes qual falta adicionar
    }

 */



    fun toDisplayName(code: String?): String {
        return map[code] ?: "Unknown"
    }



}


object OpenLibraryLanguageMapper {
    private val map = mapOf(
        "eng" to "English", "spa" to "Spanish", "fre" to "French", "ger" to "German",
        "por" to "Portuguese", "ita" to "Italian", "rus" to "Russian", "chi" to "Chinese (Mandarin)",
        "jpn" to "Japanese", "kor" to "Korean", "ara" to "Arabic", "hin" to "Hindi",
        "tur" to "Turkish", "dut" to "Dutch", "swe" to "Swedish", "pol" to "Polish",
        "vie" to "Vietnamese", "tha" to "Thai", "ind" to "Indonesian", "may" to "Malay",
        "per" to "Persian (Farsi)", "ukr" to "Ukrainian", "gre" to "Greek", "heb" to "Hebrew",
        "cze" to "Czech", "rum" to "Romanian", "hun" to "Hungarian", "dan" to "Danish",
        "fin" to "Finnish", "nor" to "Norwegian", "bul" to "Bulgarian", "hrv" to "Croatian",
        "srp" to "Serbian", "slo" to "Slovenian", "lit" to "Lithuanian", "lav" to "Latvian",
        "est" to "Estonian", "ice" to "Icelandic", "afr" to "Afrikaans", "swa" to "Swahili",
        "zul" to "Zulu", "amh" to "Amharic", "ben" to "Bengali", "pan" to "Punjabi",
        "tam" to "Tamil", "tel" to "Telugu", "urd" to "Urdu", "bur" to "Burmese",
        "khm" to "Khmer", "lao" to "Lao", "mon" to "Mongolian", "geo" to "Georgian",
        "arm" to "Armenian", "aze" to "Azerbaijani", "kaz" to "Kazakh", "uzb" to "Uzbek",
        "kir" to "Kyrgyz", "tgk" to "Tajik", "tuk" to "Turkmen", "pus" to "Pashto",
        "kur" to "Kurdish", "snd" to "Sindhi", "nep" to "Nepali", "san" to "Sanskrit",
        "tib" to "Tibetan", "uig" to "Uyghur", "hau" to "Hausa", "yor" to "Yoruba",
        "ibo" to "Igbo", "som" to "Somali", "orm" to "Oromo", "mlg" to "Malagasy",
        "mao" to "Maori", "grn" to "Guaraní", "que" to "Quechua", "aym" to "Aymara",
        "baq" to "Basque", "cat" to "Catalan", "glg" to "Galician", "wel" to "Welsh",
        "iri" to "Irish", "gla" to "Scottish Gaelic", "bre" to "Breton", "alb" to "Albanian",
        "mac" to "Macedonian", "bos" to "Bosnian", "kin" to "Kinyarwanda", "yid" to "Yiddish",
        "und" to "Unknown"
    )

/*
    fun toDisplayName(code: String?): String {
        if (code.isNullOrBlank()) return "Unknown"

        // Normaliza: "pt-BR" vira "pt"
        val cleanCode = code.lowercase().trim().take(2)

        return map[cleanCode] ?: "Unknown $code" // Mostra o código se falhar para saberes qual falta adicionar
    }

 */
    fun toDisplayName(code: String?): String {
        return map[code] ?: "Unknown"
    }
}





/*

package com.example.bookreadanddownloadforfree.bookfree.book.data.mappers


import android.util.Log
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.BookPopularEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.SearchBookEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.database.UserInterestEntity
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.GoogleBookDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.GutendexBookDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.SearchedBookOpenLibraryDto
import com.example.bookreadanddownloadforfree.bookfree.book.data.dto.TrendingBookDto
import com.example.bookreadanddownloadforfree.bookfree.book.domain.Book
import com.example.bookreadanddownloadforfree.bookfree.book.domain.InterestType
import com.example.bookreadanddownloadforfree.bookfree.book.domain.UserInterest
import kotlin.collections.emptyList





fun SearchedBookOpenLibraryDto.searchBookese(): Book {
    return Book(
        id = id.substringAfterLast("/"),
        title = title,
        coverUrl = if (coverKey != null) {
            "https://covers.openlibrary.org/b/olid/${coverKey}-M.jpg"
        } else {
            "https://covers.openlibrary.org/b/id/${corverAlternativeKey}-L.jpg"

        },

        authors = authorName ?: emptyList(),
        publishedYear = firstPublishYear.toString(),
        description = null,
        averageRating = ratingAverage,
        ratingsCount = ratingsCount,
        //numPages = numPagesMedian,
        numEditions = numEditions ?: 0,
        // downloadUrl = ,
        source = "OpenLibrary",
        //publisher ={},
        languages = listOfNotNull(),
        format = null,
        downloadUrl = null,
        previewUrl = null,
        retailPrice = null,
        isFree = true,
        printType = null,
        contentVersion = null,
        translators = null,
        copyright = null,
    )

}


fun TrendingBookDto.toDomainTrending(): Book {



    return Book(
        id = id.substringAfterLast("/"),
        title = title,
        coverUrl = if (coverKey != null) {
            "https://covers.openlibrary.org/b/olid/${coverKey}-M.jpg"
        } else {
            "https://covers.openlibrary.org/b/id/${corverAlternativeKey}-L.jpg"

        },




        languages = languages ?: emptyList(),
        authors = authorName ?: emptyList(),
        publishedYear = firstPublishYear.toString(),
        description = null,
        averageRating = ratingAverage,
        ratingsCount = ratingsCount,
        //numPages = numPagesMedian,
        numEditions = numEditions ?: 0,
        // downloadUrl = ,
        source = "OpenLibrary",

    )
}





fun GutendexBookDto.toDomainSearchGutenberg(): Book {

    return Book(
        id = id.toString(),
        title = title,
        authors = emptyList(),
        translators = emptyList(),
        languages = listOfNotNull(),
        copyright = copyright,
        // media_type = media_type,
        format = formats.toString(),
        downloadUrl = download_count.toString(),
        source = "Gutendex",
        publishedYear = null,
        publisher = null,
        coverUrl = "",
        description = null,
        averageRating = null,
        ratingsCount = null,
        numEditions = null,
        previewUrl = null,
        retailPrice = null,
        isFree = true,
        printType = null,
        contentVersion = null
    )
}






fun GoogleBookDto.toDomainSearchGoogleBookDto(): Book {

   // val imageLinks = volumeInfo.imageLinks

    /*val coverUrl = when {
        imageLinks?.extraLarge != null -> imageLinks.extraLarge
        imageLinks?.large != null -> imageLinks.large
        imageLinks?.medium != null -> imageLinks.medium
        imageLinks?.thumbnail != null -> imageLinks.thumbnail
        else -> "https://via.placeholder.com/200x300.png?text=No+Image"
    }

     */
    val imageLinks = volumeInfo.imageLinks
    val coverUrl = imageLinks?.extraLarge?.replace("http://", "https://")?.substringBefore("&edge")

        ?: imageLinks?.large?.replace("http://", "https://")?.substringBefore("&edge")

        ?: imageLinks?.medium?.replace("http://", "https://")?.substringBefore("&edge")

        ?: imageLinks?.thumbnail?.replace("http://", "https://")?.substringBefore("&edge")

        ?: imageLinks?.smallThumbnail
        ?: "https://via.placeholder.com/200x300.png?text=No+Image"

    Log.i("Mapper", "Google coverUrl = $coverUrl for book id=$id title=${volumeInfo.title}")
    val cleanUrl = imageLinks?.thumbnail?.replace("http://", "https://")?.substringBefore("&edge")

    return Book(
        id = id,
        title = volumeInfo.title ?: "Título desconhecido",
        authors = volumeInfo.authors ?: emptyList(),
        description = volumeInfo.description ?: "",
        coverUrl =coverUrl ,
        downloadUrl = accessInfo?.pdf?.acsTokenLink
            ?: accessInfo?.epub?.acsTokenLink,
        format = when {
            accessInfo?.pdf?.isAvailable == true -> "pdf"
            accessInfo?.epub?.isAvailable == true -> "epub"
            else -> null
        },
        retailPrice = null ,
        printType = null,
        averageRating = null,
        ratingsCount = null,
        contentVersion = null,
        languages = listOfNotNull(volumeInfo.language),
        source = "GoogleBooks"
    )
}






// AGORA MAPEIAR AS INFORMACOES DO BANCO DE DADOS


fun Book.markAsFavorite(): BookEntity {
    return BookEntity(
     id = id,
     title = title,
     authors = authors, // ✅ não como string
     publishedYear = publishedYear,
     publisher = publisher,
     coverUrl = coverUrl,
     description = description,
     languages = languages ,
     averageRating = averageRating,
     ratingsCount = ratingsCount,
     numEditions = numEditions,
     format = format,
     //downloadUrl = downloadUrl,
     //previewUrl = previewUrl,
     //retailPrice = retailPrice,
     //isFree = isFree,
     source = source,
    // printType = printType,
     //contentVersion = contentVersion,
     //translators = translators,
     //copyright = copyright,
    // val formats: Map<String, String>? = null,
        cachedAt = System.currentTimeMillis(), // ⏱ Salva o momento da inserção
        priority = 0, // 👈 NOVO: Salva a posição (0, 1, 2, 3...)


    )
}





fun BookEntity.getFavoriteBookAll(): Book {
    return Book(
        id = id,

        title = title,
        authors = authors, // ✅ não como string
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
        languages = languages ,
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        //downloadUrl = downloadUrl,
        //previewUrl = previewUrl,
        //retailPrice = retailPrice,
        //isFree = isFree,
        source = source,
        // printType = printType,
        //contentVersion = contentVersion,
        //translators = translators,
        //copyright = copyright,
        // val formats: Map<String, String>? = null,



    )
}




// Converte de Book para Entity (para salvar no cache)
fun Book.toSearchEntity(query: String): SearchBookEntity {
    return SearchBookEntity(
        localId = 0, // 👈 IMPORTANTE: O Room vê o 0 e gera um novo ID automático.
        id = this.id,
        query = query.lowercase().trim(),
        title = this.title,
        authors = this.authors,
        publishedYear = this.publishedYear,
        publisher = this.publisher,
        coverUrl = this.coverUrl,
        description = this.description,
        languages = this.languages,
        averageRating = this.averageRating,
        ratingsCount = this.ratingsCount,
        numEditions = this.numEditions,
        format = this.format,
        downloadUrl = this.downloadUrl,
        previewUrl = this.previewUrl,
        retailPrice = this.retailPrice,
        isFree = this.isFree,
        source = this.source,
        printType = this.printType,
        contentVersion = this.contentVersion,
        translators = this.translators,
        copyright = this.copyright,
        cachedAt = System.currentTimeMillis() // ⏱ Salva o momento da inserção
    )
}

// Converte de Entity para Book (para ler do cache)
fun SearchBookEntity.toDomain(): Book {
    return Book(
        id = id,
        title = title,
        authors = authors,
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
        languages = languages,
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        downloadUrl = downloadUrl,
        previewUrl = previewUrl,
        retailPrice = retailPrice,
        isFree = isFree,
        source = source,
        printType = printType,
        contentVersion = contentVersion,
        translators = translators,
        copyright = copyright
    )
}


// Mapper: De Livro (Domínio) para Banco de Dados
fun Book.cachedAtBookPopularEntity():  BookPopularEntity{
    return BookPopularEntity(
        localId = 0, // 👈 IMPORTANTE: O Room vê o 0 e gera um novo ID automático.
        id = this.id,
        cachedAtPopular = System.currentTimeMillis() ,// ⏱ Salva o momento da inserção
        title = this.title,
        authors = this.authors,
        publishedYear = this.publishedYear,
        publisher = this.publisher,
        coverUrl = this.coverUrl,
        description = this.description,
        languages = this.languages,
        averageRating = this.averageRating,
        ratingsCount = this.ratingsCount,
        numEditions = this.numEditions,
        format = this.format,
        downloadUrl = this.downloadUrl,
        previewUrl = this.previewUrl,
        retailPrice = this.retailPrice,
        isFree = this.isFree,
        source = this.source,
        printType = this.printType,
        contentVersion = this.contentVersion,
        translators = this.translators,
        copyright = this.copyright,
        priority = 0, // 👈 NOVO: Salva a posição (0, 1, 2, 3...)

    )
}







// Mapper: Do Banco de Dados para o Livro (Domínio)
fun BookPopularEntity.toBookPopularEntity():Book{
    return Book(

        id = id.toString(),

        title = title,
        authors = authors,
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
        languages = languages,
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        downloadUrl = downloadUrl,
        previewUrl = previewUrl,
        retailPrice = retailPrice,
        isFree = isFree,
        source = source,
        printType = printType,
        contentVersion = contentVersion,
        translators = translators,
        copyright = copyright
    )
}






fun UserInterestEntity.toDomain(): UserInterest {
    return UserInterest(
        term = term,
        count = count,
        type = InterestType.valueOf(type)
    )
}

// Para quando quisermos criar uma Entity a partir de um clique
fun UserInterest.toEntity(): UserInterestEntity {
    return UserInterestEntity(
        term = term,
        count = count,
        type = type.name,
        lastInteracted = System.currentTimeMillis()
    )
}




// SEPARAR ALISTA DE IDIOMA QUE ACADA LIVRO TEM SEU IDIOMA ESTOU FAZER ISTO PORQUE API DO OPENLIBRARY
// VEM COMO LISTA PARA IDIOMA




fun getLanguageDisplayName(langCode: String?): String {
    if (langCode.isNullOrBlank()) return "Desconhecido"

    // 1. Limpeza de "lixo" (Caminhos, Colchetes, Aspas e Espaços)
    val cleanCode = langCode
        .substringAfterLast("/")  // "/languages/por" -> "por"
        .replace("[", "")          // "[por" -> "por"
        .replace("]", "")          // "por]" -> "por"
        .replace("\"", "")         // "\"por\"" -> "por"
        .trim()                    // " por " -> "por"

    // 2. Se ainda vier uma lista (ex: "por,eng"), pegamos apenas o primeiro
    // Já que o teu banco agora terá uma linha para cada um.
    val finalCode = cleanCode.split(",").first().trim()

    // 3. Validação do código (ISO costuma ter 2 ou 3 letras)
    if (finalCode.length !in 2..3) {
        return finalCode.uppercase() // Se for algo estranho, mostra o que veio em maiúsculo
    }

    return try {
        // Criamos o Locale com o código (ex: "por")
        val locale = java.util.Locale(finalCode)

        // .getDisplayLanguage(Locale("pt")) garante que o nome venha em Português
        // independentemente do idioma do telemóvel do utilizador.
        val displayName = locale.getDisplayLanguage(java.util.Locale("pt"))

        if (displayName.isNotEmpty() && displayName != finalCode) {
            displayName.replaceFirstChar { it.uppercase() }
        } else {
            finalCode.uppercase() // Fallback: mostra "POR" se não conseguir traduzir
        }
    } catch (e: Exception) {
        finalCode.uppercase()
    }
}


fun Book.markAsFavorite(selectedLanguage: String): BookEntity {
    return BookEntity(
        id = id,
        languages = selectedLanguage, // ✅ Usa o idioma que o usuário escolheu
        title = title,
        authors = authors,
        publishedYear = publishedYear,
        publisher = publisher,
        coverUrl = coverUrl,
        description = description,
        averageRating = averageRating,
        ratingsCount = ratingsCount,
        numEditions = numEditions,
        format = format,
        source = source,
        cachedAt = System.currentTimeMillis(),
        priority = 0
    )
}



 */






