package com.example.tvshowreminder.util

import com.example.tvshowreminder.data.pojo.general.TvShow

sealed class Resource<T> {


    companion object {
        fun <T> create() : Resource<T>{
            return Loading()
        }

        fun <T> createNoResult() : Resource<T>{
            return EmptyList()
        }

        fun <T> createError(message: String) : Resource<T>{
            return Error(message)
        }

        fun <T> create(data: T) : Resource<T>{
            return Success(data)
        }

        fun <T> create(data: T, networkErrorMessage: String) : Resource<T>{
            return SuccessWithMessage(data, networkErrorMessage)
        }
    }

    class Loading<T>() : Resource<T>()
    class EmptyList<T>() : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class SuccessWithMessage<T>(val data: T, val networkErrorMessage: String) : Resource<T>()
}


//
//companion object {
//
//    fun create() : Resource{
//        return Loading()
//    }
//
//    fun createNoResult() : Resource{
//        return EmptyList()
//    }
//
//    fun create(message: String) : Resource{
//        return Error(message)
//    }
//
//    fun create(list: List<TvShow>) : Resource{
//        return Success(list)
//    }
//
//    fun create(list: List<TvShow>, networkErrorMessage: String) : Resource{
//        return SuccessWithMessage(list, networkErrorMessage)
//    }
//
//
//}
//
//class Loading() : Resource()
//class EmptyList() : Resource()
//data class Error(val message: String) : Resource()
//data class Success(val list: List<TvShow>) : Resource()
//data class SuccessWithMessage(val list: List<TvShow>, val networkErrorMessage: String) : Resource()