package com.example.tvshowreminder.data.network

import android.util.Log
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.ERROR_MESSAGE_NETWORK_PROBLEM_2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NetworkDaraSource: NetworkContract {



    override fun getPopularTvShowList(
        language: String,
        page: String,
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        MovieDbApiService.tvShowService().getPopularTvShowList(language = language, page = page).enqueue(object : Callback<TvShowsList>{
                override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
                    errorCallback(t.message ?: ERROR_MESSAGE_NETWORK_PROBLEM_2)
                }
                override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
                    if (response.isSuccessful){
                        response.body()?.showsList?.let {
                            successCallback(it)
                        }
                    }else {
                    errorCallback(response.message())
                    }
                }
            })
    }

    override fun getLatestTvShowList(
        currentDate: String,
        language: String,
        page: String,
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        MovieDbApiService.tvShowService().getLatestTvShowList(currentDate = currentDate, language = language, page = page).enqueue(object : Callback<TvShowsList>{
            override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
                errorCallback(t.message ?: ERROR_MESSAGE_NETWORK_PROBLEM_2)
            }
            override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
                if (response.isSuccessful){
                    response.body()?.showsList?.let {
                        successCallback(it)
                    }
                }else {
                    errorCallback(response.message())
                }
            }

        })
    }

    override fun searchTvShow(
        query: String,
        language: String,
        page: String,
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        MovieDbApiService.tvShowService().searchTvShow(query = query, language = language, page = page).enqueue(object : Callback<TvShowsList>{
            override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
                errorCallback(t.message ?: ERROR_MESSAGE_NETWORK_PROBLEM_2)
            }

            override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
                if (response.isSuccessful) {
                    response.body()?.showsList?.let { tvShowList ->
                        successCallback(tvShowList)
                    }
                } else {
                    errorCallback(response.message())
                }
            }
        })
    }


    override fun getTvShowDetails(
        tvId: Int,
        successCallback: (TvShowDetails) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        MovieDbApiService.tvShowService().getTvShowDetails(tvId).enqueue(object : Callback<TvShowDetails>{
            override fun onFailure(call: Call<TvShowDetails>, t: Throwable) {
                errorCallback(t.message ?: ERROR_MESSAGE_NETWORK_PROBLEM_2)
            }
            override fun onResponse(call: Call<TvShowDetails>, response: Response<TvShowDetails>) {
                if (response.isSuccessful){
                    response.body()?.let {
                        successCallback(it)
                    }
                } else {
                    errorCallback(response.message())
                }
            }
        })
    }

    override fun getSeasonDetails(
        tvId: Int,
        seasonNumber: Int,
        successCallback: (SeasonDetails) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        MovieDbApiService.tvShowService().getSeasonDetails(tvId, seasonNumber).enqueue(object : Callback<SeasonDetails>{
            override fun onFailure(call: Call<SeasonDetails>, t: Throwable) {
                errorCallback(t.message ?: ERROR_MESSAGE_NETWORK_PROBLEM_2)
            }
            override fun onResponse(call: Call<SeasonDetails>, response: Response<SeasonDetails>) {
                if (response.isSuccessful){
                    response.body()?.let {
                        successCallback(it)
                    }
                } else {
                    errorCallback(response.message())
                }
            }
        })
    }
}


