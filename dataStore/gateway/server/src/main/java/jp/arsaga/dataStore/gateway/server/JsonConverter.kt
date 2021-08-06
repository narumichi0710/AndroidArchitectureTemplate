package jp.arsaga.dataStore.gateway.server

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

object JsonConverter {

    private val contentType = "application/json".toMediaType()

    private val format = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val factory = format.asConverterFactory(contentType)
}