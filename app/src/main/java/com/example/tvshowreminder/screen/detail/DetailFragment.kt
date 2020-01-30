package com.example.tvshowreminder.screen.detail


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tvshowreminder.R
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.screen.detail.tabsfragments.adapters.TabFragmentPageAdapter
import com.example.tvshowreminder.util.*
import kotlinx.android.synthetic.main.fragment_detail.*
import javax.inject.Inject

class DetailFragment : Fragment(), DetailContract.View {

    @Inject
    lateinit var presenter: DetailContract.Presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as TvShowApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvShowId = arguments?.getInt(TV_SHOW_ID)

        presenter.attachView(this)

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
                presenter.insertTvShowToDatabase(requireContext().applicationContext)
            }
        }
    }

    override fun setButtonWithDeleteFunction() {
        button_add_delete.apply {
            text = BUTTON_DELETE
            isEnabled = true
            setOnClickListener {
                presenter.deleteTvShowFromDatabase(requireContext().applicationContext)
            }
        }
    }

    override fun showProgressBar(isVisible: Boolean){
        if (isVisible) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
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
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
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
