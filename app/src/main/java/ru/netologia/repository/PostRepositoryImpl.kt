package ru.netology.nmedia.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netologia.dto.Post
import ru.netologia.repository.IPostRepository
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : IPostRepository {
    private val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    private val typeToken = object : TypeToken<List<Post>>() {}
    private val gson = Gson()

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: IPostRepository.GetAllCallback) {
        val request: Request = Request.Builder()
                .url("${BASE_URL}/api/posts")
                .build()
        client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.use {
                            try {
                                callback.onSuccess(gson.fromJson(it.string(), typeToken.type))
                            } catch (e: Exception) {
                                callback.onError(e)
                            }
                        }
                    }
                })
    }

    override fun unLikeById(id: Long, callback: IPostRepository.LikeByIdCallback) {
        val requestQ: Request =
                Request.Builder()
                        .delete()
                        .url("${BASE_URL}/api/posts/$id/likes")
                        .build()
        client.newCall(requestQ)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.use {
                            try {
                                callback.onSuccess(gson.fromJson(it.string(), Post::class.java))
                            } catch (e: IOException) {
                                callback.onError(e)
                            }
                        }
                    }
                })
    }

    override fun likeById(post: Post, callback: IPostRepository.LikeByIdCallback) {
        val requestLike: Request =
                Request.Builder()
                        .post(gson.toJson(post).toRequestBody(jsonType))
                        .url("${BASE_URL}/api/posts/${post.id}/likes")
                        .build()

        client.newCall(requestLike)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.use {
                            try {
                                callback.onSuccess(gson.fromJson(it.string(), Post::class.java))
                            } catch (e: IOException) {
                                callback.onError(e)
                            }
                        }
                    }
                })
    }

    override fun removePost(id: Long, callback: IPostRepository.RemovePostCallback) {
        val request: Request = Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/$id")
                .build()

        client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        callback.onSuccess()
                    }
                })
    }

    override fun savePost(post: Post, callback: IPostRepository.SavePostCallback) {
        val request: Request = Request.Builder()
                .post(gson.toJson(post).toRequestBody(jsonType))
                .url("${BASE_URL}/api/posts")
                .build()

        client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.use {
                            try {
                                callback.onSuccess(gson.fromJson(it.string(), Post::class.java))
                            } catch (e: IOException) {
                                callback.onError(e)
                            }
                        }
                    }
                })
    }

}