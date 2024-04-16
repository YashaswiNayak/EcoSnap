package com.example.ecosnap
import android.app.ProgressDialog
import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import io.grpc.Context.Storage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

private var capturedImage: Bitmap? = null
class PostCreation:AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private var token: String = ""
    private lateinit var title: EditText
    private lateinit var content: EditText

    var fileUri: Uri? = null
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Convert the URI to a Bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            // Set the Bitmap to capturedImage
            capturedImage = bitmap
            // Display the selected image in the ImageView
            imageView.setImageBitmap(bitmap)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
        enableEdgeToEdge()
        setContentView(R.layout.post_creation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.postCreationLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = findViewById(R.id.etTitle)
        content = findViewById(R.id.etDescription)
        imageView = findViewById(R.id.imageView)

        sharedPreferences = getSharedPreferences("EcoSnap", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("user_token", "") ?: ""

        val btnPost: Button = findViewById(R.id.btnPost)
        val btnChoose: Button = findViewById(R.id.btnChoose)
        val btnCapture: Button = findViewById(R.id.btnCapture)

        btnCapture.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, PIC_ID)
        }

        btnPost.setOnClickListener{
            if (fileUri!=null){
                uploadImage()
                val returnToMain = Intent(this, Homepage::class.java)
                startActivity(returnToMain)
            }else{
                Toast.makeText(this, "Please select image to upload", Toast.LENGTH_SHORT).show()
            }
        }



        btnChoose.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose image to upload"),0)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data!=null && data.data!=null){
            fileUri = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                imageView.setImageBitmap(bitmap)
            } catch(e: Exception){
                Log.e("Exception", "Error: $e")
            }
        }else if (requestCode == PIC_ID && resultCode == RESULT_OK && data != null) {
            // For images captured by the camera
            val photo: Bitmap = data.extras?.get("data") as Bitmap
            // Save the captured image to a temporary file
            val tempUri: Uri? = saveImageToTempFile(photo)
            tempUri?.let {
                fileUri = tempUri
                imageView.setImageBitmap(photo)
            }
        }
    }
    private fun saveImageToTempFile(bitmap: Bitmap): Uri? {
        return try {
            val file = File.createTempFile(
                "tempImage",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun uploadImage() {
        if (fileUri != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading Image...")
            progressDialog.setMessage("Processing...")
            progressDialog.show()

            val ref: StorageReference = FirebaseStorage.getInstance().getReference().child(UUID.randomUUID().toString())
            ref.putFile(fileUri!!).continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                // Continue with the task to get the download URL
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val post = Post(title.text.toString(), content.text.toString(), downloadUri.toString())
                    RetrofitClient.instance.createPost(token, post).enqueue(object :
                            Callback<PostResponse> {
                            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                                if (response.isSuccessful) {
                                    // Handle the response
                                    val postResponse = response.body()
                                    Log.d("Response", "Post created successfully: ${postResponse?.title}")
                                } else {
                                    // Handle error
                                    Log.e("Error", "Failed to create post")
                                }
                            }

                            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                                // Handle failure
                                Log.e("Error", "Error creating post", t)
                            }
                        })
                    progressDialog.dismiss()
                    Toast.makeText(this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show()

                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "File Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    companion object {
        private const val PIC_ID = 123
    }






}