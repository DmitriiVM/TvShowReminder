package com.example.tvshowreminder.screen.detail.tabsfragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.database.DatabaseDataSource
import com.example.tvshowreminder.data.database.TvShowDatabase
import com.example.tvshowreminder.data.network.NetworkDaraSource
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.screen.detail.tabsfragments.adapters.SeasonsRecyclerViewAdapter
import com.example.tvshowreminder.util.AppExecutors

const val KEY_TV_ID = "tv_id"
const val KEY_NUMBER_OF_SEASONS = "number_of_seasons"


class SeasonsFragment : Fragment(), SeasonsContract.View {

    private var tvId: Int? = null
    private var numberOfSeasons: Int? = null
    private lateinit var adapter: SeasonsRecyclerViewAdapter

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

        val presenter = SeasonFragmentPresenter(
            repository = TvShowRepository.getInstance(
                network = NetworkDaraSource,
                database = DatabaseDataSource(TvShowDatabase.getInstance(requireContext()), AppExecutors())
            ),
            view = this
        )

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
}
