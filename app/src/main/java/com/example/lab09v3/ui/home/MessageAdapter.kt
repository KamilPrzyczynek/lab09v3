package com.example.lab09v3.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lab09v3.R
import com.example.lab09v3.Wiadomosc
import com.example.lab09v3.ui.home.ApiService
import com.example.lab09v3.ui.home.MessageViewHolder

// MessageAdapter
class MessageAdapter(
    private val apiService: ApiService,
    private val currentUser: String?
) : ListAdapter<Wiadomosc, MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view, apiService, currentUser, this)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }


    private class MessageDiffCallback : DiffUtil.ItemCallback<Wiadomosc>() {
        override fun areItemsTheSame(oldItem: Wiadomosc, newItem: Wiadomosc): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Wiadomosc, newItem: Wiadomosc): Boolean {
            return oldItem == newItem
        }
    }
}
