package com.example.tvshowreminder.screen.detail.tabsfragments

import com.example.tvshowreminder.data.pojo.season.SeasonDetails

interface SeasonsContract {

    interface View{
        fun showSeasonsDetails(seasonsList: List<SeasonDetails>)
        fun showError(message: String)
    }

    interface Presenter{
        fun getSeasonsDetails(tvId: Int, seasonNumber: Int, numberOfSeasons: Int)
    }
}