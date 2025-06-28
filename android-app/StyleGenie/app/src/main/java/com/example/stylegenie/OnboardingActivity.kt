package com.example.stylegenie

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding)

        // ⏳ Splash delay (2 seconds)
        Handler(Looper.getMainLooper()).postDelayed({

            val nextScreen = if (FirebaseAuth.getInstance().currentUser != null) {
                HomeActivity::class.java
            } else {
                LoginActivity::class.java
            }

            startActivity(Intent(this, nextScreen))
            finish()

        }, 2000) // 2000 milliseconds = 2 seconds
    }
}
