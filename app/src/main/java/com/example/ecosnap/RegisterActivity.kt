package com.example.ecosnap

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)
        val username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val loginRedirect = findViewById<TextView>(R.id.loginRedirect)
        btnRegister.setOnClickListener {
            val usernameText = username.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (usernameText.isNotEmpty() && passwordText.isNotEmpty()) {
                val user = User(usernameText, emailText, passwordText)
                registerUser(user)
            } else {
                // Show error message
            }
        }

        loginRedirect.setOnClickListener(){
            finish()
        }
    }

    private fun registerUser(user: User) {
        RetrofitClient.instance.register(user).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Register Success", Toast.LENGTH_SHORT).show()

                    // Handle successful registration
                    // For example, navigate to the login screen
                } else {
                    // Handle error
                    Toast.makeText(this@RegisterActivity, "Register Failed", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure
                Toast.makeText(this@RegisterActivity, "Register Failed", Toast.LENGTH_SHORT).show()

            }
        })
    }
}