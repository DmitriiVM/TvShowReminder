package com.example.tvshowreminder.screen.detail.tabsfragments

import android.content.res.Resources
import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.util.Resource
import com.example.tvshowreminder.util.getDeviceLanguage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SeasonPresenter @Inject constructor(
    private val repository: TvShowRepository
) : SeasonsContract.Presenter {

    lateinit var view: SeasonsContract.View
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun attachView(view: SeasonsContract.View) {
        this.view = view
    }

    private val seasonsList = mutableListOf<SeasonDetails>()

    override fun getSeasonsDetails(tvId: Int, seasonNumber: Int, numberOfSeasons: Int){
        disposable.add(
            repository.getSeasonDetails(tvId, seasonNumber, getDeviceLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({resource ->
                    when (resource) {
                        is Resource.Error -> view.showError(resource.message)
                        is Resource.Success -> {
                            seasonsList.add(resource.data)
                            if (seasonsList.size == numberOfSeasons) {
                                seasonsList.sortBy { it.seasonNumber }
                                view.showSeasonsDetails(seasonsList)
                            }
                        }
                    }
                }, { t ->
                    view.showError(t.message ?: Resources.getSystem().getString(R.string.error_message))
                })
        )
    }

    override fun onPause() {
        seasonsList.clear()
    }

    override fun onDestroy() {
        disposable.dispose()
    }
}