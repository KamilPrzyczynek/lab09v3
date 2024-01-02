package com.example.lab09v3.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab09v3.Wiadomosc
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<Wiadomosc>>()
    val messages: LiveData<List<Wiadomosc>> get() = _messages

    val apiService: ApiService = ApiService.create()

    private val _currentMessage = MutableLiveData<String>()

    fun updateCurrentMessage(message: String) {
        _currentMessage.value = message
    }

    fun fetchMessages() {
        viewModelScope.launch {
            try {
                val response: Response<List<Wiadomosc>> =
                    apiService.pobierzWiadomosci(ostatnie = 10)

                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            val messagesList = mutableListOf<Wiadomosc>()

                            for (i in responseBody.indices) {
                                val message = responseBody[i]
                                val login = message.login
                                val date = message.date
                                val content = message.content

                                messagesList.add(Wiadomosc(content, login, date, ""))
                            }

                            _messages.value = messagesList
                        } else {
                            Log.e("NetworkError", "Response body is null")
                        }
                    } catch (e: Exception) {
                        Log.e("NetworkError", "Error parsing JSON", e)
                    }
                } else {
                    Log.e("NetworkError", "Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Exception: ${e.message}", e)
            }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            try {
                val response = apiService.sendMessage(message)

                val contentType = response.headers()["Content-Type"]
                Log.d("NetworkResponse", "Content Type: $contentType")

                if ("application/json" == contentType) {
                    if (response.isSuccessful) {
                        if (response.body()?.contentLength() != 0L) {
                            val jsonResponse = response.body()?.string()
                            Log.d("NetworkResponse", "JSON Response: $jsonResponse")
                        } else {
                            Log.d("NetworkResponse", "Empty response body")
                        }
                    } else {
                        Log.e("NetworkError", "Error: ${response.code()} - ${response.message()}")
                    }
                } else if ("text/html" == contentType) {
                    val htmlResponse = response.body()?.string()
                    Log.d("NetworkResponse", "HTML Response: $htmlResponse")
                } else {
                    Log.e("NetworkError", "Unexpected Content-Type: $contentType")
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Exception: ${e.message}", e)
            }
        }
    }



}

