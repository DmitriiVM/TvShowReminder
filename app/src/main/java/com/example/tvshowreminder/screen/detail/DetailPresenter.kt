package com.example.tvshowreminder.screen.detail

import android.util.Log
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.general.TvShowDetails

class DetailPresenter(
    private val repository: TvShowRepository,
    private val view: DetailContract.View
) : DetailContract.Presenter {

    private var tvShowDetails: TvShowDetails? = null

    override fun getTvShowDetail(tvShowId: Int) {
        checkForPresenceInList(tvShowId)
        repository.getTvShowDetails(tvShowId, { tvShowDetails ->
            view.displayTvShow(tvShowDetails)
            this.tvShowDetails = tvShowDetails
        }, {})
    }

    override fun insertTvShowToDatabase() {
        tvShowDetails?.let {
            repository.insertTvShow(it){
                view.setButtonWithDeleteFunction()
            }
        }

    }

    override fun deleteTvShowFromDatabase() {
        tvShowDetails?.let {
            repository.deleteTvShow(it){
                view.setButtonWitAddFunction()
            }
        }
    }

    private fun checkForPresenceInList(tvShowId: Int) {
        repository.getFavouriteTvShowList({ favouriteTvShowList ->

            var isPresent = false
            favouriteTvShowList.forEach { tvShow ->
                if (tvShow.id == tvShowId) {
                    isPresent = true
                }
            }
            if (isPresent) {
                view.setButtonWithDeleteFunction()
            } else {
                view.setButtonWitAddFunction()
            }
        }, {
            view.setButtonWitAddFunction()
        })
    }
}