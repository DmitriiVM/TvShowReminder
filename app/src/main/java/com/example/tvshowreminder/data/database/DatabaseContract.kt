package com.example.tvshowreminder.data.database

import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails

interface DatabaseContract {

    fun getPopularTvShowList(successCallback: (List<TvShow>) -> Unit, errorCallback: (String) -> Unit)
    fun getLatestTvShowList(successCallback: (List<TvShow>) -> Unit, errorCallback: (String) -> Unit)
    fun getFavouriteTvShowList(successCallback: (List<TvShowDetails>) -> Unit, errorCallback: (String) -> Unit)

    fun insertPopularTvShowList(tvShowList: List<TvShow>)
    fun insertLatestTvShowList(tvShowList: List<TvShow>)

    fun getTvShow(tvShowId: Int, successCallback: (TvShowDetails) -> Unit, errorCallback: (String) -> Unit)
    fun insertTvShow(tvShowDetails: TvShowDetails, successCallback: () -> Unit)
    fun deleteTvShow(tvShowDetails: TvShowDetails, successCallback: () -> Unit)

    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int, successCallback: (SeasonDetails) -> Unit, errorCallback: (String) -> Unit)
    fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails)
    fun deleteFavouriteSeasonDetails(tvShowId: Int)

    fun searchFavouriteTvShowsList(query: String, successCallback: (List<TvShow>) -> Unit, errorCallback: (String) -> Unit)

}