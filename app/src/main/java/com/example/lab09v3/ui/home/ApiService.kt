package com.example.lab09v3.ui.home

import com.example.lab09v3.AktualizacjaWiadomosci
import com.example.lab09v3.OdpowiedzApi
import com.example.lab09v3.Wiadomosc
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type


interface ApiService {
    @GET("shoutbox/messages")
    suspend fun pobierzWiadomosci(@Query("last") ostatnie: Int): Response<List<Wiadomosc>>

    @POST("shoutbox/message")
    suspend fun sendMessage(@Body message: Message): Response<ResponseBody>

    @PUT("shoutbox/message/{id}")
    suspend fun aktualizujWiadomosc(@Path("id") id: String, @Body zapytanie: AktualizacjaWiadomosci): Response<OdpowiedzApi>

    @DELETE("shoutbox/message/{id}")
    suspend fun usunWiadomosc(@Path("id") id: String): Response<OdpowiedzApi>

    companion object {
        fun utworz(): ApiService {
            val gson = GsonBuilder().setLenient().create()

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://tgryl.pl/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(NullOnEmptyConverterFactory())
                .client(okHttpClient)
                .build()


            return retrofit.create(ApiService::class.java)
        }

        fun create(): ApiService = utworz()
    }
}

class NullOnEmptyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val delegate = retrofit.nextResponseBodyConverter<Any?>(this, type, annotations)
        return Converter<ResponseBody, Any?> { body ->
            if (body.contentLength() == 0L) null else delegate.convert(body)
        }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val delegate = retrofit.nextRequestBodyConverter<Any?>(this, type, parameterAnnotations, methodAnnotations)
        return Converter<Any?, RequestBody> { value -> delegate.convert(value) }
    }
}
data class Message(val content: String,val login: String)

