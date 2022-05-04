package com.example.myweatherapp.base

import com.example.myweatherapp.util.Resource
import retrofit2.Response
import javax.inject.Inject

open class BaseServiceImpl @Inject constructor() {

    open suspend fun <T> getResponse(request: suspend () -> Response<T>): Resource<T> {
        return try {
            val result = request.invoke()

            if (result.isSuccessful) {
                result.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("Bir hata oluştu!", null)
            } else {
                return Resource.error("Bir hata oluştu!", null)
            }
        } catch (e: Throwable) {
            return Resource.error(CoreNetworkError(e).appErrorMessage ?: "", null)
        }
    }

}