package com.example.dailydose.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.dailydose.R
import com.example.dailydose.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupAnimations()
        navigateToMain()
    }
    
    private fun setupAnimations() {
        // Fade in animation for the app name
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeIn.duration = 1000
        
        // Scale animation for the logo
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        
        binding.appNameText.startAnimation(fadeIn)
        binding.subtitleText.startAnimation(fadeIn)
        binding.logoContainer.startAnimation(scaleAnimation)
    }
    
    private fun navigateToMain() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2500) // 2.5 seconds
    }
} 