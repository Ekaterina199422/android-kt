package ru.netologia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netologia.api.ApiService
import ru.netologia.dto.Post
import ru.netologia.model.AppError


class PostPagingSource (
    private val service: ApiService,
    ) : PagingSource<Long, Post>() {

        override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
            //  на вход подаются парметры на выход состояние загрузки
            try {
                val response = when (params) { // определяем что хочет сделать пользователь
                    is LoadParams.Refresh -> service.getLatest(params.loadSize)
                    is LoadParams.Prepend -> return LoadResult.Page(
                        data = emptyList(),
                        prevKey = params.key,
                        nextKey = null
                    )
                    is LoadParams.Append -> service.getBefore(params.key, params.loadSize) //  назад(перем посты на основе последнего id)
                }

                if (!response.isSuccessful) {
                    throw AppError(
                        response.code(),
                        response.message()
                    )
                }
                val body = response.body() ?: throw AppError(
                    response.code(),
                    response.message(),
                )
                val nextKey = if (body.isEmpty()) null else body.last().id
                return LoadResult.Page(
                    data = body,
                    prevKey = params.key,
                    nextKey = nextKey,
                )
            } catch (e: Exception) {
                return LoadResult.Error(e)
            }
        }
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? {
        return null
    }
}