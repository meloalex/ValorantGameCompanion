package com.alexespada.valorantgamecompanion.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.models.Clip
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

class ClipsAdapter (var clipsList:List<Clip>):RecyclerView.Adapter<ClipsAdapter.ClipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipViewHolder {
        val clipsView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_clip, parent, false)
        return ClipViewHolder(clipsView)
    }

    override fun onBindViewHolder(holder: ClipViewHolder, position: Int) {
        val clip = clipsList[position]
        holder.titleTextureView.text = clip.title
        clip.thumbnailUrl?.let {
            val url = clip.thumbnailUrl.replace("{width}x{height}", "1280x720")
            Picasso.with(holder.context).load(url).into(holder.thumbnail)
        }

        holder.card.setOnClickListener {
            val browerIntent = Intent(Intent.ACTION_VIEW, Uri.parse(clip.url))
            holder.context.startActivity(browerIntent)
            Log.i("Clip Adapter:", clip.url!!)
        }
    }

    override fun getItemCount(): Int {
        return clipsList.count()
    }

    inner class ClipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextureView:TextView = view.findViewById(R.id.titleTextView)
        val thumbnail:ImageView = view.findViewById(R.id.thumnailImageView)
        val card:MaterialCardView = view.findViewById(R.id.clipCard)
        val context = view.context
    }
}