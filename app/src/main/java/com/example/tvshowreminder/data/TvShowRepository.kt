package com.example.tvshowreminder.data

import android.util.Log
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.NetworkContract
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.ERROR_MESSAGE_NETWORK_PROBLEM_1
import com.example.tvshowreminder.util.ERROR_MESSAGE_NETWORK_PROBLEM_2

class TvShowRepository private constructor(
    private val network: NetworkContract,
    private val database: DatabaseContract
) {


    private var _cachedTvShowList = mutableListOf<TvShow>()
    val cachedTvShowList: List<TvShow>
        get() = _cachedTvShowList

    var cachedFavouriteTvShowList = listOf<TvShowDetails>()


    fun getPopularTvShowList(
        language: String,
        page: String,
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        network.getPopularTvShowList(
            language = language,
            page = page,
            successCallback = { tvShowList ->
                successCallback(tvShowList)
                cacheTvShowList(page, tvShowList)
                database.insertPopularTvShowList(tvShowList)
            },
            errorCallback = {
                errorCallback(ERROR_MESSAGE_NETWORK_PROBLEM_1)
                database.getPopularTvShowList({ tvShowList ->
                    successCallback(tvShowList)
                    cacheTvShowList(page, tvShowList)
                }, { errorMessage ->
                    errorCallback(errorMessage)
                })
            })
    }

    fun getLatestTvShowList(
        currentDate: String,
        language: String,
        page: String,
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        network.getLatestTvShowList(currentDate = currentDate,
            language = language,
            page = page,
            successCallback = { tvShowList ->
                successCallback(tvShowList)
                cacheTvShowList(page, tvShowList)
                database.insertLatestTvShowList(tvShowList)
            },
            errorCallback = {
                errorCallback(ERROR_MESSAGE_NETWORK_PROBLEM_1)
                database.getLatestTvShowList({ tvShowList ->
                    successCallback(tvShowList)
                    cacheTvShowList(page, tvShowList)
                }, { errorMessage ->
                    errorCallback(errorMessage)
                })
            })
    }

    fun getFavouriteTvShowList(
        successCallback: (List<TvShowDetails>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        database.getFavouriteTvShowList({ tvShowList ->
            successCallback(tvShowList)
            cachedFavouriteTvShowList = tvShowList
        }, { errorMessage ->
            errorCallback(errorMessage)
        })
    }


    fun searchTvShowsList(
        query: String,
        language: String,
        page: String,
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        network.searchTvShow(
            query = query,
            language = language,
            page = page,
            successCallback = { tvShowList ->
                successCallback(tvShowList)
                cacheTvShowList(page, tvShowList)
            },
            errorCallback = {
                errorCallback(ERROR_MESSAGE_NETWORK_PROBLEM_2)
            })
    }

    fun searchTvShowsListInFavourite(
        query: String,
        successCallback: (List<TvShow>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        database.searchFavouriteTvShowsList(query, { tvShowList ->
            successCallback(tvShowList)
        }, { errorMessage ->
            errorCallback(errorMessage)
        })
    }


    fun getTvShowDetails(
        tvId: Int,
        successCallback: (TvShowDetails) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        network.getTvShowDetails(tvId, { tvShowDetails ->
            successCallback(tvShowDetails)
        }, { errorMessage ->
            errorCallback(errorMessage)
            database.getTvShow(tvId, { tvShow ->
                successCallback(tvShow)
            }, {
                errorCallback(it)
            })
        })
    }

    fun getSeasonDetails(
        tvId: Int,
        seasonNumber: Int,
        successCallback: (SeasonDetails) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        network.getSeasonDetails(tvId, seasonNumber, { seasonDetails ->
            successCallback(seasonDetails)
        }, { errorMessage ->
            database.getFavouriteSeasonDetails(
                tvId,
                seasonNumber,
                successCallback = { seasonDetails ->
                    successCallback(seasonDetails)
                },
                errorCallback = {
                    errorCallback(it)
                })
            errorCallback(errorMessage)
        })
    }

    fun insertTvShow(
        tvShowDetails: TvShowDetails,
        successCallback: () -> Unit
    ) {
        database.insertTvShow(tvShowDetails){
            successCallback()
        }
        tvShowDetails.numberOfSeasons?.let {
            for (i in 1..it) {
                network.getSeasonDetails(tvShowDetails.id, i, successCallback = { seasonDetails ->
                    database.insertFavouriteSeasonDetails(seasonDetails)
                }, errorCallback = {})
            }
        }
    }

    fun deleteTvShow(
        tvShowDetails: TvShowDetails,
        successCallback: () -> Unit
    ) {
        database.deleteTvShow(tvShowDetails){
            successCallback()
        }
        database.deleteFavouriteSeasonDetails(tvShowDetails.id)
    }


    private fun cacheTvShowList(page: String, tvShowList: List<TvShow>) {
        if (page == "1") {
            _cachedTvShowList = tvShowList as MutableList<TvShow>
        } else {
            _cachedTvShowList.addAll(tvShowList)
        }
    }

    companion object {

        private var instance: TvShowRepository? = null

        fun getInstance(network: NetworkContract, database: DatabaseContract) =
            instance ?: synchronized(this) {
                instance ?: TvShowRepository(network, database).also { instance = it }
            }
    }
}


