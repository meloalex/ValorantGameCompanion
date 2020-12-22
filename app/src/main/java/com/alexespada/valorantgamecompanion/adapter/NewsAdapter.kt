package com.alexespada.valorantgamecompanion.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.models.News
import com.squareup.picasso.Picasso
import java.net.URL

class NewsAdapter (var newsList:List<News>): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.NewsViewHolder {
        val newsView:View = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(newsView)
    }

    override fun onBindViewHolder(holder: NewsAdapter.NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.titleTextView.text = news.title

        news.imageUrl?.let {
            Picasso.with(holder.context).load(news.imageUrl).into(holder.backgroundImageView)
        }
    }

    override fun getItemCount(): Int {
        return newsList.count()
    }

    inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val backgroundImageView: ImageView = view.findViewById(R.id.newsImageView)
        val context = view.context
    }

}