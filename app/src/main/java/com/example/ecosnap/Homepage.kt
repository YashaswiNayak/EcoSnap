package com.example.ecosnap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class Homepage:AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var sharedPreferences: SharedPreferences
    private var token: String = ""
    private var posts: List<PostResponse>? = null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_app)
        sharedPreferences = getSharedPreferences("EcoSnap", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("user_token", "") ?: ""
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        if (token.isNotEmpty()) {
            Log.d("Fetching posts now", "fetching")
            fetchPosts()
            Log.d("Fetching posts now", "fetched")
            posts?.get(0)?.let { Log.d("test post", it.title) }
        } else {
            // Handle the case where the token is empty
            // This could involve showing an error message or redirecting the user to a login screen
            Toast.makeText(this, "Please log in to fetch posts", Toast.LENGTH_SHORT).show()
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Proceed with location operations
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    Log.d("Location", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
                    getCityName(it)
                }
            }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val topToolbar = findViewById<Toolbar>(R.id.topToolbar)
        val newPost=findViewById<ImageButton>(R.id.newPost)
        val container: LinearLayout =findViewById(R.id.main)
        setSupportActionBar(topToolbar)
        val logout = findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val login = Intent(this, Login::class.java)
            startActivity(login)
        }

        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false) // Hide default title
            setDisplayHomeAsUpEnabled(false)
        }

//        repeat(3){
//            val postLayout=layoutInflater.inflate(R.layout.post,null)
//            val layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            layoutParams.width = 800 // Change as needed
//            layoutParams.bottomMargin=20
//            layoutParams.topMargin=20
//            postLayout.setPadding(20, 20, 20, 20)
//            layoutParams.gravity= Gravity.CENTER
//            postLayout.layoutParams = layoutParams
//            val likeButton: ImageButton = postLayout.findViewById(R.id.likeButton)
//            val visitPost:LinearLayout=postLayout.findViewById(R.id.visitPost)
//            val title: TextView = postLayout.findViewById(R.id.title)
//            title.text = "testing hello"
//            visitPost.setOnClickListener{
//                val profileView= Intent(this,ProfileView::class.java).apply {
//
//                }
//                startActivity(profileView)
//            }
//            likeButton.setImageResource(R.drawable.ic_heart)
//            var image = false
//            likeButton.setOnClickListener {
//                image = !image
//                if (image) {
//                    likeButton.setImageResource(R.drawable.ic_heart_red)
//                } else {
//                    likeButton.setImageResource(R.drawable.ic_heart)
//                }
//            }
//            val actionButton=postLayout.findViewById<Button>(R.id.actionButton)
//            val commentButton=postLayout.findViewById<Button>(R.id.commentButton)
//            actionButton.setOnClickListener {
//                val participate = Intent(this, Participate::class.java)
//                startActivity(participate)
//            }
//            commentButton.setOnClickListener {
//                val comment = Intent(this, Comment::class.java)
//                startActivity(comment)
//            }
//
//            container.addView(postLayout)
//        }
        newPost.setOnClickListener{
            val postCreation= Intent(this,PostCreation::class.java).apply {

            }
            startActivity(postCreation)
        }
    }
    private fun getCityName(location: Location) {
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val cityName = addresses[0].locality
                    Log.d("City", "City Name: $cityName")
                    saveCityName(cityName)
                    Toast.makeText(this, "You are in $cityName ", Toast.LENGTH_SHORT).show()
                    val city = findViewById<TextView>(R.id.cityName)
                    city.text = cityName
                }
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Error getting city name", e)
        }
    }

    private fun saveCityName(cityName: String) {
        val editor = sharedPreferences.edit()
        editor.putString("cityName", cityName)
        editor.apply() // Save the changes
    }

    private fun fetchPosts() {
        token.let { RetrofitClient.instance.fetchPosts(it) }
            .enqueue(object : Callback<List<PostResponse>> {
                override fun onResponse(call: Call<List<PostResponse>>, response: Response<List<PostResponse>>) {
                    if (response.isSuccessful) {
                        posts = response.body()
                        populatePostsUI()
                    } else {
                        Toast.makeText(this@Homepage, "Error fetching posts", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<PostResponse>>, t: Throwable) {
                    Log.e("Network Error", "Failed to fetch posts", t)
                    Toast.makeText(this@Homepage, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun populatePostsUI() {
        val container: LinearLayout = findViewById(R.id.main)
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
            val visitPost:LinearLayout=postLayout.findViewById(R.id.visitPost)
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
                comment.putExtra("post_id", post._id)
                startActivity(comment)
            }

            container.addView(postLayout)
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, you can proceed with location operations
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Request permissions
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                            LOCATION_PERMISSION_REQUEST_CODE
                        )

                        recreate()
                    }

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            // Got last known location. In some rare situations this can be null.
                            location?.let {
                                Log.d("Location", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
                            }
                        }
                } else {
                    // Permission was denied, you cannot proceed with location operations
                    Log.d("Location", "Location permission denied")
                }
                return
            }
            else -> {
                // Ignore all other requests
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options,menu)
        return true
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profileButton -> {
                val profileView= Intent(this,ProfileView::class.java).apply {

                }
                startActivity(profileView)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}