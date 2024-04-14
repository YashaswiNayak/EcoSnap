package com.example.ecosnap

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PostCreation:AppCompatActivity() {
    private lateinit var imageView: ImageView

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.post_creation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.postCreationLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.imageView)

        val btnPost: Button = findViewById(R.id.btnPost)
        val btnChoose: Button = findViewById(R.id.btnChoose)
        val btnCapture: Button = findViewById(R.id.btnCapture)

        btnCapture.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, PIC_ID)
        }

        btnPost.setOnClickListener {
            val returnToMain = Intent(this, Homepage::class.java)
            startActivity(returnToMain)
        }

        btnChoose.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_ID && resultCode == RESULT_OK) {
            val photo = data?.extras?.get("data") as Bitmap?
            imageView.setImageBitmap(photo)
        }
    }

    companion object {
        private const val PIC_ID = 123
    }
}