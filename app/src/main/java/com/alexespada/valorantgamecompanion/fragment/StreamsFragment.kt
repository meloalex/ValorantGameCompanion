package com.alexespada.valorantgamecompanion.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alexespada.valorantgamecompanion.Constants
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.activity.TwitchLoginActivity
import com.alexespada.valorantgamecompanion.adapter.StreamsAdapter
import com.alexespada.valorantgamecompanion.models.Stream
import com.alexespada.valorantgamecompanion.models.StreamsResponse
import com.alexespada.valorantgamecompanion.services.NetworkManager
import com.alexespada.valorantgamecompanion.services.UserManager
import io.ktor.client.request.*
import kotlinx.android.synthetic.main.fragment_streams.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONObject
import kotlin.math.log

class StreamsFragment: Fragment() {

    private lateinit var loginTwitchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var streamsAdapter: StreamsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_streams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initRecyclerView()
        initListeners()
        checkUserAvailability()
    }

    private fun initViews(view: View)
    {
        loginTwitchButton = view.findViewById(R.id.twitchLoginButton)
        recyclerView = view.findViewById(R.id.streamRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
    }

    private fun initRecyclerView()
    {
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        streamsAdapter = StreamsAdapter(streamsList = listOf())
        recyclerView.adapter = streamsAdapter
    }

    private fun initListeners(){
        loginTwitchButton.setOnClickListener {
            val intent = Intent(activity, TwitchLoginActivity::class.java)
            startActivity(intent)
        }

        swipeRefreshLayout.setOnRefreshListener {
            getStreams()
        }
    }

    override fun onResume() {
        super.onResume()
        checkUserAvailability()
    }

    private fun checkUserAvailability()
    {
        val isLoggedIn = UserManager(requireContext()).getAccessToken() != null
        if (isLoggedIn)
        {
            loginTwitchButton.visibility = View.GONE
            getStreams()
        }
        else loginTwitchButton.visibility = View.VISIBLE
    }

    private fun getStreams()
    {
        swipeRefreshLayout.isRefreshing = true

        val httpClient = NetworkManager.createHttpClient()

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val accessToken = UserManager(requireContext()).getAccessToken()

               try {
                   val response = httpClient.get<StreamsResponse>("https://api.twitch.tv/helix/streams") {
                       header("Client-Id", Constants.OAUTH_CLIENT_ID)
                       header("Authorization", "Bearer ${accessToken}")
                       parameter("game_id", "516575")
                   }

                   withContext(Dispatchers.Main) {
                       streamsAdapter.streamsList = response.streams ?: listOf()
                       streamsAdapter.notifyDataSetChanged()
                       swipeRefreshLayout.isRefreshing = false
                   }
               } catch (t: Throwable) {
                   // TODO: Handle Error
                   Log.e("Twitch Streams", "${t.message}")
               }
            }
        }
    }

    private fun getGameId()
    {
        val httpClient = NetworkManager.createHttpClient()
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val accessToken = UserManager(requireContext()).getAccessToken()

                try {
                    val response = httpClient.get<String>("https://api.twitch.tv/helix/games?name=VALORANT") {
                        header("Client-Id", Constants.OAUTH_CLIENT_ID)
                        header("Authorization", "Bearer ${accessToken}")
                    }
                    Log.i("GAME ID", "$response")
                } catch (t: Throwable) {
                    // TODO: Handle Error
                }
            }
        }
    }
}