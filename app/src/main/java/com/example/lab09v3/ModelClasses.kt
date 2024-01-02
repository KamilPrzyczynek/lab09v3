package com.example.lab09v3

data class Wiadomosc(
    val content: String,
    val login: String,
    val date: String,
    val id: String
)

data class AktualizacjaWiadomosci(
    val content: String,
    val login: String
)

data class OdpowiedzApi(
    val messages: Any,
    val isSuccessful: Boolean,
    val error: String? = null,
    val httpCode: Int? = null,
    val unexpectedResponse: String? = null,
    val unexpectedThrowable: Throwable? = null
) {
    fun code(): Int {
        return httpCode ?: 0
    }

    fun message(): String {
        return error ?: ""
    }

    fun errorBody(): String {
        return error ?: ""
    }

    fun isResponseString(): Boolean {
        return messages.isEmpty() && error != null
    }


    fun getStringResponse(): String {
        return if (messages is String) {
            messages
        } else {
            error ?: "Unknown error"
        }
    }

    companion object {
        fun create(messages: List<Wiadomosc>, isSuccessful: Boolean): OdpowiedzApi {
            return OdpowiedzApi(messages, isSuccessful)
        }

        fun createForError(error: String, httpCode: Int): OdpowiedzApi {
            return OdpowiedzApi(emptyList<Wiadomosc>(), false, error, httpCode)
        }

        fun createForUnexpectedResponse(response: String, throwable: Throwable?, isSuccessful: Boolean): OdpowiedzApi {
            return OdpowiedzApi(
                emptyList<Wiadomosc>(),
                isSuccessful,
                unexpectedResponse = response,
                unexpectedThrowable = throwable
            )
        }
    }
}

@JvmName("isEmpty")
private fun <T> T?.isEmpty(): Boolean {
    return this == null
}


