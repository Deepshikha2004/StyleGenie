package com.example.stylegenie

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

class ChatBotActivity : AppCompatActivity() {

    private lateinit var chatContainer: LinearLayout
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
        inputEditText = findViewById(R.id.inputEditText)
        sendButton = findViewById(R.id.sendButton)
        btnRestart = findViewById(R.id.btnRestart)
        chatScrollView = findViewById(R.id.chatScrollView)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnRecord = findViewById(R.id.btnRecord)

        // Send button click
        sendButton.setOnClickListener {
            val message = inputEditText.text.toString().trim()

            // 👇 Check if an image was selected
            if (selectedImageUri != null) {
                addImageMessage(selectedImageUri!!)
                selectedImageUri = null
                inputEditText.setText("") // clear after sending
                addMessage("Bot: Nice picture!", isUser = false)
                return@setOnClickListener
            }

            if (message.isNotEmpty()) {
                addMessage(message, isUser = true)
                inputEditText.text.clear()
                simulateBotTyping("Bot: I'm still learning to respond!")

            }
        }

        // Restart chat
        btnRestart.setOnClickListener {
            chatContainer.removeAllViews()
            selectedImageUri = null
            inputEditText.setText("")
            Toast.makeText(this, "Chat restarted", Toast.LENGTH_SHORT).show()
        }

        // Upload image
        btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // Record voice
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                    val uri = data?.data
                    if (uri != null) {
                        selectedImageUri = uri
                        inputEditText.setText("Image selected. Click send to send it.")
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

    private fun addImageMessage(imageUri: Uri) {
        val imageView = ImageView(this).apply {
            setImageURI(imageUri)
            layoutParams = LinearLayout.LayoutParams(500, 500).apply {
                topMargin = 8
                bottomMargin = 8
                marginStart = 80
                marginEnd = 16
                gravity = android.view.Gravity.END
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        chatContainer.addView(imageView)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        chatScrollView.post {
            chatScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
    private fun simulateBotTyping(response: String) {
        val typingTextView = TextView(this).apply {
            text = "Bot is typing..."
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setTextColor(ContextCompat.getColor(this@ChatBotActivity, android.R.color.darker_gray))
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 8
            bottomMargin = 8
            marginStart = 16
            marginEnd = 80
            gravity = android.view.Gravity.START
        }

        typingTextView.layoutParams = layoutParams
        chatContainer.addView(typingTextView)
        scrollToBottom()

        typingTextView.postDelayed({
            chatContainer.removeView(typingTextView)
            addMessage(response, isUser = false)
        }, 2000) // 1 second typing simulation
    }

}
