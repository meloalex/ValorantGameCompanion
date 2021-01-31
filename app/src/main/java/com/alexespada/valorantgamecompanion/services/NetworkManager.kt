package com.alexespada.valorantgamecompanion.services

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

object NetworkManager {
    fun createHttpClient(): HttpClient {
        // Config json parsing
        val jsonConfig = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }

        // Config Client
        return HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(jsonConfig)
            }
        }
    }
}