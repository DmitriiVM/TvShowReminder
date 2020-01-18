package com.example.tvshowreminder.screen.detail


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.database.DatabaseDataSource
import com.example.tvshowreminder.data.database.TvShowDatabase
import com.example.tvshowreminder.data.network.NetworkDaraSource
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.screen.detail.tabsfragments.adapters.TabFragmentPageAdapter
import com.example.tvshowreminder.util.*
//import com.example.tvshowreminder.util.setImage
import kotlinx.android.synthetic.main.fragment_detail.*

const val TV_SHOW_ID = "tv_show_id"

class DetailFragment : Fragment(), DetailContract.View {

    private lateinit var presenter: DetailPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvShowId = arguments?.getInt(TV_SHOW_ID)

        presenter = DetailPresenter(
            TvShowRepository.getInstance(
                NetworkDaraSource,
                DatabaseDataSource.getInstance(
                    TvShowDatabase.getInstance(requireContext()),
                    AppExecutors()
                )
            ), this
        )
        progress_bar.visibility = View.VISIBLE
        button_add_delete.isEnabled = false

        tvShowId?.let {
            presenter.getTvShowDetail(it)
        }
    }

    override fun setButtonWitAddFunction() {
        button_add_delete.apply {
            text = BUTTON_ADD
            isEnabled = true
            setOnClickListener {
                presenter.insertTvShowToDatabase()
            }
        }
    }

    override fun setButtonWithDeleteFunction() {
        button_add_delete.apply {
            text = BUTTON_DELETE
            isEnabled = true
            setOnClickListener {
                presenter.deleteTvShowFromDatabase()
            }
        }
    }

    override fun displayTvShow(tvShowDetails: TvShowDetails) {
        progress_bar.visibility = View.INVISIBLE
        text_view_title.text = tvShowDetails.name
        rating_bar.progress = tvShowDetails.voteAverage?.toInt() ?: 0
        image_view_tvshow_detail.setImage(tvShowDetails.backdropPath ?: "", ErrorImageOrientation.HORIZONTAL)

        view_pager.adapter =
            TabFragmentPageAdapter(
                requireActivity().supportFragmentManager,
                tvShowDetails
            )
        tab_layout.setupWithViewPager(view_pager)
    }

    override fun showError(errorMessage: String) {
        progress_bar.visibility = View.INVISIBLE
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(tvShowId: Int): Fragment {
            val fragment = DetailFragment()
            fragment.arguments = Bundle().apply {
                putInt(TV_SHOW_ID, tvShowId)
            }
            return fragment
        }
    }
}
