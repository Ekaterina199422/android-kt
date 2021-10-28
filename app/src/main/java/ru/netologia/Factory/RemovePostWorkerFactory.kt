package ru.netologia.Factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.netologia.repository.IPostRepository
import ru.netologia.work.RemovePostWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemovePostWorkerFactory  @Inject constructor (
    private val repository: IPostRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        RemovePostWorker::class.java.name -> RemovePostWorker(
            appContext,
            workerParameters,
            repository
        )
        else -> null
    }
}