package com.example.ecosnap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Participate:AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var token: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.participate)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.participate)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences("EcoSnap", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("user_token", "") ?: ""
        val postId = intent.getStringExtra("post_id")
        val posterId = intent.getStringExtra("poster_id")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("desc")
        val poster = intent.getStringExtra("poster")
        val imgUrl = intent.getStringExtra("imgUrl")
        val time = intent.getStringExtra("timestamp")

        val titleBox = findViewById<TextView>(R.id.title)
        val descBox = findViewById<TextView>(R.id.description)
        val postImage = findViewById<ImageView>(R.id.postImage)

        titleBox.text = title
        descBox.text = description
        Glide.with(this)
            .load(imgUrl) // Assuming post.imageUrl contains the URL of the image
            .into(postImage)

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup1)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            // Check which radio button is selected
            when (checkedId) {
                R.id.radioButton1 -> {
                    val send = postId?.let { OptRes(it) }
                    if (send != null) {
                        RetrofitClient.instance.optIn(token, send).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                if (response.isSuccessful) {
                                    // Handle the response
                                    val postResponse = response.body()
                                    Log.d("Response", "Opted in successfully")
                                } else {
                                    Log.d("err res", response.toString())
                                    // Handle error
                                    Log.e("Error", "Failed to opt in")
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                // Handle failure
                                Log.e("Error", "Error opting in", t)
                            }
                        })
                    }
                    // The first radio button is selected
                    // Handle your logic here
                }
                R.id.radioButton2 -> {
                    // The second radio button is selected
                    // Handle your logic here
                    val send = postId?.let { OptRes(it) }
                    if (send != null) {
                        RetrofitClient.instance.optOut(token, send).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                if (response.isSuccessful) {
                                    // Handle the response
                                    val postResponse = response.body()
                                    Log.d("Response", "Opted out successfully")
                                } else {
                                    Log.d("err res", response.toString())
                                    // Handle error
                                    Log.e("Error", "Failed to opt out")
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                // Handle failure
                                Log.e("Error", "Error opting out", t)
                            }
                        })
                    }
                }
            }
        }


        val amount=findViewById<EditText>(R.id.amount)
        val donate=findViewById<Button>(R.id.donate)
        donate.setOnClickListener{
            Toast.makeText(this@Participate,"Amount: ${amount.text}",Toast.LENGTH_SHORT).show()
            val returnToMain= Intent(this,Homepage::class.java).apply {

            }
            startActivity(returnToMain)
        }
    }
}