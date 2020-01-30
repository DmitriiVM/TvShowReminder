package com.example.tvshowreminder.data.database

import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface DatabaseContract {

    fun getPopularTvShowList(): Single<List<TvShow>>
    fun getLatestTvShowList(): Single<List<TvShow>>
    fun getFavouriteTvShowList(): Single<List<TvShowDetails>>

    fun insertPopularTvShowList(tvShowList: List<TvShow>)
    fun insertLatestTvShowList(tvShowList: List<TvShow>)

    fun getTvShow(tvShowId: Int): Flowable<TvShowDetails>
    fun insertTvShow(tvShowDetails: TvShowDetails) : Completable
    fun deleteTvShow(tvShowDetails: TvShowDetails) : Completable

    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int): Flowable<SeasonDetails>
    fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails)
    fun deleteFavouriteSeasonDetails(tvShowId: Int)

    fun searchFavouriteTvShowsList(query: String): Flowable<List<TvShow>>
}