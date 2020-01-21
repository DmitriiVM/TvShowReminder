package com.example.tvshowreminder.screen.detail.tabsfragments

import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import javax.inject.Inject

class SeasonFragmentPresenter @Inject constructor(
    private val repository: TvShowRepository
) : SeasonsContract.Presenter {

    lateinit var view: SeasonsContract.View

    override fun attachView(view: SeasonsContract.View) {
        this.view = view
    }

    private val seasonsList = mutableListOf<SeasonDetails>()

    override fun getSeasonsDetails(tvId: Int, seasonNumber: Int, numberOfSeasons: Int){
        repository.getSeasonDetails(tvId, seasonNumber, {
            seasonsList.add(it)
            if (seasonsList.size == numberOfSeasons){
                seasonsList.sortBy { seasonDetails ->
                    seasonDetails.seasonNumber
                }
                view.showSeasonsDetails(seasonsList)
            }
        }, {})
    }


}