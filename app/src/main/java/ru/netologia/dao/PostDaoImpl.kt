package ru.netologia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netologia.dto.Post
import java.text.SimpleDateFormat
import java.util.*

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {

    companion object {

        val DDL = """
        CREATE TABLE ${PostColumns.TABLE} (
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
            ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
            ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
            ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_SHARE} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_VIEW} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_CHAT} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_VIDEO} TEXT NOT NULL  
        );
        """.trimIndent()
    }

    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_SHARE = "share"
        const val COLUMN_VIEW = "views"
        const val COLUMN_CHAT = "chat"
        const val COLUMN_VIDEO = "videoUrl"
        const val COLUMN_LIKED_BY_ME = "likedByMe"
        const val COLUMN_LIKES = "likes"


        val ALL_COLUMNS = arrayOf(
                COLUMN_ID,
                COLUMN_AUTHOR,
                COLUMN_CONTENT,
                COLUMN_PUBLISHED,
                COLUMN_SHARE,
                COLUMN_VIEW,
                COLUMN_VIDEO,
                COLUMN_CHAT,
                COLUMN_LIKED_BY_ME,
                COLUMN_LIKES
        )
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
                PostColumns.TABLE,
                PostColumns.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy в HH:mm", Locale.ENGLISH)
        val currentDate = dateFormat.format(Date())
        val values = ContentValues().apply {
            if (post.id != 0L) {
                put(PostColumns.COLUMN_ID, post.id)
            }
            put(PostColumns.COLUMN_AUTHOR, "Этот пост создан мной")
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHED, currentDate)
            put(PostColumns.COLUMN_VIDEO, post.videoUrl)
        }
        val id = db.replace(PostColumns.TABLE, null, values)
        db.query(
                PostColumns.TABLE,
                PostColumns.ALL_COLUMNS,
                "${PostColumns.COLUMN_ID} = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
        ).use {
            it.moveToNext()
            return map(it)
        }

    }

    override fun likeById(id: Long) {
        db.execSQL(
                """
           UPDATE posts SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
    }

    override fun removeById(id: Long) {
        db.delete(
                PostColumns.TABLE,
                "${PostColumns.COLUMN_ID} = ?",
                arrayOf(id.toString())
        )
    }

    override fun share(id: Long) {
        db.execSQL(
                """
           UPDATE posts SET
            share = share + 1 WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
    }

    private fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                    id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                    author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                    content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                    published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                    likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
                    likes = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
                    share = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARE)),
                    views = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_VIEW)),
                    videoUrl = getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO)),
                    chat = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_CHAT))
            )
        }
    }
}