package com.example.tvshowreminder.data.database


import android.util.Log
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.AppExecutors
import com.example.tvshowreminder.util.MESSAGE_EMPTY_DATABASE
import com.example.tvshowreminder.util.MESSAGE_NO_SEARCH_MATCHES

class DatabaseDataSource(
    private val tvShowDatabase: TvShowDatabase,
    private val appExecutors: AppExecutors
) : DatabaseContract {


    override fun getPopularTvShowList(
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().getPopularTvShowsList().let { tvShowList ->
                appExecutors.main.execute {
                    if (tvShowList.isEmpty()) {
                        errorCallback(MESSAGE_EMPTY_DATABASE)
                    } else {
                        successCallback(tvShowList)
                    }
                }
            }
        }
    }

    override fun getLatestTvShowList(
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().getLatestTvShowsList().let { tvShowList ->
                appExecutors.main.execute {
                    if (tvShowList.isEmpty()) {
                        errorCallback(MESSAGE_EMPTY_DATABASE)
                    } else {
                        successCallback(tvShowList)
                    }
                }
            }
        }
    }

    override fun getFavouriteTvShowList(
        successCallback: (List<TvShowDetails>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().getFavouriteTvShowsList().let { tvShowList ->
                appExecutors.main.execute {
                    successCallback(tvShowList)
                }
            }
        }
    }

    override fun insertPopularTvShowList(
        tvShowList: List<TvShow>
    ) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().insertPopularTvShowList(tvShowList).let { result ->
                appExecutors.main.execute {
                    var error = false
                    result.forEach {
                        if (it == -1L) {
                            error = true
                        }
                    }
                    if (error) {
                        Log.d("mmm","DatabaseDataSource :  insertPopularTvShowList --  Database error")
                    }
                }
            }
        }
    }

    override fun insertLatestTvShowList(
        tvShowList: List<TvShow>
    ) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().insertLatestTvShowList(tvShowList).let { result ->
                appExecutors.main.execute {
                    var error = false
                    result.forEach {
                        if (it == -1L) {
                            error = true
                        }
                    }
                    if (error) {
                        Log.d("mmm","DatabaseDataSource :  insertLatestTvShowList --  Database error")
                    }
                }
            }
        }
    }

    override fun getTvShow(
        tvShowId: Int,
        successCallback: (TvShowDetails) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().getFavouriteTvShow(tvShowId).let { tvShow ->
                appExecutors.main.execute {
                    if (tvShow == null) {
                        errorCallback(MESSAGE_EMPTY_DATABASE)
                    } else {
                        successCallback(tvShow)
                    }

                }
            }
        }
    }

    override fun insertTvShow(tvShowDetails: TvShowDetails, successCallback: () -> Unit) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().insertTvShow(tvShowDetails).let {
                if (it != -1L) {
                    successCallback()
                }
            }
        }
    }

    override fun deleteTvShow(tvShowDetails: TvShowDetails, successCallback: () -> Unit) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().deleteTvShow(tvShowDetails).let {
                if (it == 1) {
                    successCallback()
                }
            }
        }
    }

    override fun getFavouriteSeasonDetails(
        tvShowId: Int,
        seasonNumber: Int,
        successCallback: (SeasonDetails) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        appExecutors.diskIO.execute {
            val episodes = tvShowDatabase.tvShowDao().getEpisodesGorSeason(tvShowId, seasonNumber)
            val seasonDetails =
                tvShowDatabase.tvShowDao().getFavouriteSeasonDetails(tvShowId, seasonNumber)
            seasonDetails.episodes = episodes
            appExecutors.main.execute {
                successCallback(seasonDetails)
            }
        }
    }

    override fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails) {
        appExecutors.diskIO.execute {

            val tvShowId = seasonDetails.episodes?.get(0)?.showId
            seasonDetails.showId = tvShowId

            tvShowDatabase.tvShowDao().insertFavouriteSeasonDetails(seasonDetails).let {
                if (it == -1L) {
                    Log.d("mmm", "DatabaseDataSource :  deleteTvShow --  Database error")
                }
            }
            seasonDetails.episodes?.let {
                tvShowDatabase.tvShowDao().insertEpisodes(it)
            }
        }
    }

    override fun deleteFavouriteSeasonDetails(tvShowId: Int) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().deleteFavouriteSeasonDetail(tvShowId).let {
                if (it != 1) {
                    Log.d("mmm", "DatabaseDataSource :  deleteTvShow --  Database error")
                }
            }
        }
    }

    override fun searchFavouriteTvShowsList(
        query: String,
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        appExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().searchTvShowsList(query).let { tvShowList ->
                appExecutors.main.execute {
                    if (tvShowList.isNullOrEmpty()) {
                        errorCallback(MESSAGE_NO_SEARCH_MATCHES)
                    } else {
                        successCallback(tvShowList)
                    }
                }
            }
        }
    }

    companion object {

        @Volatile
        private var instance: DatabaseDataSource? = null

        fun getInstance(
            tvShowDatabase: TvShowDatabase,
            appExecutors: AppExecutors
        ) = instance ?: synchronized(this) {
            instance ?: DatabaseDataSource(tvShowDatabase, appExecutors).also { instance = it }
        }

    }
}