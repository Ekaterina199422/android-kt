package ru.netologia.application

import android.app.Application
import ru.netologia.Auth.AppAuth
import ru.netologia.db.AppDb
import ru.netologia.repository.IPostRepository
import ru.netologia.repository.PostRepositoryImpl

class NMediaApplication : Application() {
    companion object {
        lateinit var repository: IPostRepository
        lateinit var appAuth: AppAuth
    }

    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
        repository = PostRepositoryImpl(AppDb.getInstance(applicationContext).postDao())
        appAuth = AppAuth.getInstance()
    }
}
