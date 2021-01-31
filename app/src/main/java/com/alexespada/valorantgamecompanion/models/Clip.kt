package com.alexespada.valorantgamecompanion.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Clip (
    @SerialName("title") val title:String? = null,
    @SerialName("url") val url:String? = null,
    @SerialName("thumbnail_url") val thumbnailUrl:String? = null,
    @SerialName("view_count") val viewsCount:String? = null,
    @SerialName("created_at") val date:String? = null,
        )