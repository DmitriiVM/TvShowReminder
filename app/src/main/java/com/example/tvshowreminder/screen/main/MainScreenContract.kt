package com.example.tvshowreminder.screen.main

import com.example.tvshowreminder.data.pojo.general.TvShow

interface MainScreenContract {

    interface View{
        fun displayTvShowList(tvShowList: List<TvShow>)
        fun showMessage(message: String)
        fun resetAdapterList()
        fun showProgressBar(isVisible: Boolean)
    }

    interface Presenter{
        fun getTvShowList(itemId: Int, page: String)
        fun searchTvShow(selectedItemId: Int, query: String)
        fun getCachedTvShowList(): List<TvShow>
        fun getCachedFavouriteTvShowList(): List<TvShow>
        fun attachView(view: View)
        fun onDestroy()
    }
}