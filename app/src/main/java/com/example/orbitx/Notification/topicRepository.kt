package com.example.orbitx.Notification

import com.google.firebase.messaging.FirebaseMessaging

class topicRepository {

     fun subscribe(topic: String){
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

     fun unsubscribe(topic: String){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }
}