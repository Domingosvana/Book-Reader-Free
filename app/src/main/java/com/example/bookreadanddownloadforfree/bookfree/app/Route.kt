package com.example.bookreadanddownloadforfree.bookfree.app

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object  BookGraph: Route

    @Serializable
    data object BookList: Route

    @Serializable
    data object BookSearch:Route


    @Serializable
    data class BookDetail(val id: String):Route


    @Serializable
    data object BookLibrary: Route

    @Serializable
    data object Profile: Route


    @Serializable
    data object Filter: Route

    @Serializable
    data object Main: Route

    @Serializable
    data object Count: Route






}




