package com.example.bookreadanddownloadforfree.bookfree.core.domian






// Renomeie para evitar conflito com kotlin.Result
sealed interface AppResult<out D, out E: Error> {
    data class Success<out D>(val data: D) : AppResult<D, Nothing>
    data class Failure<out E : Error>(val error: E) : AppResult<Nothing, E>
}

inline fun <T, E : Error, R> AppResult<T, E>.map(map: (T) -> R): AppResult<R, E> {
    return when (this) {
        is AppResult.Failure -> AppResult.Failure(error)
        is AppResult.Success -> AppResult.Success(map(data))
    }
}

fun <T, E : Error> AppResult<T, E>.asEmptyDataResult(): EmptyAppResult<E> {
    return map { }
}

inline fun <T, E : Error> AppResult<T, E>.onSuccess(action: (T) -> Unit): AppResult<T, E> {
    return when (this) {
        is AppResult.Failure -> this
        is AppResult.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, E : Error> AppResult<T, E>.onError(action: (E) -> Unit): AppResult<T, E> {
    return when (this) {
        is AppResult.Failure -> {
            action(error)
            this
        }
        is AppResult.Success -> this
    }
}

typealias EmptyAppResult<E> = AppResult<Unit, E>