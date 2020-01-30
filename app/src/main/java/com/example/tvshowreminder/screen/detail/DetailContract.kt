package com.example.tvshowreminder.screen.detail

import android.content.Context
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails

interface DetailContract {

    interface View{
        fun displayTvShow(tvShowDetails: TvShowDetails)
        fun showError(errorMessage: String)
        fun setButtonWitAddFunction()
        fun setButtonWithDeleteFunction()
        fun showProgressBar(isVisible: Boolean)
    }

    interface Presenter{
        fun getTvShowDetail(tvShowId: Int)
        fun insertTvShowToDatabase(appContext: Context)
        fun deleteTvShowFromDatabase(appContext: Context)
        fun attachView(view: View)
        fun onDestroy()
    }
}