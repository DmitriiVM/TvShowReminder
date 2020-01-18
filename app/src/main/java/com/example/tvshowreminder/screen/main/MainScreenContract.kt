package com.example.tvshowreminder.screen.main

import com.example.tvshowreminder.data.pojo.general.TvShow

interface MainScreenContract {

    interface View{
        fun displayTvShowList(tvShowList: List<TvShow>)
        fun showMessage(message: String)
        fun resetAdapterList()
    }

    interface Presenter{
        fun getTvShowList(itemId: Int, page: String)
        fun getPopularTvShowList(page: String)
        fun searchTvShow(selectedItemId: Int, query: String, page: String )
        fun getCachedTvShowList(): List<TvShow>
        fun getCachedFavouriteTvShowList(): List<TvShow>
    }
}