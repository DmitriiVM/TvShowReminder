package com.example.tvshowreminder.data.database

import android.util.Log
import androidx.room.*
import com.example.tvshowreminder.data.pojo.episode.Episode
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.LatestTvShow
import com.example.tvshowreminder.data.pojo.general.PopularTvShow
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails


@Dao
interface TvShowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PopularTvShow::class)
    fun insertPopularTvShowList(tvShowList: List<TvShow>) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = LatestTvShow::class)
    fun insertLatestTvShowList(tvShowList: List<TvShow>) : List<Long>

    @Query("SELECT * FROM popular_tv_shows_table")
    fun getPopularTvShowsList(): List<TvShow>

    @Query("SELECT * FROM latest_tv_shows_table")
    fun getLatestTvShowsList(): List<TvShow>

    @Query("SELECT * FROM tv_show_details")
    fun getFavouriteTvShowsList(): List<TvShowDetails>

    @Query("SELECT * FROM tv_show_details WHERE original_name LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchTvShowsList(query: String): List<TvShow>


    @Query("SELECT * FROM tv_show_details WHERE id = :tvShowId")
    fun getFavouriteTvShow(tvShowId: Int): TvShowDetails

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvShow(tvShowDetails: TvShowDetails) : Long

    @Delete
    fun deleteTvShow(tvShowDetails: TvShowDetails) : Int


    @Query("SELECT * FROM seasons_details WHERE show_id = :tvShowId AND season_number = :seasonNumber")
    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int): SeasonDetails

    @Query("SELECT * FROM episode WHERE show_id = :tvShowId AND season_number = :seasonNumber")
    fun getEpisodesGorSeason(tvShowId: Int, seasonNumber: Int) : List<Episode>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisodes(episodes: List<Episode>): List<Long>


    @Query("DELETE FROM seasons_details WHERE id = :tvShowId")
    fun deleteFavouriteSeasonDetail(tvShowId: Int): Int






}