package com.example.lab09v3.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab09v3.AktualizacjaWiadomosci
import com.example.lab09v3.R
import com.example.lab09v3.Wiadomosc
import com.example.lab09v3.ui.MessageAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessageViewHolder(
    itemView: View,
    private val apiService: ApiService,
    private val currentUser: String?,
    private val adapter: MessageAdapter
) : RecyclerView.ViewHolder(itemView) {
    private val textViewUser: TextView = itemView.findViewById(R.id.textViewUser)
    private val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
    private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
    private val editDeleteLayout: LinearLayout = itemView.findViewById(R.id.editDeleteLayout)
    private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
    private val saveButton: ImageButton = itemView.findViewById(R.id.saveButton)
    private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    private val commentEditText: EditText = itemView.findViewById(R.id.commentEditText)

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

        if (currentUser == message.login) {
            editDeleteLayout.visibility = View.VISIBLE

            editButton.visibility = View.VISIBLE
            saveButton.visibility = View.GONE
            commentEditText.visibility = View.GONE

            editButton.setOnClickListener {
                editButton.visibility = View.GONE
                saveButton.visibility = View.VISIBLE
                commentEditText.visibility = View.VISIBLE
                commentEditText.setText(message.content)
                Log.d("MessageViewHolder", "Message ID: ${message.id}")

            }
            saveButton.setOnClickListener {
                val editedContent = commentEditText.text.toString()
                val messageId = message.id

                Log.d("SaveButton", "Message ID: $messageId")
                Log.d("SaveButton", "Edited Content: $editedContent")

                if (messageId != null && messageId.isNotEmpty()) {
                    val aktualizacjaWiadomosci = AktualizacjaWiadomosci(editedContent, currentUser ?: "")

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d("HTTP_REQUEST", "Updating message with ID: $messageId")
                            val response = apiService.aktualizujWiadomosc(messageId, aktualizacjaWiadomosci)
                            Log.e("Wyslanawiaodmosc", "$aktualizacjaWiadomosci")
                            if (response.isSuccessful) {
                                message.content = editedContent
                                adapter.notifyItemChanged(adapterPosition)
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e("SaveButton", "Invalid Message ID or Bad Request")
                                Log.e("HTTP_RESPONSE", "Failed to update the message on the server: $errorBody")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    Log.e("SaveButton", "Invalid Message ID")
                }
            }

            deleteButton.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        Log.d("DeleteButton", "Deleting message with ID: ${message.id}")

                        val messageId = message.id

                        if (messageId != null && messageId.isNotBlank()) {
                            Log.d("DeleteButton", "Deleting message with ID: $messageId")
                            val response = apiService.usunWiadomosc(messageId)

                            if (response.isSuccessful) {
                                val updatedList = adapter.currentList.toMutableList()
                                updatedList.remove(message)

                                adapter.submitList(updatedList)

                                Log.d("DeleteButton", "Wiadomosc usunieta")
                            } else {
                                Log.e("DeleteButton", "Nie usunieta wiadomosc")
                            }
                        } else {
                            Log.e("DeleteButton", "Invalid or empty Message ID")
                        }
                    } catch (e: Exception) {
                        Log.e("DeleteButton", "Exception during deletion: ${e.message}", e)
                    }
                }
            }




        } else {
            editDeleteLayout.visibility = View.GONE
        }
    }
}
