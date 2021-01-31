package com.alexespada.valorantgamecompanion

object Constants {

    // Firestore
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CHAT = "chat"
    const val COLLECTION_NEWS = "news"

    // Twitch
    const val OAUTH_CLIENT_ID = "tthwyfnbiv3gue4bythkyqee67tgrx"
    const val OAUTH_CLIENT_SECRET = "wd7meic1orl7hbprg0hemxfs0740ci"
    const val OAUTH_REDIRECT_URI = "http://localhost"
    const val OAUTH_AUTHORIZE_URI = "https://id.twitch.tv/oauth2/authorize"

    // Codes
    const val REQUEST_IMAGE_CAPTURE = 100

    // Twitch API
    const val CLIPS_API = "https://api.twitch.tv/helix/clips"
    const val STREAM_EMBED_URL = "https://m.twitch.tv/<user>"
    const val STREAM_URL = "https://www.twitch.tv/<user>"
}