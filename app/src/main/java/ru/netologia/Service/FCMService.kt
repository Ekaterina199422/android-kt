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
import dagger.hilt.android.AndroidEntryPoint
import ru.netologia.R
import ru.netologia.auth.AppAuth
import ru.netologia.dto.ContentPush
import javax.inject.Inject
import kotlin.random.Random

const val TAG_APP = "FCMService"

@AndroidEntryPoint
class FCMService : FirebaseMessagingService(){
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()
    private val action = "action"

    @Inject
    lateinit var auth: AppAuth

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
            val authStateId = auth.authStateFlow.value.id
            when (content.recipientId) {
                authStateId, null -> handlerForAll(content.content)
                else -> auth.sendPushToken()
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
        auth.sendPushToken(token)
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