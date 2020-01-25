package com.example.tvshowreminder.di

import android.content.Context
import androidx.room.Room
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.database.DatabaseDataSource
import com.example.tvshowreminder.data.database.TvShowDatabase
import com.example.tvshowreminder.screen.detail.DetailContract
import com.example.tvshowreminder.screen.detail.DetailPresenter
import com.example.tvshowreminder.screen.detail.tabsfragments.SeasonPresenter
import com.example.tvshowreminder.screen.detail.tabsfragments.SeasonsContract
import com.example.tvshowreminder.screen.main.MainPresenter
import com.example.tvshowreminder.screen.main.MainScreenContract
import com.example.tvshowreminder.util.DB_NAME
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Module
    companion object {

        @Singleton
        @Provides
        @JvmStatic
        fun provideTvShowDatabase(context: Context): TvShowDatabase =
            Room.databaseBuilder(context, TvShowDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration().build()
    }

    @Binds
    abstract fun bindMainPresenter(presenter: MainPresenter): MainScreenContract.Presenter

    @Binds
    abstract fun bindDetailPresenter(presenter: DetailPresenter): DetailContract.Presenter

    @Binds
    abstract fun bindSeasonPresenter(presenter: SeasonPresenter): SeasonsContract.Presenter

    @Binds
    abstract fun bindDatabaseContract(databaseContract: DatabaseDataSource): DatabaseContract
}