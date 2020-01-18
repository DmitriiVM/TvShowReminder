package com.example.tvshowreminder.screen.main

import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.MESSAGE_NO_SEARCH_MATCHES
import com.example.tvshowreminder.util.MESSAGE_NO_TVSHOWS_IN_LIST
import com.example.tvshowreminder.util.getCurrentDate
import com.example.tvshowreminder.util.getDeviceLanguage

class MainPresenter(
    private val repository: TvShowRepository,
    private val view: MainScreenContract.View
) : MainScreenContract.Presenter {


    private val language = getDeviceLanguage()
    private val currentDate = getCurrentDate()

    override fun getTvShowList(itemId: Int, page: String) {
        when (itemId) {
            R.id.menu_item_popular -> {
                getPopularTvShowList(page)
            }
            R.id.menu_item_latest -> {
                getLatestTvShowList(currentDate, language, page)
            }
            R.id.menu_item_shows_to_follow -> {
                getFavouriteTvShowList()
            }
        }
    }

    override fun getPopularTvShowList(page: String) {
        repository.getPopularTvShowList(language = language, page = page,
            successCallback = { tvShowList ->
                view.displayTvShowList(tvShowList)
            }, errorCallback = { errorMessage ->
                view.showMessage(errorMessage)
            })
    }

    private fun getLatestTvShowList(currentDate: String, language: String, page: String) {
        repository.getLatestTvShowList(currentDate = currentDate, language = language, page = page,
            successCallback = { tvShowList ->
                view.displayTvShowList(tvShowList)
            }, errorCallback = { errorMessage ->
                view.showMessage(errorMessage)
            })
    }

    private fun getFavouriteTvShowList() {
        repository.getFavouriteTvShowList({ tvShowDetailListFromDb ->
            if (tvShowDetailListFromDb.isNullOrEmpty()) {
                view.resetAdapterList()
                view.showMessage(MESSAGE_NO_TVSHOWS_IN_LIST)
            } else {
                val tvShowList = mutableListOf<TvShow>()
                for (i in tvShowDetailListFromDb.indices) {
                    repository.getTvShowDetails(tvShowDetailListFromDb[i].id, { tvShoeDetail ->
                        tvShowList.add(
                            TvShow(
                                id = tvShoeDetail.id,
                                name = tvShoeDetail.name,
                                originalName = tvShoeDetail.originalName,
                                posterPath = tvShoeDetail.posterPath,
                                voteAverage = tvShoeDetail.voteAverage
                            )
                        )
                        if (tvShowList.size == tvShowDetailListFromDb.size) {
                            view.displayTvShowList(tvShowList)
                        }
                    }, {})
                }
            }
        }, { errorMessage ->
            view.showMessage(errorMessage)
        })
    }


    override fun searchTvShow(selectedItemId: Int, query: String, page: String) {
        when (selectedItemId) {
            R.id.menu_item_popular, R.id.menu_item_latest  -> {
                repository.searchTvShowsList(query = query, language = language, page = page, successCallback = { tvShowList ->
                    if (tvShowList.isEmpty()){
                        view.showMessage(MESSAGE_NO_SEARCH_MATCHES)
                    } else {
                        view.resetAdapterList()
                        view.displayTvShowList(tvShowList)
                    }
                }, errorCallback = { errorMessage ->
                    view.showMessage(errorMessage)
                })
            }
            R.id.menu_item_shows_to_follow -> {
                repository.searchTvShowsListInFavourite(query, { tvShowList ->
                    view.resetAdapterList()
                    view.displayTvShowList(tvShowList)
                }, { errorMessage ->
                    view.showMessage(errorMessage)
                })
            }
        }
    }

    override fun getCachedTvShowList() = repository.cachedTvShowList

    override fun getCachedFavouriteTvShowList(): List<TvShow> {
        val tvShowsDetailsList = repository.cachedFavouriteTvShowList
        val tvShowList  = mutableListOf<TvShow>()
        tvShowsDetailsList.forEach {
            tvShowList.add(TvShow(id = it.id, name = it.name, posterPath = it.posterPath, voteAverage = it.voteAverage))
        }
        return tvShowList
    }
}