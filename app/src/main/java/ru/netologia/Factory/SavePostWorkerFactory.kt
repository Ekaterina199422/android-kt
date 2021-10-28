package ru.netologia.Factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.netologia.repository.IPostRepository
import ru.netologia.work.SavePostWorker
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SavePostWorkerFactory @Inject constructor(
private val repository: IPostRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        SavePostWorker::class.java.name -> SavePostWorker(
            appContext,
            workerParameters,
            repository
        )
        else -> null
    }
}