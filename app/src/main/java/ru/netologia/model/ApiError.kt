package ru.netologia.model

import java.net.ConnectException
import java.sql.SQLException

sealed class ApiError(var code: String) : RuntimeException() {
    companion object {
        fun fromThrowable(e: Throwable): ApiError =
            when (e) {
                is ApiError -> e
                is SQLException -> DbError
                is ConnectException -> NetworkError
                else -> UnknownError
            }
    }
}

class AppError(val status: Int, code: String) : ApiError(code)
object NetworkError : ApiError("error_network")
object DbError : ApiError("error_db")
object UnknownError : ApiError("error_unknown")