package com.example.tvshowreminder.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object MovieDbApiService {


        private fun getOkHttpClient() = OkHttpClient
            .Builder()
            .addInterceptor(ApiKeyInterceptor())
            .build()

        private fun getRetrofit()= Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun tvShowService(): MovieDbApi = getRetrofit().create(MovieDbApi::class.java)


}