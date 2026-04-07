package com.example.bookreadanddownloadforfree.bookfree.core.data

import com.example.bookreadanddownloadforfree.bookfree.core.domian.AppResult
import com.example.bookreadanddownloadforfree.bookfree.core.domian.DataError
import io.ktor.client.call.body
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

import java.net.ConnectException
import java.net.UnknownHostException
import java.io.IOException


suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): AppResult<T, DataError.Remote>{

    val response = try{
        execute()
    }
    catch(_: SocketTimeoutException){
        return AppResult.Failure(DataError.Remote.REQUEST_TIMEOUT)
    }
    catch (_: UnresolvedAddressException){
        return AppResult.Failure(DataError.Remote.NO_INTERNET)
    }
    catch(_: UnknownHostException){// DNS não resolveu
        return AppResult.Failure(DataError.Remote.NO_INTERNET)
    }
    catch (_: ConnectException){// Não conseguiu abrir socket
        return AppResult.Failure(DataError.Remote.NO_INTERNET)
    }
    catch (_: IOException){// Qualquer falha de I/O de rede
        return AppResult.Failure(DataError.Remote.NO_INTERNET)
     }
    catch (_: Exception){
        coroutineContext.ensureActive()
        return AppResult.Failure(DataError.Remote.UNKNOWN)
    }

    return responseToResult(response)

}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): AppResult<T, DataError.Remote>{
   return when(response.status.value){
       in 200..299 ->{
           try {
               AppResult.Success(response.body<T>()) // ✅ CORRIGIDO
           } catch(e: NoTransformationFoundException) {
               AppResult.Failure(DataError.Remote.SERIALIZATION) // ✅ CORRIGIDO
           }
       }

       408 -> AppResult.Failure(DataError.Remote.REQUEST_TIMEOUT) // ✅ CORRIGIDO
       429 -> AppResult.Failure(DataError.Remote.TOO_MANY_REQUESTS) // ✅ CORRIGIDO
       in 500..599 -> AppResult.Failure(DataError.Remote.SERVER) // ✅ CORRIGIDO
       else -> AppResult.Failure(DataError.Remote.UNKNOWN) // ✅ CORRIGIDO
   }
}