package com.example.tvshowreminder.data

import android.util.Log
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.*
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvShowRepository @Inject constructor(
    private val database: DatabaseContract
) {

    private var _cachedTvShowList = mutableListOf<TvShow>()
    val cachedTvShowList: List<TvShow>
        get() = _cachedTvShowList


    fun getPopularTvShowList(language: String, page: String ) : Single<Resource<List<TvShow>>> =
        MovieDbApiService.tvShowService().getPopularTvShowList(language = language, page = page)
            .map { tvShowsList ->

                tvShowsList.showsList
            }
            .map { tvShowList: List<TvShow> ->
                cacheTvShowList(page, tvShowList)
                database.insertPopularTvShowList(tvShowList)
                Resource.create(tvShowList)
            }
            .onErrorResumeNext { _: Throwable ->
                database.getPopularTvShowList()
                    .map { tvShowList: List<TvShow>  ->
                        val sortedList = tvShowList.sortedByDescending { it.popularity }
                        cacheTvShowList(page, sortedList)
                        Resource.create(sortedList, ERROR_MESSAGE_NETWORK_PROBLEM_1)
                    }
                    .doOnError { t2 ->
                        Resource.createError<List<TvShow>>(t2.message ?: ERROR_MESSAGE)
                    }
            }


    fun getLatestTvShowList(currentDate: String, language: String, page: String) : Single<Resource<List<TvShow>>> =
        MovieDbApiService.tvShowService().getLatestTvShowList(
            currentDate = currentDate, language = language, page = page
        )
            .map { tvShowsList ->
                tvShowsList.showsList
            }
            .map { tvShowList: List<TvShow> ->
                cacheTvShowList(page, tvShowList)
                database.insertLatestTvShowList(tvShowList)
                Resource.create(tvShowList)
            }
            .onErrorResumeNext { _: Throwable ->
                database.getLatestTvShowList()
                    .map { tvShowList: List<TvShow>  ->
                        val sortedList = tvShowList.sortedByDescending { it.firstAirDate }
                        cacheTvShowList(page, sortedList)
                        Resource.create(sortedList, ERROR_MESSAGE_NETWORK_PROBLEM_1)
                    }
                    .doOnError { t2 ->
                        Resource.createError<List<TvShow>>(t2.message ?: ERROR_MESSAGE)
                    }
            }


    fun getFavouriteTvShowList() = database.getFavouriteTvShowList()
        .map { tvShowDetailsList ->
            cacheTvShowList("1", convertToTvShowList(tvShowDetailsList))
            Resource.create(convertToTvShowList(tvShowDetailsList))
        }
        .doOnError { t ->
            Resource.createError<List<TvShow>>(t.message ?: ERROR_MESSAGE)
        }

    private fun convertToTvShowList(tvShowDetailListFromDb: List<TvShowDetails>): List<TvShow> {
        val tvShowList = mutableListOf<TvShow>()
        tvShowDetailListFromDb.forEach { tvShoeDetail ->
                tvShowList.add(
                    TvShow(id = tvShoeDetail.id, name = tvShoeDetail.name,
                        originalName = tvShoeDetail.originalName,
                        posterPath = tvShoeDetail.posterPath,
                        voteAverage = tvShoeDetail.voteAverage,
                        popularity = tvShoeDetail.popularity)
                )
        }
        return tvShowList
    }

    fun searchTvShowsList(query: String, language: String) : Flowable<Resource<List<TvShow>>> =
        MovieDbApiService.tvShowService().searchTvShow(query = query, language = language)
            .map { tvShowsList ->
                tvShowsList.showsList
            }
            .compose(searchTvShows())

    fun searchTvShowsListInFavourite(query: String) : Flowable<Resource<List<TvShow>>> =
        database.searchFavouriteTvShowsList(query)
            .compose(searchTvShows())

    private fun searchTvShows(): FlowableTransformer<List<TvShow>, Resource<List<TvShow>>>{
        return FlowableTransformer {
            it.doOnNext { tvShowList ->
                if (tvShowList.isNotEmpty()) {
                    cacheTvShowList("1", tvShowList)
                }
            }
                .map { tvShowList ->
                    if (tvShowList.isEmpty()) {
                        Resource.createNoResult()
                    } else {
                        Resource.create(tvShowList)
                    }
                }
                .doOnError {
                    Resource.createError<List<TvShow>>(ERROR_MESSAGE_NETWORK_PROBLEM_2)
                }
                .startWith(Resource.create())
        }
    }

    fun getTvShowDetails(tvId: Int, language: String) =
        MovieDbApiService.tvShowService().getTvShowDetails(tvId, language)
            .map { tvShowDetails ->
                Resource.create(tvShowDetails)
            }
            .onErrorResumeNext { _: Throwable ->
                database.getTvShow(tvId)
                    .map { tvShowList ->
                        Resource.create(tvShowList)
                    }
                    .doOnError { t2 ->
                        Resource.createError<TvShowDetails>(t2.message ?: ERROR_MESSAGE)
                    }
            }
            .startWith(Resource.create())


    fun getSeasonDetails(tvId: Int, seasonNumber: Int, language: String) =
        MovieDbApiService.tvShowService().getSeasonDetails(tvId, seasonNumber, language)
            .map {seasonDetails ->
                database.insertFavouriteSeasonDetails(seasonDetails)
                Resource.create(seasonDetails)
            }
            .onErrorResumeNext { _: Throwable ->
                database.getFavouriteSeasonDetails(tvId, seasonNumber)
                    .map {seasonDetails ->
                        Resource.create(seasonDetails)
                    }
                    .doOnError { t2 ->
                        Resource.createError<SeasonDetails>(t2.message ?: ERROR_MESSAGE)
                    }
            }
            .startWith(Resource.create())

    fun insertTvShow(
        tvShowDetails: TvShowDetails
    ): Completable  {
        tvShowDetails.numberOfSeasons?.let {
            for (i in 1..it) {
                MovieDbApiService.tvShowService().getSeasonDetails(tvShowDetails.id, i)
                    .subscribeOn(Schedulers.io())
                    .doOnNext {seasonDetails ->
                        database.insertFavouriteSeasonDetails(seasonDetails)
                    }
                    .subscribe({}, {})
            }
        }
        return database.insertTvShow(tvShowDetails)
    }

    fun deleteTvShow(
        tvShowDetails: TvShowDetails
    ): Completable {
        return database.deleteTvShow(tvShowDetails)
            .doOnEvent { database.deleteFavouriteSeasonDetails(tvShowDetails.id) }
    }

    private fun cacheTvShowList(page: String, tvShowList: List<TvShow>) {
        if (page == "1") {
            _cachedTvShowList = tvShowList as MutableList<TvShow>
        } else {
            _cachedTvShowList.addAll(tvShowList)
        }
    }
}


