package ru.netologia.Factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.netologia.repository.IPostRepository
import ru.netologia.work.RefreshPostsWorker

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshPostsWorkerFactory@Inject constructor (
    private val repository: IPostRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        RefreshPostsWorker::class.java.name -> RefreshPostsWorker(
            appContext,
            workerParameters,
            repository
        )
        else -> null
    }
}