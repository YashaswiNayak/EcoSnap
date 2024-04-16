package com.example.ecosnap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp

open class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homepage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharedPreferences = getSharedPreferences("login_test_app", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("user_token", "")

        val entryTextView = findViewById<TextView>(R.id.entry)
        entryTextView.visibility = TextView.VISIBLE
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 2000
        fadeIn.fillAfter = true

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 2000
        fadeOut.fillAfter = true

        val handler = Handler(Looper.getMainLooper())
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                handler.postDelayed({
                    entryTextView.startAnimation(fadeOut)
                }, 1000) // Adjust delay between fade in and fade out
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Start the next activity here
                if (token == "") {
                    val intent = Intent(this@MainActivity, Login::class.java)
                    startActivity(intent)
                    finish() // Optional, depending on whether you want to finish this activity
                } else {
                    val intent = Intent(this@MainActivity, Homepage::class.java)
                    startActivity(intent)
                    finish() // Optional, depending on whether you want to finish this activity
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        entryTextView.startAnimation(fadeIn)


    }
}