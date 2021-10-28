package ru.netologia.module

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netologia.Factory.DependencyWorkerFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WorkManagerModule {

    @Provides
    @Singleton
    fun providesWork(
        @ApplicationContext context: Context,
        workerFactory: DependencyWorkerFactory
    ): WorkManager {
        WorkManager.initialize(
            context, Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )
        return WorkManager.getInstance(context)
    }
}


