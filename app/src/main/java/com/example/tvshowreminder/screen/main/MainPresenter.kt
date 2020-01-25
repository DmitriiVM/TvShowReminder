package com.example.tvshowreminder.screen.main

import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.util.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val repository: TvShowRepository
) : MainScreenContract.Presenter {

    lateinit var view: MainScreenContract.View
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun attachView(view: MainScreenContract.View){
        this.view = view
    }
    private val language = getDeviceLanguage()
    private val currentDate = getCurrentDate()

    override fun getTvShowList(itemId: Int, page: String) {
        when (itemId) {
            R.id.menu_item_popular -> {
                getTvShows(repository.getPopularTvShowList(language = language, page = page))
            }
            R.id.menu_item_latest -> {
                getTvShows(repository.getLatestTvShowList(currentDate = currentDate, language = language, page = page))
            }
            R.id.menu_item_shows_to_follow -> {
                getTvShows(repository.getFavouriteTvShowList())
            }
        }
    }

    private fun getTvShows(resource: Flowable<Resource<List<TvShow>>>) = disposable.add(
        resource
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                view.apply {
                    showProgressBar(false)
                    when (res) {
                        is Resource.Loading -> showProgressBar(true)
                        is Resource.EmptyList -> {
                            resetAdapterList()
                            showMessage(MESSAGE_NO_TVSHOWS_IN_LIST)
                        }
                        is Resource.Error -> showMessage(res.message)
                        is Resource.Success -> displayTvShowList(res.data)
                        is Resource.SuccessWithMessage -> {
                            showMessage(res.networkErrorMessage)
                            displayTvShowList(res.data)
                            disposable.clear()
                        }
                    }
                }
            }, { t ->
                view.showMessage(t.message ?: ERROR_MESSAGE)
            })
    )


    override fun searchTvShow(selectedItemId: Int, query: String) {
        when (selectedItemId) {
            R.id.menu_item_popular, R.id.menu_item_latest  ->
                search(repository.searchTvShowsList(query, language))
            R.id.menu_item_shows_to_follow -> search(repository.searchTvShowsListInFavourite(query))
        }
    }

    private fun search(resource: Flowable<Resource<List<TvShow>>>) = disposable.add(
        resource
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                view.apply {
                    showProgressBar(false)
                    when (res) {
                        is Resource.Loading -> showProgressBar(true)
                        is Resource.EmptyList -> showMessage(MESSAGE_NO_SEARCH_MATCHES)
                        is Resource.Error -> showMessage(res.message)
                        is Resource.Success -> {
                            resetAdapterList()
                            displayTvShowList(res.data)
                        }
                    }
                }
            }, { t ->
                view.showMessage(t.message ?: ERROR_MESSAGE)
            })
    )

    override fun getCachedTvShowList() = repository.cachedTvShowList

    override fun getCachedFavouriteTvShowList(): List<TvShow> {
        val tvShowsDetailsList = repository.cachedFavouriteTvShowList
        val tvShowList  = mutableListOf<TvShow>()
        tvShowsDetailsList.forEach {
            tvShowList.add(TvShow(id = it.id, name = it.name, posterPath = it.posterPath, voteAverage = it.voteAverage))
        }
        return tvShowList
    }

    override fun onDestroy() = disposable.dispose()
}