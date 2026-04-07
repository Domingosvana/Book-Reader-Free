package com.example.bookreadanddownloadforfree.bookfree.book.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SearchResponseDto(
    @SerialName("docs") val results: List<SearchedBookOpenLibraryDto>
)


@Serializable
data class GutendexResponseDto(
    @SerialName("count")val count: Int?=null,
    @SerialName("next")val next: String?=null,
    @SerialName("previous")val previous: String?=null,
    @SerialName("results")val results: List<GutendexBookDto>
)



@Serializable
data class GoogleBooksResponseDto(
    @SerialName("items") val results: List<GoogleBookDto>? = null
)

//AIzaSyA_2m4aztbdI0HAxL32Kt6qYZBimFvTGGM





@Serializable
data class TrendingResponseDto(
    @SerialName("works")
    val works: List<TrendingBookDto> = emptyList()
)














