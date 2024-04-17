package com.example.ecosnap

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Comment:AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var token: String = ""
    private lateinit var listView: ListView
    private lateinit var post_id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.comments)

        sharedPreferences = getSharedPreferences("EcoSnap", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("user_token", "").toString()
        post_id = intent.getStringExtra("post_id").toString()
        val comment_text = findViewById<EditText>(R.id.addComment)
        listView = findViewById<ListView>(R.id.listView)
        Log.d("Comment", "Token: $token")
        fetchComments()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.comments)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val submit = findViewById<AppCompatButton>(R.id.submitButton)

        // Call retrofit to add comment
        submit.setOnClickListener {
            val commentText = comment_text.text.toString()

            if (commentText.isNotEmpty()) {
                val comment = CommentFields(post_id.toString(), commentText)

                RetrofitClient.instance.addComment(token, comment)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                // Handle success
                                Toast.makeText(this@Comment, "Comment added successfully", Toast.LENGTH_SHORT).show()
                                // Clear the comment text after successful addition
                                comment_text.text.clear()
                            } else {
                                Log.e("Comment", response.toString())
                                // Handle error
                                Toast.makeText(this@Comment, "Failed to add comment", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            // print error
                            Log.e("Comment", "Failed to add comment", t)
                            // Handle failure
                            Toast.makeText(this@Comment, "Failed to add comment", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                // Display a message indicating that comment text is empty
                Toast.makeText(this@Comment, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchComments() {
        // Call Retrofit to fetch comments for the given post_id
        RetrofitClient.instance.fetchComments(token, post_id)
            .enqueue(object : Callback<List<CommentResponse>> {
                override fun onResponse(call: Call<List<CommentResponse>>, response: Response<List<CommentResponse>>) {
                    if (response.isSuccessful) {
                        // Handle success
                        val comments = response.body()
                        // Update your UI with the fetched comments
                        // For example, you can use an ArrayAdapter to display comments in a ListView
                        comments?.let {
                            val adapter = ArrayAdapter(this@Comment, android.R.layout.simple_list_item_1, it)
                            listView.adapter = adapter
                        }
                    } else {
                        Log.e("Comment", "Failed to fetch comments: ${response}")
                        Log.e("Comment", "Failed to fetch comments: ${response.code()}")
                        // Handle error
                        Toast.makeText(this@Comment, "Failed to fetch comments", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                    // Print error
                    Log.e("Comment", "Failed to fetch comments", t)
                    // Handle failure
                    Toast.makeText(this@Comment, "Failed to fetch comments", Toast.LENGTH_SHORT).show()
                }
            })
    }


}