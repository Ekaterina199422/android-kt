package ru.netologia.api

import okhttp3.MultipartBody
import retrofit2.http.*
import ru.netologia.auth.AuthState
import ru.netologia.dto.Media
import ru.netologia.dto.Post
import ru.netologia.dto.PushToken


interface ApiService {

    @POST("users/push-tokens")
    suspend fun push(@Body pushToken: PushToken): Unit

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

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Media

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(@Field("login") login: String, @Field("pass") pass: String): AuthState

}

