package com.example.tvshowreminder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tvshowreminder.data.pojo.episode.Episode
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.LatestTvShow
import com.example.tvshowreminder.data.pojo.general.PopularTvShow
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.DB_NAME


@Database(entities = [TvShow::class, LatestTvShow::class, PopularTvShow::class,  TvShowDetails::class, SeasonDetails::class, Episode::class], version = 26)
abstract class TvShowDatabase : RoomDatabase() {

    abstract fun tvShowDao():TvShowDao

    companion object {

        @Volatile
        private var instance: TvShowDatabase? = null

        fun getInstance(context: Context): TvShowDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context): TvShowDatabase =
            Room.databaseBuilder(context.applicationContext, TvShowDatabase::class.java, DB_NAME).fallbackToDestructiveMigration().build()

    }


}