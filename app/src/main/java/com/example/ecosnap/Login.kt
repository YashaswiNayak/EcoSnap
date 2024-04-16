package com.example.ecosnap

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        var password = findViewById<EditText>(R.id.password)
        var username = findViewById<EditText>(R.id.username)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerRedirect = findViewById<TextView>(R.id.registerRedirect)
        registerRedirect.setOnClickListener(){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()

            // Create a LoginCredentials object
            val credentials = LoginCredentials(usernameText, passwordText)

            // Use Retrofit to make a login request
            val retrofit = provideRetrofit(this)
            val service = retrofit.create(ApiService::class.java)
            val call = service.login(credentials)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        // Handle successful login
                        val token = response.body()?.token
                        // Save the token using SessionManager
                        val sessionManager = SessionManager(this@Login)
                        sessionManager.saveAuthToken(token ?: "")
                        Toast.makeText(this@Login, "Login Success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login, Homepage::class.java)
                        startActivity(intent)
                        // Navigate to the next activity or show a success message
                    } else {
                        // Handle login failure
                        Toast.makeText(this@Login, response.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Handle failure
                    Toast.makeText(this@Login, "Login Failed", Toast.LENGTH_SHORT).show()

                }
            })
        }
    }

    fun provideRetrofit(context: Context): Retrofit {
        val sessionManager = SessionManager(context)
        val token = sessionManager.fetchAuthToken()

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://ecosnap-server.onrender.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}