package ru.netologia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netologia.dao.PostDao
import ru.netologia.dao.PostRemoteKeyDao
import ru.netologia.dao.PostWorkDao
import ru.netologia.entity.PostEntity
import ru.netologia.entity.PostRemoteKeyEntity
import ru.netologia.entity.PostWorkEntity

@Database(
    entities = [PostEntity::class, PostWorkEntity::class, PostRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postWorkDao(): PostWorkDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}

