package com.alexespada.valorantgamecompanion.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.activity.StreamDetail
import com.alexespada.valorantgamecompanion.models.Stream
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class StreamsAdapter(var streamsList:List<Stream>):RecyclerView.Adapter<StreamsAdapter.StreamsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamsAdapter.StreamsViewHolder {
        val streamsView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_stream, parent, false)
        return StreamsViewHolder(streamsView)
    }

    override fun onBindViewHolder(holder: StreamsAdapter.StreamsViewHolder, position: Int) {
        val stream = streamsList[position]
        holder.titleTextView.text = stream.title
        holder.viewersTextView.text = stream.viewers
        stream.thumbnailUrl?.let {
            val url = stream.thumbnailUrl.replace("{width}x{height}", "1280x720")
            Picasso.with(holder.context).load(url).into(holder.thumbnail)
        }

        holder.card.setOnClickListener {
            val intent = Intent(holder.context, StreamDetail::class.java)
            intent.putExtra("stream_title", stream.title)
            intent.putExtra("user_name", stream.streamer)
            intent.putExtra("viewer_count", stream.viewers)
            intent.putExtra("started_at", stream.startedAt)
            intent.putExtra("user_id", stream.broadcasterID)
            holder.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return streamsList.count()
    }

    inner class StreamsViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        val titleTextView:TextView = view.findViewById(R.id.titleTextView)
        val thumbnail:ImageView = view.findViewById(R.id.streamImageView)
        val viewersTextView:TextView = view.findViewById(R.id.viewersTextView)
        val card:MaterialCardView = view.findViewById(R.id.cardMaterialCard)
        val context = view.context
    }
}