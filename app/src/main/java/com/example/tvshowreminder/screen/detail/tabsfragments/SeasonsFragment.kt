package com.example.tvshowreminder.screen.detail.tabsfragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshowreminder.R
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.screen.detail.tabsfragments.adapters.SeasonsRecyclerViewAdapter
import com.example.tvshowreminder.util.KEY_NUMBER_OF_SEASONS
import com.example.tvshowreminder.util.KEY_TV_ID
import javax.inject.Inject

class SeasonsFragment : Fragment(), SeasonsContract.View {

    @Inject
    lateinit var presenter: SeasonsContract.Presenter

    private var tvId: Int? = null
    private var numberOfSeasons: Int? = null
    private lateinit var adapter: SeasonsRecyclerViewAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as TvShowApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tvId = it.getInt(KEY_TV_ID)
            numberOfSeasons = it.getInt(KEY_NUMBER_OF_SEASONS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_seasons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_seasons)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SeasonsRecyclerViewAdapter()
        recyclerView.adapter = adapter
        for (i in 1..(numberOfSeasons!!)) {
            presenter.getSeasonsDetails(tvId!!, i, numberOfSeasons!!)
        }
    }

    fun newInstance(tvId: Int, numberOfSeasons: Int): SeasonsFragment{
        return SeasonsFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_TV_ID, tvId)
                putInt(KEY_NUMBER_OF_SEASONS, numberOfSeasons)
            }
        }
    }

    override fun showSeasonsDetails(seasonsList: List<SeasonDetails>) {
        adapter.setSeasonsList(seasonsList)
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
