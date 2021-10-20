package ru.netologia.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netologia.Auth.AppAuth
import ru.netologia.R
import ru.netologia.application.NMediaApplication
import ru.netologia.dto.ContentPush
import kotlin.random.Random

const val TAG_APP = "FCMService"

class FCMService : FirebaseMessagingService(){
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()
    private val action = "action"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {

        try {
        val content = gson.fromJson(message.data[content], ContentPush::class.java)
        val authStateId = NMediaApplication.appAuth.authStateFlow.value.id
        when (content.recipientId) {
            authStateId, null -> handlerForAll(content.content)
            else -> AppAuth.getInstance().sendPushToken()
        }
    } catch (e: Exception) {
        Log.e(
            TAG_APP,
            getString(
                R.string.notification_error,
                message.data[action]
            )
        )
    }
}

    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
    }
    private fun handlerForAll(content: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.posts_avatars_drawable)
            .setContentTitle(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

}