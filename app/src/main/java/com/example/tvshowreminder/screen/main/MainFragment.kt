package com.example.tvshowreminder.screen.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.screen.detail.DetailActivity
import com.example.tvshowreminder.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.*
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.screen.settings.SettingsActivity
import kotlinx.android.synthetic.main.dialog_layout.view.*
import javax.inject.Inject

class MainFragment : Fragment(), MainScreenContract.View,
    TvShowRecyclerAdapter.OnTvShowClickListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var presenter: MainScreenContract.Presenter

    private lateinit var adapter: TvShowRecyclerAdapter
    private lateinit var searchView: SearchView
    private var menuItemId = R.id.menu_item_popular
    private var toast: Toast? = null
    private var query : String? = null
    private var page = 1
    private var afterSearch = false
    private var isNeededToLoad = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as TvShowApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        presenter.attachView(this)

        setRecyclerView()
        bottom_nav_view.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        } else {
            presenter.getTvShowList(menuItemId, page.toString())
        }
    }

    private fun restoreState(savedInstanceState: Bundle){
        page = savedInstanceState.getInt(KEY_PAGE)
        query = savedInstanceState.getString(KEY_QUERY)
        menuItemId = savedInstanceState.getInt(KEY_MENU_ITEM_ID)
        presenter.getCachedTvShowList()
    }

    private fun setRecyclerView() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        } else {
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        }

        adapter = TvShowRecyclerAdapter()
        recyclerView.adapter = adapter
        adapter.setOnShowClickListener(this)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handlePagination()
            }
        })
    }

    fun handlePagination(){
        if (menuItemId == R.id.menu_item_shows_to_follow) {
            return
        }
        if (!recyclerView.canScrollVertically(1)
            && ConnectivityHelper.isOnline(requireContext())
        ) {
            if (isNeededToLoad) {
                page++
                presenter.getTvShowList(menuItemId, page.toString())
            }else {
                isNeededToLoad = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.app_bar_search)
        searchView = searchItem.actionView as SearchView

        if (!query.isNullOrBlank()) {
                searchItem.expandActionView()
                searchView.setQuery(query, false)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchItem.collapseActionView()
                query?.let {
                    search(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.popup_menu_settings -> showSettings()
            R.id.popup_menu_about -> showAboutInfo()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSettings() {
        val intent = Intent(requireContext(), SettingsActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_SETTINGS)
    }

    private fun showAboutInfo() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()
        dialogView.button_dialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun displayTvShowList(tvShowList: List<TvShow>) {
        adapter.addItems(tvShowList)
    }

    override fun resetAdapterList(){
        adapter.resetList()
    }

    private fun search(query: String) {
        if (ConnectivityHelper.isOnline(requireContext()) || menuItemId == R.id.menu_item_shows_to_follow) {
            afterSearch = true
            val selectedItemId =
                bottom_nav_view.menu.findItem(bottom_nav_view.selectedItemId).itemId
            presenter.searchTvShow(selectedItemId, query)
        } else
            showResultMessage(MESSAGE_SEARCH_NO_INTERNET)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (menuItemId != item.itemId || afterSearch){
            page = 1
            isNeededToLoad = false
            afterSearch = false
            adapter.resetList()
            menuItemId = item.itemId
            presenter.getTvShowList(menuItemId, page.toString())
        }
        return true
    }

    override fun onTvShowClick(tvId: Int) {
        if (ConnectivityHelper.isOnline(requireContext())) {
            startDetailActivity(tvId)
        } else {
            if (menuItemId == R.id.menu_item_shows_to_follow){
                startDetailActivity(tvId)
            } else
                showResultMessage(MESSAGE_DETAILS_WITH_NO_INTERNET)
        }
    }


    private fun startDetailActivity(tvId: Int){
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(INTENT_EXTRA_TV_SHOW_ID, tvId)
        startActivityForResult(intent, REQUEST_CODE_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RESULT){
            if (menuItemId == R.id.menu_item_shows_to_follow){
                adapter.resetList()
                presenter.getTvShowList(menuItemId, page.toString())
            }
        }
        page = 1
    }

    override fun showProgressBar(isVisible: Boolean){
        if (isVisible) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun showMessage(message: String) {
        showResultMessage(message)
    }

    private fun showResultMessage(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
        toast?.apply {
            setGravity(Gravity.CENTER, 0, 300)
            show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_MENU_ITEM_ID, menuItemId)
        outState.putString(KEY_QUERY, searchView.query.toString())
        outState.putInt(KEY_PAGE, page)
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}