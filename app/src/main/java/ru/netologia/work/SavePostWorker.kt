package ru.netologia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.netologia.application.NMediaApplication

class SavePostWorker (
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {

        companion object {
            const val postKey = "post"
            const val name = "ru.netology.nmedia.work.SavePostWorker"
        }

        override suspend fun doWork(): Result {
            val id = inputData.getLong(postKey, 0L)
            if (id == 0L) {
                return Result.failure()
            }
            return try {
                NMediaApplication.repository.processWork(id)
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }

    }
