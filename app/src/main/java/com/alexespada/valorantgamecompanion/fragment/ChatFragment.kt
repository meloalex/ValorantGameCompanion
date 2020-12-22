package com.alexespada.valorantgamecompanion.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alexespada.valorantgamecompanion.Constants.COLLECTION_CHAT
import com.alexespada.valorantgamecompanion.Constants.COLLECTION_USERS
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.adapter.ChatAdapter
import com.alexespada.valorantgamecompanion.models.Chat
import com.alexespada.valorantgamecompanion.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var firestore: FirebaseFirestore
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        initViews(view)
        initRecyclerView()
        initListeners()
        getChats()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        messageEditText = view.findViewById(R.id.messageEditText)
        sendButton = view.findViewById(R.id.sendButton)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
    }

    private fun initRecyclerView() {
        // Layout Manager
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        // Adapter
        chatAdapter = ChatAdapter(chatList = listOf())
        recyclerView.adapter = chatAdapter
    }

    private fun initListeners() {
        sendButton.setOnClickListener {
            Log.i("Chat", "Tap button")
            val message = messageEditText.text.toString()
            if (message.isBlank()) return@setOnClickListener
            sendMessage(message)
        }

        swipeRefreshLayout.setOnRefreshListener {
            getChats()
        }
    }

    private fun sendMessage(message: String) {
        Firebase.auth.currentUser?.uid?.let { uid: String ->
            // User Available
            firestore.collection(COLLECTION_USERS).document(uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result?.toObject(User::class.java)?.let { user:User ->
                        val chat = Chat(
                            userId = uid,
                            message = message,
                            sentAt = Date().time,
                            isSent = false,
                            imageUrl = null,
                            username = user.username,
                            avatarUrl = null,
                        )

                        firestore.collection(COLLECTION_CHAT).add(chat).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.i("Chat", "Success uploading message $message")
                                getChats()
                            } else {
                                Log.w("Chat", "Error uploading message $message")
                            }
                        }

                        // Clear Text container
                        messageEditText.text.clear()

                    } ?: run {
                        Toast.makeText(this.context, getString(R.string.generic_error), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this.context, getString(R.string.generic_error), Toast.LENGTH_LONG).show()
                }
            }
        } ?: run {
            // User not available
            Toast.makeText(this.context, getString(R.string.user_not_authenticated), Toast.LENGTH_LONG).show()
        }
    }

    private fun getChats() {
        swipeRefreshLayout.isRefreshing = true

        firestore.collection(COLLECTION_CHAT).orderBy("sentAt", Query.Direction.DESCENDING).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val chats: List<Chat> =
                    it.result?.documents?.mapNotNull { it.toObject(Chat::class.java) }.orEmpty()
                chatAdapter.chatList = chats
                chatAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this.context, getString(R.string.error_fetching_chat), Toast.LENGTH_SHORT).show()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }
}