package com.example.tvshowreminder.screen.detail

import android.util.Log
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val repository: TvShowRepository
) : DetailContract.Presenter {

    lateinit var view: DetailContract.View

    override fun attachView(view: DetailContract.View) {
        this.view = view
    }

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