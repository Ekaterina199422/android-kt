package ru.netologia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
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
    fun getAll(): Call<List<Post>>
    @DELETE("posts/{id}/likes")
    fun unLikeById(@Path("id") id: Long): Call<Post>
    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Long): Call<Post>
    @DELETE("posts/{id}")
    fun removePost(@Path("id") id: Long): Call<Unit>
    @POST("posts")
    fun savePost(@Body post: Post): Call<Post>
}
object PostsApi {
    val retrofitService: PostApiService by lazy(retrofit::create)
}