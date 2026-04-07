package com.example.bookreadanddownloadforfree.bookfree.book.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// ✅ BOOK ENTITY (Favoritos)
// Usamos ID + Languages como chave para permitir salvar o mesmo livro em idiomas diferentes nos favoritos
@Entity(primaryKeys = ["id", "languages"])
data class BookEntity(
    val id: String,
    val languages:  String, // ⚠️ Removido o '?' (Não pode ser nulo)
    val title: String,
    val cachedAt: Long = 0,
    val priority: Int = 0,
    val authors: List<String> = emptyList(),
    val publishedYear: String? = null,
    val publisher: String? = null,
    val coverUrl: String?,
    val description: String? = null,
    val averageRating: Double? = null,
    val ratingsCount: Int? = null,
    val numEditions: Int? = null,
    val format: String? = null,
    val source: String,
    val translators: List<String>? = null,
    val copyright: Boolean? = null,
    val acsTokenLink: String? = null,
)

// ✅ SEARCH BOOK ENTITY (Cache de Pesquisa)
@Entity(
    tableName = "search_books",
    primaryKeys = ["id", "languages"] // ✅ Chave Composta Correta
)
data class SearchBookEntity(
    val id: String,
    val languages: String, // ⚠️ Removido o '?' (Não pode ser nulo)
    val query: String,
    val position: Int,      // ✅ Adicionado para manter a ordem da API
    val title: String,
    val authors: List<String> = emptyList(),
    val cachedAt: Long = 0,
    val publishedYear: String? = null,
    val publisher: String? = null,
    val coverUrl: String?,
    val description: String? = null,
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
    val acsTokenLink: String? = null,
)

// ✅ BOOK POPULAR ENTITY (Livros Populares)
@Entity(primaryKeys = ["id", "languages"]) // ✅ Corrigido: Removido os dois @PrimaryKey
data class BookPopularEntity(
    val id: String,
    val languages: String, // ⚠️ Removido o '?' (Não pode ser nulo)
    val priority: Int,      // Mantém a ordem de popularidade
    val cachedAtPopular: Long = 0,
    val title: String,
    val authors: List<String> = emptyList(),
    val publishedYear: String? = null,
    val publisher: String? = null,
    val coverUrl: String?,
    val description: String? = null,
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
    val acsTokenLink: String? = null,
)

// ✅ USER INTEREST ENTITY (Interesses - Permanece com Chave Única)
@Entity
data class UserInterestEntity(
    @PrimaryKey val term: String,
    val count: Int = 1,
    val lastInteracted: Long = System.currentTimeMillis(),
    val type: String
)