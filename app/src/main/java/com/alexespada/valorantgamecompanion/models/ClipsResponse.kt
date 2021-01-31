package com.alexespada.valorantgamecompanion.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ClipsResponse (
    @SerialName("data") val clips:List<Clip>?,
)