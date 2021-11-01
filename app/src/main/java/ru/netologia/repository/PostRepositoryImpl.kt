package ru.netologia.repository

import android.net.Uri
import androidx.core.net.toFile
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netologia.api.ApiService
import ru.netologia.auth.AppAuth
import ru.netologia.auth.AuthState
import ru.netologia.dao.PostDao
import ru.netologia.dao.PostRemoteKeyDao
import ru.netologia.dao.PostWorkDao
import ru.netologia.db.AppDb
import ru.netologia.dto.Media
import ru.netologia.dto.MediaUpload
import ru.netologia.dto.Post
import ru.netologia.entity.AttachmentEmbeddable
import ru.netologia.entity.PostEntity
import ru.netologia.entity.PostWorkEntity
import ru.netologia.enumeration.AttachmentType
import ru.netologia.enumeration.PostState
import javax.inject.Inject


class PostRepositoryImpl @Inject constructor(
    appDb: AppDb,
    private val dao: PostDao,
    private val postWorkDao: PostWorkDao,
    private val apiService: ApiService,
    private val auth: AppAuth,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    ) : IPostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 25),
        remoteMediator = PostRemoteMediator(apiService, appDb, dao, postRemoteKeyDao),
        pagingSourceFactory = dao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            val new = apiService.getNewer(id)
            emit(new.size) // тот кто подписался на данные оновления будет получать количестов постов

        }
    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)

    override fun getNewList(id: Long): Flow<List<Post>> = flow {
        val posts = apiService.getNewer(id)
        emit(posts)

    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll(): List<Post> {
        val netPosts =apiService.getAll()
        dao.insert(netPosts.map(PostEntity.Companion::fromDto))
        return netPosts
    }

    override suspend fun unLikeById(id: Long) {
        apiService.unLikeById(id)
        dao.likeById(id)

    }

    override suspend fun likeById(id: Long) {
        apiService.likeById(id)
        dao.likeById(id)
    }

    override suspend fun removePost(id: Long) {
        apiService.removePost(id)
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
        return apiService.upload(media)
    }

    override suspend fun updateUser(login: String, pass: String): AuthState {
        return apiService.updateUser(login, pass)

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
                                authorId = auth.authStateFlow.value.id,
                                state = PostState.Progress
                            )
                    )
                }
                else -> {
                    val media = upload(MediaUpload(Uri.parse(workEntity.uri).toFile()))
                    PostEntity.fromWorkDto(
                        workEntity
                            .copy(
                                authorId = auth.authStateFlow.value.id,
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

    override suspend fun sendPost(post: Post): Post = apiService.savePost(post)
}