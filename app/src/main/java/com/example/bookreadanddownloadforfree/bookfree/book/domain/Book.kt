package com.example.bookreadanddownloadforfree.bookfree.book.domain

data class Bookse(
    val id: String,                           // 🔑 Identificador único
    val title: String,                        // 📖 Título do livro
    val authors: List<String>,                // 👥 Lista de autores
    val publishedYear: String? =null, // 📅 Ano de publicação (opcional)
    val publisher : String? =null,
    val coverUrl: String?,                    // 🖼️ URL da capa (opcional)
    val description: String? = "",             // 📝 Descrição/sinopse
    val languages: List<String>?,
    val averageRating: Double?,
    val ratingsCount: Int?=null,
    val numPages: Int?,
    val numEditions: Int,
    val format:String?=null,
    val downloadUrl : String?=null,
    val source: String

)
data class BookOpenLibrary(
    val id: String,                           // 🔑 Identificador único
    val title: String,                        // 📖 Título do livro
    val authors: List<String>,                // 👥 Lista de autores
    val publishedYear: String? =null, // 📅 Ano de publicação (opcional)
    //val publisher : String? =null,
    val coverUrl: String?,                    // 🖼️ URL da capa (opcional)
    val description: String? = "",             // 📝 Descrição/sinopse
    //val languages: String?,
    val averageRating: Double?,
    val ratingsCount: Int?,
    val numPages: Int?,
    val numEditions: Int,
    //val format:String?=null,
    val downloadUrl : String?=null,
    val source: String
)







data class Book(
    val id: String,
    val title: String,
    val authors: List<String> = emptyList(), // ✅ não como string
    val publishedYear: String? = null,
    val publisher: String? = null,
    val coverUrl: String?,
    val description: String? = null,
    val languages: List<String>,
    val averageRating: Double? = null,
    val ratingsCount: Int? = null,
    val numEditions: Int? = null,
    val format: String? = null,
    val downloadUrl: String? = null,
    val previewUrl: String? = null,
    val retailPrice: List<String>? = null,
    val isFree: Boolean = false,
    val source: String,
    val printType: String? = null,
    val contentVersion: String? = null,
    val translators: List<String>? = null,
    val copyright: Boolean? = null,
    val industryIdentifiers: String? = null ,
    val isAvailable: Boolean?=false,
    val acsTokenLink:String?=null,
    val isSample: Boolean?=null,
    val colors: List<Int> ,

   // val formats: Map<String, String>? = null,

)





