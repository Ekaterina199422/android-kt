package ru.netologia.application

import android.app.Application
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netologia.Auth.AppAuth
import ru.netologia.db.AppDb
import ru.netologia.repository.IPostRepository
import ru.netologia.repository.PostRepositoryImpl
import ru.netologia.work.RefreshPostsWorker
import java.util.concurrent.TimeUnit

class NMediaApplication : Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)
    companion object {
        lateinit var repository: IPostRepository
        lateinit var appAuth: AppAuth
    }

    override fun onCreate() {
        super.onCreate()
        setupAuth()
        setupWork()
        repository = PostRepositoryImpl(AppDb.getInstance(applicationContext).postDao(),
        AppDb.getInstance(applicationContext).postWorkDao())
        appAuth = AppAuth.getInstance()
    }
    private fun setupWork() {
        appScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<RefreshPostsWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(this@NMediaApplication).enqueueUniquePeriodicWork(
                RefreshPostsWorker.name,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    private fun setupAuth() {
        appScope.launch {
            AppAuth.initApp(this@NMediaApplication)
        }
    }
}
