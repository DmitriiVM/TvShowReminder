package com.example.tvshowreminder.data

import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.util.ERROR_MESSAGE
import com.example.tvshowreminder.util.ERROR_MESSAGE_NETWORK_PROBLEM_1
import com.example.tvshowreminder.util.ERROR_MESSAGE_NETWORK_PROBLEM_2
import com.example.tvshowreminder.util.Resource
import io.reactivex.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvShowRepository @Inject constructor(
    private val database: DatabaseContract
) {

    private var _cachedTvShowList = mutableListOf<TvShow>()
    val cachedTvShowList: List<TvShow>
        get() = _cachedTvShowList

    var cachedFavouriteTvShowList = listOf<TvShowDetails>()


    fun getPopularTvShowList(language: String, page: String ) : Flowable<Resource<List<TvShow>>> =
        MovieDbApiService.tvShowService().getPopularTvShowList(language = language, page = page)
            .compose(getList(page))

    fun getLatestTvShowList(currentDate: String, language: String, page: String) : Flowable<Resource<List<TvShow>>> =
        MovieDbApiService.tvShowService().getLatestTvShowList(
            currentDate = currentDate, language = language, page = page
        )
            .compose(getList(page))

    private fun getList(page: String): FlowableTransformer<TvShowsList, Resource<List<TvShow>>> =
        FlowableTransformer {
            it
                .map { tvShowsList ->
                    tvShowsList.showsList
                }
                .doOnNext { tvShowList ->
                    cacheTvShowList(page, tvShowList)
                    database.insertPopularTvShowList(tvShowList)
                }
                .map { tvShowList ->
                    Resource.create(tvShowList)
                }
                .onErrorResumeNext { t: Throwable ->
                    database.getPopularTvShowList()
                        .doOnNext { tvShowList ->
                            cacheTvShowList(page, tvShowList)
                        }
                        .map { tvShowList ->
                            Resource.create(tvShowList, ERROR_MESSAGE_NETWORK_PROBLEM_1)
                        }
                        .doOnError { t2 ->
                            Resource.createError<List<TvShow>>(t2.message ?: ERROR_MESSAGE)
                        }
                }
                .startWith(Resource.create())
        }


    fun getFavouriteTvShowList()= database.getFavouriteTvShowList()
        .map {tvShowDetailsList ->
                Resource.create(convertToTvShowList(tvShowDetailsList))
        }
        .doOnError { t ->
            Resource.createError<List<TvShow>>(t.message ?: ERROR_MESSAGE)
        }
        .startWith(Resource.create())

    private fun convertToTvShowList(tvShowDetailListFromDb: List<TvShowDetails>): List<TvShow> {
        val tvShowList = mutableListOf<TvShow>()
        tvShowDetailListFromDb.forEach { tvShoeDetail ->
                tvShowList.add(
                    TvShow(id = tvShoeDetail.id, name = tvShoeDetail.name,
                        originalName = tvShoeDetail.originalName,
                        posterPath = tvShoeDetail.posterPath,
                        voteAverage = tvShoeDetail.voteAverage)
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

    fun getTvShowDetails(tvId: Int) =
        MovieDbApiService.tvShowService().getTvShowDetails(tvId)
            .map { tvShowDetails ->
                Resource.create(tvShowDetails)
            }
            .onErrorResumeNext { t: Throwable ->
                database.getTvShow(tvId)
                    .map { tvShowList ->
                        Resource.create(tvShowList)
                    }
                    .doOnError { t2 ->
                        Resource.createError<TvShowDetails>(t2.message ?: ERROR_MESSAGE)
                    }
            }
            .startWith(Resource.create())


    fun getSeasonDetails(tvId: Int, seasonNumber: Int) =
        MovieDbApiService.tvShowService().getSeasonDetails(tvId, seasonNumber)
            .map {seasonDetails ->
                database.insertFavouriteSeasonDetails(seasonDetails)
                Resource.create(seasonDetails)
            }
            .onErrorResumeNext { t: Throwable ->
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
        return database.insertTvShow(tvShowDetails)
            .andThen {
                tvShowDetails.numberOfSeasons?.let {
                    for (i in 1..it) {
                        MovieDbApiService.tvShowService().getSeasonDetails(tvShowDetails.id, i)
                            .doOnNext {seasonDetails ->
                                database.insertFavouriteSeasonDetails(seasonDetails)
                            }
                            .subscribe({}, {

                            })
                    }
                }
            }
    }

    fun deleteTvShow(
        tvShowDetails: TvShowDetails
    ): Completable {
        return database.deleteTvShow(tvShowDetails)
            .andThen {
                database.deleteFavouriteSeasonDetails(tvShowDetails.id)
            }
    }

    private fun cacheTvShowList(page: String, tvShowList: List<TvShow>) {
        if (page == "1") {
            _cachedTvShowList = tvShowList as MutableList<TvShow>
        } else {
            _cachedTvShowList.addAll(tvShowList)
        }
    }
}


