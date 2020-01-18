package com.example.tvshowreminder.data.pojo.episode


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class NextEpisodeToAir(
    @SerializedName("air_date")
    val airDate: String?,
    @SerializedName("episode_number")
    val episodeNumber: Int?,
    val nameNextEpisode: String?,
    val overviewNextEpisode: String?,
    @SerializedName("season_number")
    val seasonNumber: Int?
) : Parcelable