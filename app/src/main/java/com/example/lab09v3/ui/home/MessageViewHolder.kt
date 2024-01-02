package com.example.lab09v3.ui.home

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab09v3.R
import com.example.lab09v3.Wiadomosc
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textViewUser: TextView = itemView.findViewById(R.id.textViewUser)
    private val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
    private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)

    @SuppressLint("SimpleDateFormat")
    fun bind(message: Wiadomosc) {
        textViewUser.text = "${message.login}"
        textViewContent.text = message.content

        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val parsedDate = dateFormat.parse(message.date)

            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate = outputDateFormat.format(parsedDate)

            textViewDate.text = formattedDate
        } catch (e: ParseException) {
            textViewDate.text = message.date
        }
    }

}
