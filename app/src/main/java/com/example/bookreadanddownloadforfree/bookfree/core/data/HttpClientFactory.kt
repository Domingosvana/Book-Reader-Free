package com.example.bookreadanddownloadforfree.bookfree.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(engine: HttpClientEngine): HttpClient {


        return HttpClient(engine) {
            install(ContentNegotiation)  {
                json(
                    json = Json{
                        prettyPrint = true
                        //  isLenient = true
                        ignoreUnknownKeys = true
                       // classDiscriminator = "type"
                        encodeDefaults = true
                    }
                )


            }
            install(HttpTimeout){
                //  requestTimeoutMillis = 15000L
                //  connectTimeoutMillis = 15000L
                //  socketTimeoutMillis = 15000L
                socketTimeoutMillis =  60000L
                requestTimeoutMillis =  60000L

            }

            install(Logging){
                logger = object:Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level  = LogLevel.ALL

            }

            defaultRequest { contentType(ContentType.Application.Json) }



        }
    }


}