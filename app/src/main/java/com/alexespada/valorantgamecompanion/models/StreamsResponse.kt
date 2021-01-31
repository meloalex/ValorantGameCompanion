package com.alexespada.valorantgamecompanion.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class StreamsResponse (
    @SerialName("data") val streams:List<Stream>?,
    )