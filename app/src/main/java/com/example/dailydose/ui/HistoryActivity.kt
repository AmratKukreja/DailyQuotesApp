package com.example.dailydose.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailydose.DailyDoseApplication
import com.example.dailydose.R
import com.example.dailydose.databinding.ActivityHistoryBinding
import com.example.dailydose.ui.adapter.QuoteAdapter
import com.example.dailydose.viewmodel.HistoryViewModel
import com.example.dailydose.viewmodel.HistoryViewModelFactory

class HistoryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var quoteAdapter: QuoteAdapter
    
    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as DailyDoseApplication).repository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.history)
            setDisplayHomeAsUpEnabled(true)
        }
    }
    
    private fun setupRecyclerView() {
        quoteAdapter = QuoteAdapter(
            onFavoriteClick = { quote ->
                viewModel.toggleFavoriteStatus(quote)
            },
            onDeleteClick = { quote ->
                viewModel.deleteQuote(quote)
            }
        )
        
        binding.recyclerView.apply {
            adapter = quoteAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }
    
    private fun setupObservers() {
        viewModel.showFavoritesOnly.observe(this) { showFavoritesOnly ->
            updateFilterButton(showFavoritesOnly)
            observeQuotes(showFavoritesOnly)
        }
    }
    
    private fun observeQuotes(showFavoritesOnly: Boolean) {
        // Remove previous observers
        viewModel.allQuotes.removeObservers(this)
        viewModel.favoriteQuotes.removeObservers(this)
        
        if (showFavoritesOnly) {
            viewModel.favoriteQuotes.observe(this) { quotes ->
                quoteAdapter.submitList(quotes)
                updateEmptyState(quotes.isEmpty(), true)
            }
        } else {
            viewModel.allQuotes.observe(this) { quotes ->
                quoteAdapter.submitList(quotes)
                updateEmptyState(quotes.isEmpty(), false)
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.filterButton.setOnClickListener {
            viewModel.toggleFavoriteFilter()
        }
    }
    
    private fun updateFilterButton(showFavoritesOnly: Boolean) {
        if (showFavoritesOnly) {
            binding.filterButton.text = getString(R.string.all_quotes)
            binding.filterButton.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_list, 0, 0, 0
            )
        } else {
            binding.filterButton.text = getString(R.string.favorites)
            binding.filterButton.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_favorite_filled, 0, 0, 0
            )
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean, isFavoritesFilter: Boolean) {
        if (isEmpty) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.emptyStateText.text = if (isFavoritesFilter) {
                getString(R.string.no_favorites)
            } else {
                getString(R.string.no_quotes_available)
            }
        } else {
            binding.emptyStateText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 