package ru.netologia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netologia.Auth.AuthState
import ru.netologia.api.Api
import ru.netologia.dao.PostDao
import ru.netologia.dto.*
import ru.netologia.enumeration.AttachmentType


class PostRepositoryImpl(private val dao: PostDao) : IPostRepository {
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
        val new = Api.Service.getNewer(id)
        emit(new.size) // тот кто подписался на данные оновления будет получать количестов постов


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

        override suspend fun unLikeById(id: Long){
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

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        val media = upload(upload)
        val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
        savePost(PostEntity.fromDto(postWithAttachment))
        sendPost(postWithAttachment)
    }
    override suspend fun savePost(post: PostEntity): Long = dao.insert(post)

    override suspend fun updateUser(login: String, pass: String): AuthState {
        return Api.Service.updateUser(login, pass)
    }
    override suspend fun sendPost(post: Post): Post = Api.Service.savePost(post)

    override suspend fun upload(upload: MediaUpload): Media {
        val media = MultipartBody.Part.createFormData(
                "file",
                upload.file.name,
                upload.file.asRequestBody()
        )
        return Api.Service.upload(media)
    }

}
