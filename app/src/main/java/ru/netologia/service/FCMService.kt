package ru.netologia.service

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
import ru.netologia.R
import ru.netologia.dto.AddPost
import ru.netologia.dto.Like
import kotlin.random.Random

const val TAG_APP = "FCMService"

class FCMService : FirebaseMessagingService() {

    private val action = "action"
    private val channelId = "remote"
    private val content = "content"
    private val gson = Gson()

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
            message.data[action]?.let {
                when (Action.valueOf(it)) {
                    Action.LIKE -> handleLike(
                            gson.fromJson(
                                    message.data[content],
                                    Like::class.java
                            )
                    )
                    Action.ADD -> addNewPost(
                            gson.fromJson(
                                    message.data[content],
                                    AddPost::class.java
                            )
                    )
                }
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
        println(token)
    }

    private fun addNewPost(content: AddPost) {
        val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.posts_avatars_drawable)
                .setContentTitle(
                        getString(
                                R.string.notification_user_add,
                                content.postAuthor
                        )
                )
                .setContentText(content.content)
                .setStyle(
                        NotificationCompat.BigTextStyle()
                                .bigText(content.content)
                )
                .build()
        NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.posts_avatars_drawable)
                .setContentTitle(
                        getString(
                                R.string.notification_user_liked,
                                content.userName,
                                content.postAuthor
                        )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
    }
}

enum class Action {
    LIKE,
    ADD,
}