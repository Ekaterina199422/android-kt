package ru.netologia.model

import android.content.res.Resources
import ru.netologia.R
import java.net.ConnectException

    sealed class ApiError {
        object UnknownException : ApiError()
        object ServerError : ApiError()
        object NetworkError : ApiError()
        companion object {
            fun fromThrowable(throwable: Throwable): ApiError =
                    when (throwable) {
                        is ApiException -> throwable.error
                        is ConnectException -> NetworkError
                        else -> UnknownException
                    }
        }
    }

    fun ApiError?.getCreateReadableMessageError(resources: Resources): String =
            when (this) {
                ApiError.UnknownException -> resources.getString(R.string.error_unknown)
                ApiError.ServerError -> resources.getString(R.string.error_server)
                ApiError.NetworkError, null -> resources.getString(R.string.error_network)
            }
