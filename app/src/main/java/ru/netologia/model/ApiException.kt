package ru.netologia.model

import java.io.IOException

class ApiException(
    val error: ApiError,
    throwable: Throwable? = null
) : IOException(throwable) {



}