package com.example.orbitx.Notification

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FCMService {
    @POST("v1/projects/orbitx-32de1/messages:send")
    suspend fun sendNotification(
        @Body requestBody: RequestBody,
        @Header("Authorization") authToken: String
    ): Response<ResponseBody>
}
