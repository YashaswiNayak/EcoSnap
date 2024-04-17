package com.example.ecosnap

import android.content.Context
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.comments)

        val sharedPreferences = getSharedPreferences("EcoSnap", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("user_token", "")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.comments)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val post_id = intent.getStringExtra("post_id")
        val comment_text = findViewById<EditText>(R.id.addComment)
        val listView = findViewById<ListView>(R.id.listView)
        val submit = findViewById<AppCompatButton>(R.id.submitButton)

        // Call retrofit to add comment
        submit.setOnClickListener {
            val commentText = comment_text.text.toString()

            if (commentText.isNotEmpty() && token != null) {
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

}