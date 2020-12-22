package com.alexespada.valorantgamecompanion.fragment

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alexespada.valorantgamecompanion.LoginActivity
import com.alexespada.valorantgamecompanion.R
import com.alexespada.valorantgamecompanion.RegisterActivity
import com.alexespada.valorantgamecompanion.data.UserRepository
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import org.w3c.dom.Text

class ProfileFragment :  Fragment() {

    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button
    private lateinit var welcomeText: TextView
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
    }

    private fun checkUserAvailability()
    {
        Firebase.auth.currentUser?.let {
            registerButton.visibility = View.GONE
            loginButton.visibility = View.GONE
            logoutButton.visibility = View.VISIBLE
            welcomeTextView.visibility = View.VISIBLE

        } ?: run {
            registerButton.visibility = View.VISIBLE
            loginButton.visibility = View.VISIBLE
            logoutButton.visibility = View.GONE
            welcomeTextView.visibility = View.GONE
        }
    }
}