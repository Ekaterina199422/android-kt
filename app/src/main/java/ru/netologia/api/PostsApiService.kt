package ru.netologia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netologia.BuildConfig
import ru.netologia.dto.Post
import java.util.concurrent.TimeUnit


private val logging = HttpLoggingInterceptor()
        .apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(PostsInterceptor())
        .addInterceptor(logging)
        .build()

private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
interface PostApiService {
    @GET("posts")
    suspend fun getAll(): List<Post>
    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long):List<Post> // получение новывх постов
    @DELETE("posts/{id}/likes")
    suspend fun unLikeById(@Path("id") id: Long): Post
    @POST("posts/{id}/likes")
    suspend  fun likeById(@Path("id") id: Long): Post
    @DELETE("posts/{id}")
    suspend fun removePost(@Path("id") id: Long): Unit
    @POST("posts")
    suspend fun savePost(@Body post: Post): Post
}
object PostsApi {
    val Service: PostApiService by lazy(retrofit::create)
}