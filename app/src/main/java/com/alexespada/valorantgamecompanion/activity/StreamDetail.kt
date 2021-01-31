package com.alexespada.valorantgamecompanion.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexespada.valorantgamecompanion.Constants
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.adapter.ClipsAdapter
import com.alexespada.valorantgamecompanion.models.ClipsResponse
import com.alexespada.valorantgamecompanion.services.NetworkManager
import com.alexespada.valorantgamecompanion.services.UserManager
import io.ktor.client.request.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_stream_detail.*
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StreamDetail : AppCompatActivity() {

    private lateinit var titleTextView:TextView
    private lateinit var steamerNameTextView:TextView
    private lateinit var viewersCountTextView:TextView
    private lateinit var streamTimeTextView:TextView
    private lateinit var openStreamButton:Button
    private lateinit var webView:WebView
    private lateinit var clipsRecyclerView:RecyclerView
    private lateinit var clipsAdapter: ClipsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream_detail)

        initView()
        initRecyclerView()
        loadParams()
        loadStreamView()
        initListeners()
        getClips(this)
    }

    private fun initView()
    {
        titleTextView = findViewById(R.id.streamTitleTextView)
        viewersCountTextView = findViewById(R.id.viewersCountTextView)
        steamerNameTextView = findViewById(R.id.streamerNameTextView)
        streamTimeTextView = findViewById(R.id.timestampTextView)
        openStreamButton = findViewById(R.id.openStreamButton)
        webView = findViewById(R.id.streamWebView)
        clipsRecyclerView = findViewById(R.id.clipsRecyclerView)
    }

    private fun initListeners()
    {
        openStreamButton.setOnClickListener {
            CustomDialog(this, {
                val userName:String = intent.getStringExtra("user_name")!!
                val streamUrl:String = Constants.STREAM_URL.replace("<user>", userName)
                val browerIntent = Intent(Intent.ACTION_VIEW, Uri.parse(streamUrl))
                startActivity(browerIntent)
            }).show()
        }
    }

    private fun loadParams()
    {
        titleTextView.text = intent.getStringExtra("stream_title")!!
        viewersCountTextView.text = intent.getStringExtra("viewer_count")!!
        steamerNameTextView.text = intent.getStringExtra("user_name")!!
        streamTimeTextView.text = intent.getStringExtra("started_at")!!
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this@StreamDetail, LinearLayoutManager.HORIZONTAL, false)
        clipsRecyclerView.layoutManager = layoutManager

        clipsAdapter = ClipsAdapter(listOf())
        clipsRecyclerView.adapter = clipsAdapter
    }

    private fun loadStreamView()
    {
        val userName:String = intent.getStringExtra("user_name")!!
        val streamUrl:String = Constants.STREAM_EMBED_URL.replace("<user>", userName)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(streamUrl)
    }

    private fun getClips(context: Context)
    {
        val broadcasterID = intent.getStringExtra("user_id")!!
        val httpClient = NetworkManager.createHttpClient()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val accessToken = UserManager(applicationContext).getAccessToken()

                try {
                    val response = httpClient.get<ClipsResponse>(Constants.CLIPS_API) {
                        header("Client-Id", Constants.OAUTH_CLIENT_ID)
                        header("Authorization", "Bearer ${accessToken}")
                        parameter("broadcaster_id", broadcasterID)
                    }

                    withContext(Dispatchers.Main) {
                        clipsAdapter.clipsList = response.clips ?: listOf()
                        clipsAdapter.notifyDataSetChanged()
                    }

                } catch (t: Throwable) {
                    Toast.makeText(context, getString(R.string.generic_network_error), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}