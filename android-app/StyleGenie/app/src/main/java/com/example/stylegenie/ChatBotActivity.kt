package com.example.stylegenie

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.stylegenie.network.RetrofitInstance
import com.example.stylegenie.network.BotResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class ChatBotActivity : AppCompatActivity() {

    private lateinit var chatContainer: LinearLayout
    private lateinit var imagePreview: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var btnRestart: Button
    private lateinit var chatScrollView: ScrollView
    private lateinit var btnUploadImage: ImageButton
    private lateinit var btnRecord: ImageButton

    private var selectedImageUri: Uri? = null

    private val IMAGE_PICK_CODE = 1001
    private val SPEECH_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        // Bind views
        chatContainer = findViewById(R.id.chatContainer)
        imagePreview = findViewById(R.id.imagePreview)
        inputEditText = findViewById(R.id.inputEditText)
        sendButton = findViewById(R.id.sendButton)
        btnRestart = findViewById(R.id.btnRestart)
        chatScrollView = findViewById(R.id.chatScrollView)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnRecord = findViewById(R.id.btnRecord)

        sendButton.setOnClickListener {
            val message = inputEditText.text.toString().trim()
            val imageTag = inputEditText.tag

            if (imageTag is Uri) {
                val imageFile = File(getRealPathFromUri(imageTag))
                uploadImage(imageFile)
                inputEditText.text.clear()
                imagePreview.setImageDrawable(null)
                imagePreview.visibility = View.GONE
            } else if (message.isNotEmpty()) {
                addMessage(message, isUser = true)
                inputEditText.text.clear()
                sendTextToBot(message)
            }
        }

        btnRestart.setOnClickListener {
            chatContainer.removeAllViews()
            selectedImageUri = null
            inputEditText.setText("")
            Toast.makeText(this, "Chat restarted", Toast.LENGTH_SHORT).show()
        }

        btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        btnRecord.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }

            try {
                startActivityForResult(intent, SPEECH_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
            }
        }

        imagePreview.setOnClickListener {
            inputEditText.tag = null
            imagePreview.setImageDrawable(null)
            imagePreview.visibility = View.GONE
            Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                    val uri = data?.data
                    if (uri != null) {
                        selectedImageUri = uri
                        inputEditText.tag = uri
                        imagePreview.setImageURI(uri)
                        imagePreview.visibility = View.VISIBLE
                    }
                }

                SPEECH_REQUEST_CODE -> {
                    val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val spokenText = result?.get(0) ?: ""
                    inputEditText.setText(spokenText)
                }
            }
        }
    }

    private fun sendTextToBot(query: String) {
        val body = mapOf("query" to query)
        RetrofitInstance.api.getFashionByText(body).enqueue(object : Callback<BotResponse> {
            override fun onResponse(call: Call<BotResponse>, response: Response<BotResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        addMessage(it.bot_response, isUser = false)
                        it.images.forEach { image ->
                            addBotImage(image.image_url)
                        }
                    }
                } else {
                    addMessage("Error: ${response.code()}", isUser = false)
                }
            }

            override fun onFailure(call: Call<BotResponse>, t: Throwable) {
                addMessage("Failure: ${t.message}", isUser = false)
            }
        })
    }

    private fun uploadImage(file: File) {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitInstance.api.getFashionByImage(multipart).enqueue(object : Callback<BotResponse> {
            override fun onResponse(call: Call<BotResponse>, response: Response<BotResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        addMessage(it.bot_response, isUser = false)
                        it.images.forEach { image ->
                            addBotImage(image.image_url)
                        }
                    }
                } else {
                    addMessage("Upload error: ${response.code()}", isUser = false)
                }
            }

            override fun onFailure(call: Call<BotResponse>, t: Throwable) {
                addMessage("Upload failed: ${t.message}", isUser = false)
            }
        })
    }

    private fun addMessage(message: String, isUser: Boolean) {
        val textView = TextView(this).apply {
            text = message
            textSize = 16f
            setPadding(16, 12, 16, 12)
            background = ContextCompat.getDrawable(
                this@ChatBotActivity,
                if (isUser) R.drawable.bg_user_message else R.drawable.bg_bot_message
            )
            setTextColor(ContextCompat.getColor(this@ChatBotActivity, android.R.color.black))
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 8
            bottomMargin = 8
            marginStart = if (isUser) 80 else 16
            marginEnd = if (isUser) 16 else 80
            gravity = if (isUser) android.view.Gravity.END else android.view.Gravity.START
        }

        textView.layoutParams = layoutParams
        chatContainer.addView(textView)
        scrollToBottom()
    }

    private fun addBotImage(imageUrl: String) {
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(500, 500).apply {
                topMargin = 8
                bottomMargin = 8
                marginStart = 16
                marginEnd = 80
                gravity = android.view.Gravity.START
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        Glide.with(this).load(imageUrl).into(imageView)
        chatContainer.addView(imageView)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        chatScrollView.post {
            chatScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun getRealPathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath ?: ""
    }
}
