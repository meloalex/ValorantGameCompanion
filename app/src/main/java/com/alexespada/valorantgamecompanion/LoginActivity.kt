package com.alexespada.valorantgamecompanion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var createAccountText: TextView
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        emailEditText = findViewById<EditText>(R.id.emailEditText)
        passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        createAccountText = findViewById<TextView>(R.id.createAccountTextButton)
        loginButton = findViewById<Button>(R.id.loginButton)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        initListeners()
    }

    private fun initListeners()
    {
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (!isEmailValid(email)) {
                Log.i("RegisterActivity", "Email not valid")
                emailEditText.error = getString(R.string.error_email_invalid);
                return@setOnClickListener
            }

            val password = passwordEditText.text.toString()
            if(!isPasswordValid(password))
            {
                Log.i("RegisterActivity", "Password not valid")
                passwordEditText.error = getString(R.string.error_password_invalid)
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        createAccountText.setOnClickListener {
            finish()
            Firebase.analytics.logEvent("createAnAccountButtonClick", null)
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isEmailValid(email:String) : Boolean {
        return email.contains(Regex("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+\$"))
    }

    private fun isPasswordValid(password:String) : Boolean {
        return password.contains(Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"))
    }

    private fun loginUser(email:String, password:String) {
        progressBar.visibility = View.VISIBLE
        loginButton.isEnabled = false

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("LoginActivity", "User Login!")
                finish()
            } else {
                Toast.makeText(this, getString(R.string.error_logging_in, it.exception?.message), Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                loginButton.isEnabled = true
            }
        }
    }

}