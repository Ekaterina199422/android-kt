package ru.netologia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netologia.dao.PostDao
import ru.netologia.dao.PostWorkDao
import ru.netologia.entity.Converters
import ru.netologia.entity.PostEntity
import ru.netologia.entity.PostStateConverter
import ru.netologia.entity.PostWorkEntity

@Database(entities = [PostEntity::class, PostWorkEntity::class], version = 1, exportSchema = false)
@TypeConverters(value = [PostStateConverter::class, Converters::class])
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postWorkDao(): PostWorkDao

}

