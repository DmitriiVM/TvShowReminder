package com.example.tvshowreminder.data.network

import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails

interface NetworkContract {


    fun getPopularTvShowList(language: String, page: String, successCallback: (List<TvShow>) -> Unit, errorCallback: (String) -> Unit)

    fun getLatestTvShowList(currentDate: String, language: String, page: String, successCallback: (List<TvShow>) -> Unit, errorCallback: (String) -> Unit)

    fun searchTvShow(query: String, language: String, page: String, successCallback: (List<TvShow>) -> Unit, errorCallback: (String) -> Unit)

    fun getTvShowDetails(tvId: Int, successCallback: (TvShowDetails) -> Unit, errorCallback: (String) -> Unit)
    fun getSeasonDetails(tvId: Int, seasonNumber: Int, successCallback: (SeasonDetails) -> Unit, errorCallback: (String) -> Unit)


}