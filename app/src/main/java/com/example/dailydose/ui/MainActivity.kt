package com.example.dailydose.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dailydose.DailyDoseApplication
import com.example.dailydose.R
import com.example.dailydose.databinding.ActivityMainBinding
import com.example.dailydose.viewmodel.MainViewModel
import com.example.dailydose.viewmodel.MainViewModelFactory
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as DailyDoseApplication).repository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }
    
    private fun setupObservers() {
        viewModel.currentQuote.observe(this) { quote ->
            quote?.let {
                animateQuoteChange {
                    binding.quoteText.text = "\"${it.quoteText}\""
                    binding.authorText.text = getString(R.string.quote_by, it.author)
                    updateFavoriteButton(it.isFavorite)
                    binding.quoteCard.visibility = View.VISIBLE
                }
            }
        }
        
        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.refreshButton.isEnabled = !isLoading
            binding.randomButton.isEnabled = !isLoading
        }
        
        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.refreshButton.setOnClickListener {
            viewModel.refreshQuote()
        }
        
        binding.randomButton.setOnClickListener {
            viewModel.showRandomQuote()
        }
        
        binding.favoriteButton.setOnClickListener {
            viewModel.currentQuote.value?.let { quote ->
                viewModel.toggleFavorite(quote)
                val message = if (quote.isFavorite) {
                    "Removed from favorites"
                } else {
                    "Added to favorites ❤️"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.fabHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
    
    private fun updateFavoriteButton(isFavorite: Boolean) {
        val favoriteButton = binding.favoriteButton as MaterialButton
        if (isFavorite) {
            favoriteButton.text = getString(R.string.remove_from_favorites)
            favoriteButton.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_filled)
            favoriteButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.favorite_active)
        } else {
            favoriteButton.text = getString(R.string.mark_as_favorite)
            favoriteButton.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_border)
            favoriteButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.accent)
        }
    }
    

    
    private fun animateQuoteChange(updateContent: () -> Unit) {
        if (binding.quoteCard.visibility == View.VISIBLE) {
            // Fade out current quote
            binding.quoteCard.startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_out).apply {
                    setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                        override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                        override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                        override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                            updateContent()
                            // Fade in new quote
                            binding.quoteCard.startAnimation(
                                android.view.animation.AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_in)
                            )
                        }
                    })
                }
            )
        } else {
            updateContent()
            binding.quoteCard.startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in)
            )
        }
    }
} 