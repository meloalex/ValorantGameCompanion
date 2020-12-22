package com.alexespada.valorantgamecompanion.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.models.Chat
import java.text.SimpleDateFormat

class ChatAdapter(var chatList:List<Chat>): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val chatView:View = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(chatView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.messageTextView.text = chat.message
        holder.usernameTextView.text = chat.username
        val format = SimpleDateFormat("hh:mm, dd/MM/yyyy")
        holder.dateTextView.text = format.format(chat.sentAt)
    }

    override fun getItemCount(): Int {
        return chatList.count()
    }

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView:TextView = view.findViewById(R.id.messageTextView)
        val usernameTextView:TextView = view.findViewById(R.id.usernameTextView)
        val dateTextView:TextView = view.findViewById(R.id.messageDateTextView)
    }
}