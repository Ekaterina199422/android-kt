package ru.netologia.api

import okhttp3.Interceptor
import okhttp3.Response
import ru.netologia.model.ApiError
import ru.netologia.model.ApiException
import java.net.HttpURLConnection

class PostsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
            chain.proceed(chain.request())
                    .let { response ->
                        when {
                            response.isSuccessful -> response
                            response.code == HttpURLConnection.HTTP_INTERNAL_ERROR -> throw ApiException(
                                    ApiError.ServerError
                            )
                            else -> throw ApiException(ApiError.UnknownException)

                        }

                    }
}