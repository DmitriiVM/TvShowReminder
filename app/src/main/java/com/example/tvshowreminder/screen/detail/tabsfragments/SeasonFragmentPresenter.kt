package com.example.tvshowreminder.screen.detail.tabsfragments

import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.season.SeasonDetails

class SeasonFragmentPresenter(
    private val repository: TvShowRepository,
    private val view: SeasonsContract.View
) : SeasonsContract.Presenter {

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