package com.example.tvshowreminder

import android.app.Application
import com.example.tvshowreminder.di.AppComponent
import com.example.tvshowreminder.di.DaggerAppComponent

open class TvShowApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}