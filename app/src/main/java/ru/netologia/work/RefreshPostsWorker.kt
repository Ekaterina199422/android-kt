package ru.netologia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netologia.repository.IPostRepository

class RefreshPostsWorker (
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: IPostRepository
) : CoroutineWorker(applicationContext, params) {
        companion object {
            const val name = "ru.netology.nmedia.work.RefreshPostsWorker"
        }

        override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
            try {
                repository.getAll()
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }

