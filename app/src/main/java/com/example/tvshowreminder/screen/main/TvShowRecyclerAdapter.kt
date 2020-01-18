package com.example.tvshowreminder.screen.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.util.BASE_IMAGE_URL
import com.example.tvshowreminder.util.ErrorImageOrientation
import com.example.tvshowreminder.util.SharedPreferenceHelper
import com.example.tvshowreminder.util.setImage
import kotlinx.android.synthetic.main.tvshow_item_compact_type.view.*
import kotlinx.android.synthetic.main.tvshow_item_poster_type.view.*

class TvShowRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onTvShowClickListener: OnTvShowClickListener? = null
    private var tvShowList: MutableList<TvShow> = arrayListOf()

    fun getList() = tvShowList

    fun addItems(newTvShowList: List<TvShow>) {
        val lastPosition  = tvShowList.size
        this.tvShowList.addAll(newTvShowList)
        notifyItemRangeInserted(lastPosition, tvShowList.size)
    }

    fun resetList() {
        tvShowList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TvShowViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tvshow_item_compact_type, parent, false))
    }

    override fun getItemCount(): Int = tvShowList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TvShowViewHolder).onBind(tvShowList[position])
    }

    inner class TvShowViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun onBind(tvShow: TvShow) {
            view.apply {
                image_view_compact_type.setImage(tvShow.posterPath ?: "", ErrorImageOrientation.VERTICAL)
                text_view_title.text = tvShow.name
                text_view_raiting.text = tvShow.voteAverage.toString()
                text_view_popularuty.text = tvShow.popularity?.toInt().toString()
            }
            view.setOnClickListener {
                onTvShowClickListener?.onTvShowClick(tvShow.id)
            }
        }
    }

    interface OnTvShowClickListener {
        fun onTvShowClick(tvId: Int)
    }

    fun setOnShowClickListener(onTvShowClickListener: OnTvShowClickListener) {
        this.onTvShowClickListener = onTvShowClickListener
    }
}

