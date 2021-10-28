package ru.netologia.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netologia.dao.PostDao
import ru.netologia.dao.PostWorkDao
import ru.netologia.db.AppDb


@InstallIn(SingletonComponent::class)
@Module

object DaoModule {

    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun providePostWorkDao(db: AppDb): PostWorkDao = db.postWorkDao()

}