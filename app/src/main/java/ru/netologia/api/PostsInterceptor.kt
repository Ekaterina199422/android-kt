package ru.netologia.api

import okhttp3.Interceptor
import okhttp3.Response
import ru.netologia.model.AppError

class PostsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request())
            .let { response ->
                when {
                    response.isSuccessful -> response
                    else -> throw AppError(response.code, response.message)
                }
            }
}