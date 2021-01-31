package com.alexespada.valorantgamecompanion.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Stream (
    @SerialName("title") val title:String? = null,
    @SerialName("user_name") val streamer:String? = null,
    @SerialName("thumbnail_url") val thumbnailUrl:String? = null,
    @SerialName("viewer_count") val viewers:String? = null,
    @SerialName("started_at") val startedAt:String? = null,
    @SerialName("user_id") val broadcasterID:String? = null,
)