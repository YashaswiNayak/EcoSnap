package com.example.ecosnap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Participate:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.participate)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.participate)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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