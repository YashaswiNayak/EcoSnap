package com.example.ecosnap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileView:AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var token: String = ""
    private var posts: List<PostResponse>? = null
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
        val honor_points = findViewById<TextView>(R.id.honor_points)
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
                        name.text = userDetails?.username
                        honor_points.text = "Honor Points: " + userDetails?.honorPoints.toString()
                        fetchPosts()
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
    private fun populatePostsUI() {
        val container: LinearLayout = findViewById(R.id.main1)
        posts?.forEach { post ->
            Log.d("POST DEBUG", "user ${post.user}")
            val postLayout = layoutInflater.inflate(R.layout.post, null)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.width = 800 // Change as needed
            layoutParams.bottomMargin = 20
            layoutParams.topMargin = 20
            postLayout.setPadding(20, 20, 20, 20)
            layoutParams.gravity = Gravity.CENTER
            postLayout.layoutParams = layoutParams
            val likeButton: ImageButton = postLayout.findViewById(R.id.likeButton)
            val visitPost: LinearLayout =postLayout.findViewById(R.id.visitPost)
            // Populate the postLayout with data from the post
            val username:TextView = postLayout.findViewById(R.id.username)
            val title: TextView = postLayout.findViewById(R.id.title)
            val postImage: ImageView = postLayout.findViewById(R.id.postImage)
            Glide.with(this)
                .load(post.imageUrl) // Assuming post.imageUrl contains the URL of the image
                .into(postImage)
            title.text = post.title
            username.text = post.user.username

            // Additional setup for likeButton, visitPost, actionButton, commentButton...
            visitPost.setOnClickListener{
                val profileView= Intent(this,ProfileView::class.java).apply {

                }
                startActivity(profileView)
            }
            likeButton.setImageResource(R.drawable.ic_heart)
            var image = false
            likeButton.setOnClickListener {
                image = !image
                if (image) {
                    likeButton.setImageResource(R.drawable.ic_heart_red)
                } else {
                    likeButton.setImageResource(R.drawable.ic_heart)
                }
            }
            val actionButton=postLayout.findViewById<Button>(R.id.actionButton)
            val commentButton=postLayout.findViewById<Button>(R.id.commentButton)
            actionButton.setOnClickListener {
                val participate = Intent(this, Participate::class.java)
                participate.putExtra("post_id", post._id)
                participate.putExtra("poster_id", post.user._id)
                participate.putExtra("title", post.title)
                participate.putExtra("desc", post.content)
                participate.putExtra("poster", post.user.username)
                participate.putExtra("imgUrl", post.imageUrl)
                participate.putExtra("time", post.timestamp)
                startActivity(participate)
            }
            commentButton.setOnClickListener {
                val comment = Intent(this, Comment::class.java)
                startActivity(comment)
            }

            container.addView(postLayout)
        }
    }

    private fun fetchPosts() {
        token.let { RetrofitClient.instance.fetchUserPosts(it) }
            .enqueue(object : Callback<List<PostResponse>> {
                override fun onResponse(
                    call: Call<List<PostResponse>>,
                    response: Response<List<PostResponse>>
                ) {
                    if (response.isSuccessful) {
                        posts = response.body()
                        populatePostsUI()
                    } else {
                        Toast.makeText(this@ProfileView, "Error fetching posts", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<List<PostResponse>>, t: Throwable) {
                    Log.e("Network Error", "Failed to fetch posts", t)
                    Toast.makeText(this@ProfileView, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}