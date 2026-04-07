package com.example.bookreadanddownloadforfree.bookfree.book.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SearchedBookOpenLibraryDto(
    @SerialName("key") val id:String,
    @SerialName("title") val title:String,
    @SerialName("author_name") val authorName:List<String>?=null,
    @SerialName("author_key") val authorKey:List<String>?=null,
    @SerialName("language") val languages:List<String>?=null,
    @SerialName("cover_i") val corverAlternativeKey:Int?=null,
    @SerialName("cover_edition_key") val coverKey:String?=null,
    @SerialName("first_publish_year") val firstPublishYear:Int? = null,
    @SerialName("ratings_average") val ratingAverage: Double? = null,
    @SerialName("ratings_count") val ratingsCount:Int? = null,
    @SerialName("number_of_pages_median") val numPagesMedian: Int? = null,
    @SerialName("edition_count") val numEditions: Int? = null,
    @SerialName("ia") val ia: List<String>? = null,

)





///
@Serializable
data class GutendexBookDto(
    @SerialName("id") val id: Int,
    @SerialName("title")val title: String,
    //@SerialName("key")val subjects: List<String>? = null,
    @SerialName("authors")val authors: List<GutendexAuthorDto>? = null,
    @SerialName("translators")val translators: List<GutendexAuthorDto>? = null,
    @SerialName("languages")val languages: List<String>? = null,
    @SerialName("copyright")val copyright: Boolean? = null,
    @SerialName("media_type")val media_type: String? = null,
    @SerialName("formats")val formats: Map<String, String>? = null,
    @SerialName("download_count")val download_count: Int? = null
)

@Serializable
data class GutendexAuthorDto(
    @SerialName("birth_year")val birth_year: Int? = null,
    @SerialName("death_year")val death_year: Int? = null,
    @SerialName("name")val name: String
)




@Serializable
data class GoogleBookDto(
    @SerialName("id") val id: String,
    @SerialName("volumeInfo") val volumeInfo: VolumeInfoDto,
    @SerialName("saleInfo") val saleInfo: SaleInfoDto? = null,
    @SerialName("accessInfo") val accessInfo: AccessInfoDto? = null
)


@Serializable
data class VolumeInfoDto(
    @SerialName("title") val title: String? = null,
    @SerialName("authors") val authors: List<String>? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("imageLinks") val imageLinks: ImageLinksDto? = null,
    @SerialName("pageCount") val pageCount: Int? = null,
    @SerialName("averageRating") val averageRating: Double? = null,
    @SerialName("ratingsCount") val ratingsCount: Int? = null,
    @SerialName("contentVersion") val contentVersion: String? = null,
    @SerialName("publishedDate") val publishedDate: String? = null,
    @SerialName("industryIdentifiers") val industryIdentifiers: List<IndustryIdentifierDto>? = null,
    @SerialName("previewLink") val previewLink: String? = null, // 👈 ADICIONADO (Fallback 1)
    @SerialName("infoLink") val infoLink: String? = null,       // 👈 ADICIONADO
  //  @SerialName("categories")
)


@Serializable
data class IndustryIdentifierDto(
    @SerialName("type") val type: String? = null,
    @SerialName("identifier") val identifier: String? = null)

@Serializable
data class ImageLinksDto(
    @SerialName("thumbnail") val thumbnail: String? = null,
    @SerialName("smallThumbnail") val smallThumbnail: String? = null,
    @SerialName("medium") val medium: String? = null,
    @SerialName("large") val large: String? = null,
    @SerialName("extraLarge") val extraLarge: String? = null
)

@Serializable
data class SaleInfoDto(
    @SerialName("saleability") val saleability: String? = null
)

@Serializable
data class AccessInfoDto(
    @SerialName("viewability") val viewability: String? = null,
    @SerialName("pdf") val pdf: FormatAccessDto? = null,
    @SerialName("epub") val epub: FormatAccessDto? = null,
    @SerialName("webReaderLink") val webReaderLink: String? = null, // 👈 ADICIONADO
    @SerialName("accessViewStatus") val accessViewStatus: String? = null, // 👈 Útil para saber se é SAMPLE
    @SerialName("publicDomain") val publicDomain: Boolean? = null,
)

@Serializable
data class FormatAccessDto(
    @SerialName("isAvailable") val isAvailable: Boolean? = false,
    @SerialName("acsTokenLink") val acsTokenLink: String? = null
)





@Serializable
data class TrendingBookDto(

    @SerialName("language") val languages: List<String>? = null,
    @SerialName("key") val id:String,
    @SerialName("title") val title:String,
    @SerialName("author_name") val authorName:List<String>?=null,
    @SerialName("author_key") val authorKey:List<String>?=null,
    //@SerialName("language") val languages:List<String>?=null,
    @SerialName("cover_i") val corverAlternativeKey:Int?=null,
    @SerialName("cover_edition_key") val coverKey:String?=null,
    @SerialName("first_publish_year") val firstPublishYear:Int? = null,
    @SerialName("ratings_average") val ratingAverage: Double? = null,
    @SerialName("ratings_count") val ratingsCount:Int? = null,
    @SerialName("number_of_pages_median") val numPagesMedian: Int? = null,
    @SerialName("edition_count") val numEditions: Int? = null,
    @SerialName("ia") val ia: List<String>? = null,

)
