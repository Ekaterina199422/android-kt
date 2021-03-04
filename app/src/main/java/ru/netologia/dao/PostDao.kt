package ru.netologia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netologia.dto.*
import java.text.SimpleDateFormat
import java.util.*

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    fun updateContentById(id: Long, content: String)


    fun save(post: PostEntity) =
        if (post.id == 0L) {
            val dateFormat = SimpleDateFormat("dd MMMM yyyy в HH:mm", Locale.ENGLISH)
            val currentDate = dateFormat.format(Date())
            insert(
                post.copy(
                    author = "Этот пост создан мной",
                    published = currentDate
                )
            )
        } else {
            updateContentById(post.id, post.content)
        }

    @Query("""UPDATE PostEntity SET 
                    likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END, 
                  likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                  WHERE id = :id""")
    fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)

    @Query("""UPDATE PostEntity SET
            share = share + 1 WHERE id = :id""")
    fun share(id: Long)

}