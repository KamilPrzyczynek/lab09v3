package com.example.lab09v3.ui.home

import SwipeToDeleteCallback
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.lab09v3.R
import com.example.lab09v3.ui.MessageAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        messageAdapter = MessageAdapter(viewModel.apiService, getUserLogin())
        recyclerView.adapter = messageAdapter

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val message = messageAdapter.currentList[position]
                viewModel.deleteMessage(message.id)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages)
        }

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchMessages()
            swipeRefreshLayout.isRefreshing = false
        }

        viewModel.fetchMessages()

        val sendButton: AppCompatImageButton = view.findViewById(R.id.imageButton)
        val messageEditText: EditText = view.findViewById(R.id.SendMesage)

        sendButton.setOnClickListener {
            val messageContent = messageEditText.text.toString()

            if (messageContent.isNotEmpty()) {
                val userLogin = getUserLogin()

                if (userLogin != null) {
                    lifecycleScope.launch {
                        try {
                            val response: Response<ResponseBody> = viewModel.apiService.sendMessage(
                                Message(content = messageContent, login = userLogin)
                            )

                            val errorBody = response.errorBody()?.string() ?: "Empty error body"
                            Log.d("NetworkResponse", "Error response body: $errorBody")

                            if (response.isSuccessful) {

                            } else {
                                Log.e("NetworkError",
                                    "Error: ${response.code()} - ${response.message()}")
                            }
                        } catch (e: Exception) {
                            Log.e("NetworkError", "Exception: ${e.message}", e)
                        }
                    }
                    messageEditText.text.clear()
                } else {
                    Log.e("UserLoginError", "Cannot access user login")
                }
            } else {
                Log.e("InputError", "Message content is empty")
            }
        }

        lifecycleScope.launch {
            while (true) {
                delay(60000)
                viewModel.fetchMessages()
            }
        }

        return view
    }

    private fun getUserLogin(): String? {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userLogin", null)
    }
}
