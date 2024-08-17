data class Notification(
    val title: String,
    val body: String
)

data class Data(
    val title2: String,
    val body2: String,
    val userUid: String,
    val receiverUid: String,
    val route: String
)

data class Message(
    val topic: String,
//    val notification: Notification,
    val data: Data,

)

data class FCMRequest(
    val message: Message
)
