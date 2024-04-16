package com.example.ecosnap

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileView:AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var token: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile)
        var sharedPreferences = getSharedPreferences("EcoSnap", Context.MODE_PRIVATE)
        var token = sharedPreferences.getString("user_token", "")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<TextView>(R.id.user_name)
        //val email = findViewById<TextView>(R.id.email)
        sharedPreferences = getSharedPreferences("EcoSnap", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("user_token", "") ?: ""
        token.let { RetrofitClient.instance.getUserDetails(it) }
            .enqueue(object : Callback<UserDetails> {
                override fun onResponse(call: Call<UserDetails>, response: Response<UserDetails>) {
                    if (response.isSuccessful) {
                        val userDetails = response.body()
                        Toast.makeText(this@ProfileView, userDetails.toString(), Toast.LENGTH_SHORT).show()
                        // Update your UI with the user details
                        // For example, set text to TextViews
                        name.text = userDetails?.email
                        //email.text = userDetails?.email
                        //shToken.text = token
                    } else {
                        // Handle error
                        Toast.makeText(
                            this@ProfileView,
                            "Error fetching user details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UserDetails>, t: Throwable) {
                    // Handle failure
                    Toast.makeText(this@ProfileView, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}