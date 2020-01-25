package com.example.tvshowreminder.screen.detail

import android.util.Log
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.ERROR_MESSAGE
import com.example.tvshowreminder.util.Resource
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val repository: TvShowRepository
) : DetailContract.Presenter {

    lateinit var view: DetailContract.View
    private var disposable: CompositeDisposable = CompositeDisposable()

    private var tvShowDetails: TvShowDetails? = null

    override fun attachView(view: DetailContract.View) {
        this.view = view
    }

    override fun getTvShowDetail(tvShowId: Int) {
        disposable.add(
            repository.getTvShowDetails(tvShowId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({resource ->
                    view.apply {
                        showProgressBar(false)
                        when (resource) {
                            is Resource.Loading -> showProgressBar(true)
                            is Resource.Error -> showError(resource.message)
                            is Resource.Success -> {
                                view.displayTvShow(resource.data)
                                tvShowDetails = resource.data
                                setButton(tvShowId)

                            }
                        }
                    }
                }, { t ->
                    view.showProgressBar(false)
                    view.showError(t.message ?: ERROR_MESSAGE)
                })
        )
    }

    override fun insertTvShowToDatabase() {
        tvShowDetails?.let {
            addOrDeleteTvShow(repository.insertTvShow(it))
        }
    }

    override fun deleteTvShowFromDatabase() {
        tvShowDetails?.let {
            addOrDeleteTvShow(repository.deleteTvShow(it))
        }
    }

    private fun addOrDeleteTvShow(completable: Completable) {
        disposable.add(
            completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.showProgressBar(false)
                }, {})
        )
    }

    private fun setButton(tvShowId: Int) {
        disposable.add(
            repository.getFavouriteTvShowList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    view.setButtonWitAddFunction()
                }
                .subscribe {
                    when (it) {
                        is Resource.Success -> checkAndSetButton(it.data, tvShowId)
                    }
                }
        )
    }

    private fun checkAndSetButton(list: List<TvShow>, tvShowId: Int) {
        list.forEach { tvShow ->
            if (tvShow.id == tvShowId) {
                view.setButtonWithDeleteFunction()
                return
            }
        }
        view.setButtonWitAddFunction()
    }

    override fun onDestroy(){
        disposable.dispose()
    }
}