package com.example.tvshowreminder.data.database


import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DatabaseDataSource @Inject constructor(
    private val tvShowDatabase: TvShowDatabase
) : DatabaseContract {

    override fun getPopularTvShowList()
            = tvShowDatabase.tvShowDao().getPopularTvShowsList()

    override fun getLatestTvShowList(): Flowable<List<TvShow>>
            = tvShowDatabase.tvShowDao().getLatestTvShowsList()

    override fun getFavouriteTvShowList(): Flowable<List<TvShowDetails>>
            = tvShowDatabase.tvShowDao().getFavouriteTvShowsList()

    override fun getFavouriteSeasonDetails(
        tvShowId: Int,
        seasonNumber: Int
    ): Flowable<SeasonDetails> {

        return tvShowDatabase.tvShowDao().getFavouriteSeasonDetails(tvShowId, seasonNumber)
            .map {seasonsDetails ->
                tvShowDatabase.tvShowDao().getEpisodesForSeason(tvShowId, seasonNumber)
                    .doOnError {}
                    .subscribe {
                        seasonsDetails.episodes = it
                    }
                seasonsDetails
            }
    }

    override fun insertPopularTvShowList(
        tvShowList: List<TvShow>
    ) =
        tvShowDatabase.tvShowDao().insertPopularTvShowList(tvShowList)


    override fun insertLatestTvShowList(
        tvShowList: List<TvShow>
    ) =  tvShowDatabase.tvShowDao().insertLatestTvShowList(tvShowList)

    override fun getTvShow(tvShowId: Int)  =
        tvShowDatabase.tvShowDao().getFavouriteTvShow(tvShowId)

    override fun insertTvShow(tvShowDetails: TvShowDetails) =
        tvShowDatabase.tvShowDao().insertTvShow(tvShowDetails)

    override fun deleteTvShow(tvShowDetails: TvShowDetails) =
        tvShowDatabase.tvShowDao().deleteTvShow(tvShowDetails)

    override fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails) {

        val tvShowId = seasonDetails.episodes?.get(0)?.showId
        seasonDetails.showId = tvShowId
        seasonDetails.episodes?.let {
            tvShowDatabase.tvShowDao().insertEpisodes(it)
        }
        tvShowDatabase.tvShowDao().insertFavouriteSeasonDetails(seasonDetails)
    }

    override fun deleteFavouriteSeasonDetails(tvShowId: Int) =
            tvShowDatabase.tvShowDao().deleteFavouriteSeasonDetail(tvShowId)

    override fun searchFavouriteTvShowsList(query: String): Flowable<List<TvShow>>
            = tvShowDatabase.tvShowDao().searchTvShowsList(query)
}