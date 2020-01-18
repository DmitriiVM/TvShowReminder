package com.example.tvshowreminder.util

import android.content.Context
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tvshowreminder.R
import com.example.tvshowreminder.util.ErrorImageOrientation.*
import java.text.SimpleDateFormat
import java.util.*

enum class ErrorImageOrientation{
    HORIZONTAL, VERTICAL
}

fun ImageView.setImage(posterPath: String, orientation: ErrorImageOrientation){

    val requestOptions = RequestOptions()
    when (orientation){
        HORIZONTAL -> {
            requestOptions.error(R.drawable.no_image_available_horizontal)
        }
        VERTICAL -> {
            requestOptions.error(R.drawable.no_image_available_vertical)
        }
    }
    Glide
        .with(context)
        .setDefaultRequestOptions(requestOptions)
        .load(BASE_IMAGE_URL + posterPath)
        .into(this)
}

fun getCurrentDate(): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return simpleDateFormat.format(Date())
}

fun getDeviceLanguage(): String {
    return Locale.getDefault().toLanguageTag()
}