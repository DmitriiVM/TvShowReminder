package com.example.tvshowreminder.screen.detail

import com.example.tvshowreminder.data.pojo.general.TvShowDetails

interface DetailContract {

    interface View{
        fun displayTvShow(tvShowDetails: TvShowDetails)
        fun showError(errorMessage: String)
        fun setButtonWitAddFunction()
        fun setButtonWithDeleteFunction()
    }

    interface Presenter{
        fun getTvShowDetail(tvShowId: Int)
        fun insertTvShowToDatabase()
        fun deleteTvShowFromDatabase()

        fun attachView(view: DetailContract.View)
    }
}