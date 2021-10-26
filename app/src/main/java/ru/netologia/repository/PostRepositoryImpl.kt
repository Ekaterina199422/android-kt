package ru.netologia.repository

import android.net.Uri
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netologia.Auth.AuthState
import ru.netologia.api.Api
import ru.netologia.application.NMediaApplication
import ru.netologia.dao.PostDao
import ru.netologia.dao.PostWorkDao
import ru.netologia.dto.Media
import ru.netologia.dto.MediaUpload
import ru.netologia.dto.Post
import ru.netologia.entity.AttachmentEmbeddable
import ru.netologia.entity.PostEntity
import ru.netologia.entity.PostWorkEntity
import ru.netologia.enumeration.AttachmentType
import ru.netologia.enumeration.PostState


class PostRepositoryImpl(
    private val dao: PostDao,
    private val postWorkDao: PostWorkDao
    ) : IPostRepository {
    override val posts: Flow<List<Post>>
        get() = dao.getAll().map { // мы получаем все данные с нашей базы данных
            it.sortedWith(Comparator { o1, o2 ->
                when {
                    o1.id == 0L && o2.id == 0L -> o1.localId.compareTo(o2.localId)
                    o1.id == 0L -> -1
                    o2.id == 0L -> 1
                    else -> -o1.id.compareTo(o2.id)
                }
            })
                .map(PostEntity::toDto)
        }
            .flowOn(Dispatchers.Default) // Действия будут происходи в Default потоке

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            val new = Api.Service.getNewer(id)
            emit(new.size) // тот кто подписался на данные оновления будет получать количестов постов

        }
    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)

    override fun getNewList(id: Long): Flow<List<Post>> = flow {
        val posts = Api.Service.getNewer(id)
        emit(posts)

    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll(): List<Post> {
        val netPosts = Api.Service.getAll()
        dao.insert(netPosts.map(PostEntity.Companion::fromDto))
        return netPosts
    }

    override suspend fun unLikeById(id: Long) {
        Api.Service.unLikeById(id)
        dao.likeById(id)

    }

    override suspend fun likeById(id: Long) {
        Api.Service.likeById(id)
        dao.likeById(id)
    }

    override suspend fun removePost(id: Long) {
        Api.Service.removePost(id)
        dao.removeById(id)
    }

    override suspend fun sendNewPost(posts: List<Post>) =
        dao.insert(posts.map(PostEntity.Companion::fromDto))


    override suspend fun upload(upload: MediaUpload): Media {
        val media = MultipartBody.Part.createFormData(
            "file",
            upload.file.name,
            upload.file.asRequestBody()
        )
        return Api.Service.upload(media)
    }

    override suspend fun updateUser(login: String, pass: String): AuthState {
        return Api.Service.updateUser(login, pass)

    }
    override suspend fun saveWork(post: Post, upload: MediaUpload): Long {
        val entity = PostWorkEntity.fromDto(post).apply {
            if (upload != null) {
                this.uri = upload.file.toURI().toString()
            }
        }
        return postWorkDao.insert(entity)
    }

    override suspend fun prepareWork(post: Post): Long =
        postWorkDao.insert(PostWorkEntity.fromDto(post))

    override suspend fun processWork(id: Long) {
        var localId: Long
        try {
            val workEntity = postWorkDao.getById(id)
            val postEntity = when (workEntity.uri) {
                null -> {
                    PostEntity.fromWorkDto(
                        workEntity
                            .copy(
                                authorId = NMediaApplication.appAuth.authStateFlow.value.id,
                                state = PostState.Progress
                            )
                    )
                }
                else -> {
                    val media = upload(MediaUpload(Uri.parse(workEntity.uri).toFile()))
                    PostEntity.fromWorkDto(
                        workEntity
                            .copy(
                                authorId = NMediaApplication.appAuth.authStateFlow.value.id,
                                state = PostState.Progress,
                                attachment = AttachmentEmbeddable(media.id, AttachmentType.IMAGE)
                            )
                    )
                }
            }
            if (postEntity.id == 0L) {
                postEntity.let { entity ->
                    localId = savePost(entity)
                    entity.copy(localId = localId, id = localId)
                }
            } else {
                localId = postEntity.id
            }
            val networkPost = sendPost(postEntity.toDto())
            savePost(
                postEntity.copy(
                    id = networkPost.id,
                    localId = localId,
                    state = PostState.Success
                )
            )
            postWorkDao.removeById(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun savePost(post: PostEntity): Long = dao.insert(post)

    override suspend fun sendPost(post: Post): Post = Api.Service.savePost(post)
}