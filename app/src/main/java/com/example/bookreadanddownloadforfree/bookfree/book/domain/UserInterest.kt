package com.example.bookreadanddownloadforfree.bookfree.book.domain

data class UserInterest(
    val term: String,// O nome do autor ou a categoria (ex: "James Clear" ou "Romance")
    val count: Int,// Quantidade de vezes que interagiu
    val type: InterestType
)

enum class InterestType {
    AUTHOR, CATEGORY, SEARCH_TERM
}


