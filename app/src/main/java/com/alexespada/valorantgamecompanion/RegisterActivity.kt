package com.alexespada.valorantgamecompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.alexespada.valorantgamecompanion.models.User
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    private val MIN_PASWORD_LENGTH = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        firestore = Firebase.firestore

        emailEditText = findViewById<EditText>(R.id.emailEditText)
        usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        registerButton = findViewById<Button>(R.id.registerButton)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        initListeners()
    }

    private fun initListeners()
    {
        registerButton.setOnClickListener {
            // Validate Email
            val email = emailEditText.text.toString()
            if (!isEmailValid(email)) {
                Log.i("RegisterActivity", "Email not valid")
                emailEditText.error = getString(R.string.error_email_invalid);
                return@setOnClickListener
            }

            // Validate Password
            val password = passwordEditText.text.toString()
            if(!isPasswordValid(password))
            {
                Log.i("RegisterActivity", "Password not valid")
                passwordEditText.error = getString(R.string.error_password_invalid, MIN_PASWORD_LENGTH)
                return@setOnClickListener
            }

            // Validate Username
            val username = usernameEditText.text.toString()
            if (!isUsernameValid(username))
            {
                Log.i("RegisterActivity", "Username not valid")
                usernameEditText.error = getString(R.string.error_username_invalid)
                return@setOnClickListener
            }

            registerUser(email, password, username)
        }
    }

    private fun isEmailValid(email:String) : Boolean {
        return email.contains(Regex("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+\$"))
    }

    private fun isPasswordValid(password:String) : Boolean {
        return password.contains(Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"))
    }

    private fun isUsernameValid(username:String) : Boolean {
        return !username.isBlank()
    }

    private fun registerUser(email: String, password: String, username:String)
    {
        progressBar.visibility = View.VISIBLE
        registerButton.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("RegisterActivity", "User Registered!")
                auth.currentUser?.uid?.let  { userId ->
                    val user = User(userId, username)
                    firestore
                        .collection(Constants.COLLECTION_USERS)
                        .document(userId)
                        .set(user)
                        .addOnCompleteListener{
                            if (it.isSuccessful)
                            {
                                Firebase.analytics.logEvent("userRegistered", null)
                                finish()
                            } else {
                                Toast.makeText(this, getString(R.string.error_sign_up, it.exception?.message), Toast.LENGTH_LONG).show()
                                progressBar.visibility = View.GONE
                                registerButton.isEnabled = true
                            }
                        }
                } ?: kotlin.run {
                    Log.e("RegisterActivity", "Error: userId is null")
                    progressBar.visibility = View.GONE
                    registerButton.isEnabled = true
                    Toast.makeText(this, getString(R.string.error_sign_up, it.exception?.message), Toast.LENGTH_LONG).show()
                }
            } else {
                Log.e("RegisterActivity", "Error: ${it.exception}")
                progressBar.visibility = View.GONE
                registerButton.isEnabled = true
                Toast.makeText(this, getString(R.string.error_sign_up, it.exception?.message), Toast.LENGTH_LONG).show()
            }
        }
    }
}