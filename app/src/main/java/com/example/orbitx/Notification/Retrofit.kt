package com.example.orbitx.Notification

import Data
import FCMRequest
import Message
import Notification
import android.content.Context

import com.google.auth.oauth2.GoogleCredentials
//import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: FCMService by lazy {
        retrofit.create(FCMService::class.java)
    }
}

fun sendTopicNotification2(context: Context, topic: String, title: String, message: String, userUid: String, receiverUid: String,route: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val notification = Notification(title, message)

            val data = Data(title,message,userUid,receiverUid,route)

            val fcmMessage = FCMRequest(Message(topic, data))

            val json = Gson().toJson(fcmMessage)

            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = RequestBody.create(mediaType, json)

            val response = RetrofitInstance.api.sendNotification(
                requestBody,
                "Bearer ${getAccessToken(context)}"
            )

            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    println("Notification sent successfully: ${response.body()?.string()}")
                }
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            e.printStackTrace()
            val errorBody = e.response()?.errorBody()?.string()
            println("HTTP Exception: ${e.code()}, Error Body: $errorBody")
        } catch (e: IOException) {
            e.printStackTrace()
            println("IO Exception: ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Exception: ${e.message}")
        }
    }
}


fun getAccessToken(context: Context): String {
    val inputStream: InputStream = context.assets.open("orbitx-32de1-firebase-adminsdk-ld0ls-884fca11c0.json")
    val credentials = GoogleCredentials
        .fromStream(inputStream)
        .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
    credentials.refreshIfExpired()
    return credentials.accessToken.tokenValue
}
