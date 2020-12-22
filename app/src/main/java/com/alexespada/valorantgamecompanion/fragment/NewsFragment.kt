package com.alexespada.valorantgamecompanion.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alexespada.valorantgamecompanion.Constants
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.adapter.ChatAdapter
import com.alexespada.valorantgamecompanion.adapter.NewsAdapter
import com.alexespada.valorantgamecompanion.models.Chat
import com.alexespada.valorantgamecompanion.models.News
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NewsFragment : Fragment() {

    private lateinit var recyclerView:RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        initViews(view)
        initRecyclerView()
        initListeners()
        getNews()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
    }

    private fun initRecyclerView() {
        // Layout Manager
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        // Adapter
        newsAdapter = NewsAdapter(newsList = listOf())
        recyclerView.adapter = newsAdapter
    }

    private fun initListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            getNews()
        }
    }

    private fun getNews() {
        swipeRefreshLayout.isRefreshing = true

        firestore.collection(Constants.COLLECTION_NEWS).orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val chats: List<News> =
                    it.result?.documents?.mapNotNull { it.toObject(News::class.java) }.orEmpty()
                newsAdapter.newsList = chats
                newsAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this.context, getString(R.string.error_fetching_news), Toast.LENGTH_SHORT).show()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }
}