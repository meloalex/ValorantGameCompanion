package com.alexespada.valorantgamecompanion.activity

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.alexespada.valorantgamecompanion.Constants
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.models.OAuthTokensResponse
import com.alexespada.valorantgamecompanion.services.NetworkManager
import com.alexespada.valorantgamecompanion.services.UserManager
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.ContentType.Application.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TwitchLoginActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitch_login)
        initViews()
        loadOAuthUrl(this)
    }

    private fun initViews(){
        webView = findViewById(R.id.webView)
    }

    private fun loadOAuthUrl(context: Context)
    {
        var uri = Uri.parse(Constants.OAUTH_AUTHORIZE_URI)
            .buildUpon()
            .appendQueryParameter("client_id", Constants.OAUTH_CLIENT_ID)
            .appendQueryParameter("redirect_uri", Constants.OAUTH_REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", listOf("user:edit", "user:read:email").joinToString (" "))
            .build()

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {

                if (request?.url?.toString()?.startsWith(Constants.OAUTH_REDIRECT_URI) == true)
                {
                    request.url.getQueryParameter("code")?.let {
                        // Exchange code -> access token
                        webView.visibility = View.GONE
                        getAccessToken(it, context)
                    } ?: run {
                        Toast.makeText(context, getString(R.string.error_twitch_auth), Toast.LENGTH_LONG).show()
                    }
                }

                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        webView.loadUrl(uri.toString())
    }

    private fun getAccessToken(authorizationCode: String, context: Context) {

        val httpClient = NetworkManager.createHttpClient()

        // Assign to Activity Scope
        lifecycleScope.launch {

            // Change to Background Thread
            withContext(Dispatchers.IO) {

                try {
                    val response:OAuthTokensResponse = httpClient.post<OAuthTokensResponse>("https://id.twitch.tv/oauth2/token") {
                        parameter("client_id", Constants.OAUTH_CLIENT_ID)
                        parameter("client_secret", Constants.OAUTH_CLIENT_SECRET)
                        parameter("code", authorizationCode)
                        parameter("grant_type", "authorization_code")
                        parameter("redirect_uri", Constants.OAUTH_REDIRECT_URI)
                    }
                    Log.i("Twitch Auth","Response: $response")
                    UserManager(this@TwitchLoginActivity).saveAccessToken(response.accessToken)
                    finish()
                } catch (t: Throwable) {
                    Toast.makeText(context, getString(R.string.error_twitch_auth), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}