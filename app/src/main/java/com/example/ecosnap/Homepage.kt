package com.example.ecosnap

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Homepage:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_app)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val topToolbar = findViewById<Toolbar>(R.id.topToolbar)
        val newPost=findViewById<ImageButton>(R.id.newPost)
        val container: LinearLayout =findViewById(R.id.main)
        setSupportActionBar(topToolbar)

        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false) // Hide default title
            setDisplayHomeAsUpEnabled(false)
        }

        repeat(3){
            val postLayout=layoutInflater.inflate(R.layout.post,null)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.width = 800 // Change as needed
            layoutParams.bottomMargin=20
            layoutParams.topMargin=20
            postLayout.setPadding(20, 20, 20, 20)
            layoutParams.gravity= Gravity.CENTER
            postLayout.layoutParams = layoutParams
            val likeButton: ImageButton = postLayout.findViewById(R.id.likeButton)
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
                startActivity(participate)
            }
            commentButton.setOnClickListener {
                val comment = Intent(this, Comment::class.java)
                startActivity(comment)
            }

            container.addView(postLayout)
        }
        newPost.setOnClickListener{
            val postCreation= Intent(this,PostCreation::class.java).apply {

            }
            startActivity(postCreation)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options,menu)
        return true
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