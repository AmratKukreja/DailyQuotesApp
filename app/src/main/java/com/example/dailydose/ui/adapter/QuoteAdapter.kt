package com.example.dailydose.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.data.local.QuoteEntity
import com.example.dailydose.databinding.ItemQuoteBinding

class QuoteAdapter(
    private val onFavoriteClick: (QuoteEntity) -> Unit,
    private val onDeleteClick: (QuoteEntity) -> Unit
) : ListAdapter<QuoteEntity, QuoteAdapter.QuoteViewHolder>(QuoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val binding = ItemQuoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuoteViewHolder(private val binding: ItemQuoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(quote: QuoteEntity) {
            binding.apply {
                quoteText.text = "\"${quote.quoteText}\""
                authorText.text = root.context.getString(R.string.quote_by, quote.author)
                dateText.text = quote.dateFetched
                
                updateFavoriteButton(quote.isFavorite)
                
                favoriteButton.setOnClickListener {
                    onFavoriteClick(quote)
                }
                
                deleteButton.setOnClickListener {
                    onDeleteClick(quote)
                }
            }
        }
        
        private fun updateFavoriteButton(isFavorite: Boolean) {
            if (isFavorite) {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
            }
        }
    }

    class QuoteDiffCallback : DiffUtil.ItemCallback<QuoteEntity>() {
        override fun areItemsTheSame(oldItem: QuoteEntity, newItem: QuoteEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QuoteEntity, newItem: QuoteEntity): Boolean {
            return oldItem == newItem
        }
    }
} 