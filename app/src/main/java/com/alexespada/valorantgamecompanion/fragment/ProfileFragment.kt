package com.alexespada.valorantgamecompanion.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alexespada.valorantgamecompanion.Constants
import com.alexespada.valorantgamecompanion.Constants.REQUEST_IMAGE_CAPTURE
import com.alexespada.valorantgamecompanion.activity.LoginActivity
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.activity.RegisterActivity
import com.alexespada.valorantgamecompanion.models.User
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream

class ProfileFragment :  Fragment() {

    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button
    private lateinit var avatarButton: Button
    private lateinit var welcomeText: TextView
    private lateinit var avatarImageView: ImageView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        checkUserAvailability()
    }

    private fun initViews(parentView:View)
    {
        auth = Firebase.auth

        registerButton = parentView.findViewById<Button>(R.id.registerButton)
        loginButton = parentView.findViewById<Button>(R.id.loginButton)
        logoutButton = parentView.findViewById<Button>(R.id.logoutButton)
        welcomeText = parentView.findViewById<TextView>(R.id.welcomeTextView)
        avatarButton = parentView.findViewById(R.id.avatarButton)
        avatarImageView = parentView.findViewById<ImageView>(R.id.avatarImageView)
        avatarImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private fun initListeners() {
        registerButton.setOnClickListener{
            Firebase.analytics.logEvent("registerButtonClick", null)
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            Log.i("ProfileFragment", "Login Button Click")
            Firebase.analytics.logEvent("loginButtonClick", null)
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            Log.i("ProfileFragment", "Logout Button Click")
            Firebase.analytics.logEvent("logoutButtonClick", null)
            auth.signOut()
            checkUserAvailability()
        }

        avatarButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent()
    {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {

        }
    }

    private fun checkUserAvailability()
    {
        Firebase.auth.currentUser?.let { user ->
            registerButton.visibility = View.GONE
            loginButton.visibility = View.GONE
            logoutButton.visibility = View.VISIBLE
            welcomeTextView.visibility = View.VISIBLE
            avatarButton.visibility = View.VISIBLE

            // Get user data
            Firebase.firestore.collection(Constants.COLLECTION_USERS).document(user.uid).get().addOnSuccessListener {
                it.toObject(User::class.java)?.let { user ->
                    val avatarUrl = user.avatarUrl
                    Picasso.with(context).load(avatarUrl).into(avatarImageView)
                }
            }

        } ?: run {
            registerButton.visibility = View.VISIBLE
            loginButton.visibility = View.VISIBLE
            logoutButton.visibility = View.GONE
            welcomeTextView.visibility = View.GONE
            avatarButton.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            (data?.extras?.get("data") as? Bitmap)?.let{ bitmap ->
                uploadImage(bitmap)
                avatarImageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun uploadImage(bitmap: Bitmap) {
        val storage = Firebase.storage

        // Convert bitmap to bytes list
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Firebase reference
        val userId = Firebase.auth.currentUser?.uid
        val reference = storage.reference.child("images/avatars/$userId.jpg")

        // Upload
        reference.putBytes(data).addOnSuccessListener { taskSnapshot ->
            reference.downloadUrl.addOnSuccessListener { uri ->
                val url = uri.toString()
                Firebase.firestore.collection(Constants.COLLECTION_USERS).document(userId!!).update("avatarUrl", url)
            }.addOnFailureListener {
                Toast.makeText(context, getString(R.string.error_uploading_picture), Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, getString(R.string.error_uploading_picture), Toast.LENGTH_LONG).show()
        }
    }
}