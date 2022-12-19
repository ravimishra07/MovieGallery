package com.ravi.fimoviesdemo

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ravi.fimoviesdemo.adapter.LoaderStateAdapter
import com.ravi.fimoviesdemo.adapter.MoviesAdapter
import com.ravi.fimoviesdemo.databinding.ActivityMainBinding
import com.ravi.fimoviesdemo.util.*
import com.ravi.fimoviesdemo.util.Constants.Companion.DEFAULT_QUERY
import com.ravi.fimoviesdemo.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val mAdapter by lazy { MoviesAdapter() }
    private var searchJob: Job? = null
    private var selectedType = MovieType.HOME
    private var searchedKey = DEFAULT_QUERY
    private var appStart = true
    private val networkManager : NetworkConnectionManager by lazy { NetworkConnectionManager(this)  }
    enum class MovieType { HOME, MOVIE, SERIES,GAME }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setObservers()
        setupRecyclerView()
        setListeners()
        getMovieData()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkManager.unregisterCallback()
    }

    private fun setObservers(){

        networkManager.result.observe(this) {
            if(!appStart){
                if (it == NetworkResult.CONNECTED) {
                    binding.root.showSnackBar(getString(R.string.connected), R.color.connect_color)
                } else {
                    binding.root.showSnackBar(
                        getString(R.string.disconnected),
                        R.color.disconnect_color
                    )
                }
            }else{
                appStart = false
            }

        }

        networkManager.registerCallback()

    }

    private fun setListeners() {
        binding.menuHome.setOnClickListener {
            selectedType = MovieType.HOME
            setMenu()
            getMovieData(searchedKey)
        }
        binding.menuMovies.setOnClickListener {
            selectedType = MovieType.MOVIE
            setMenu(MovieType.MOVIE)
            getMovieData(searchedKey, type = MovieType.MOVIE.name.lowercase())
        }
        binding.menuSeries.setOnClickListener {
            selectedType = MovieType.SERIES
            setMenu(MovieType.SERIES)
            getMovieData(searchedKey, type = MovieType.SERIES.name.lowercase())
        }
        binding.menuGame.setOnClickListener {
            selectedType = MovieType.GAME
            setMenu(MovieType.GAME)
            getMovieData(searchedKey, type = MovieType.GAME.name.lowercase())
        }
        binding.ivClose.setOnClickListener {
            hideKeyboard()
            searchedKey = DEFAULT_QUERY
            binding.etSearch.text.clear()
            binding.etSearch.clearFocus()
            binding.ivClose.hide()
            binding.ivMicrophone.show()
        }
        binding.etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = v.text.toString()
                if (searchText.isNotBlank()) {
                    if (selectedType != MovieType.HOME) {
                        searchedKey = searchText
                        getMovieData(searchText, type = selectedType.name.lowercase())
                    } else {
                        getMovieData(searchText)
                    }
                }
                hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })

        binding.etSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.ivClose.show()
                binding.ivMicrophone.hide()
            } else {
                binding.ivClose.hide()
                binding.ivMicrophone.show()
            }
        }
    }

    private fun setMenu(type: MovieType = MovieType.HOME) {
        updateMenuButtons()
        when (type) {
            MovieType.HOME -> updateSelectedMenu(binding.menuHome)
            MovieType.MOVIE -> updateSelectedMenu(binding.menuMovies)
            MovieType.SERIES -> updateSelectedMenu(binding.menuSeries)
            MovieType.GAME -> updateSelectedMenu(binding.menuGame)
        }
    }

    private fun updateMenuButtons() {
        binding.apply {
            menuHome.apply {
                setTextColor(getColor(R.color.text_menu_color))
                background = ContextCompat.getDrawable(this.context, R.drawable.menu_bg)
            }
            menuMovies.apply {
                setTextColor(getColor(R.color.text_menu_color))
                background = ContextCompat.getDrawable(this.context, R.drawable.menu_bg)
            }
            menuSeries.apply {
                setTextColor(getColor(R.color.text_menu_color))
                background = ContextCompat.getDrawable(this.context, R.drawable.menu_bg)
            }
            menuGame.apply {
                setTextColor(getColor(R.color.text_menu_color))
                background = ContextCompat.getDrawable(this.context, R.drawable.menu_bg)
            }
        }
    }

    private fun updateSelectedMenu(menuView: TextView) {
        menuView.apply {
            setTextColor(getColor(R.color.white))
            background = ContextCompat.getDrawable(this@MainActivity, R.drawable.menu_selected_bg)
        }
    }

    private fun getMovieData(query: String = DEFAULT_QUERY, type: String = "") {
        if(networkManager.result.value != NetworkResult.DISCONNECTED){
            binding.tvNoMovies.hide()
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                mainViewModel.fetchMovies(query, type).collectLatest {
                    mAdapter.submitData(it)
                }
            }
        }
    }

    private fun setupRecyclerView() {

        binding.rvMovies.adapter = mAdapter.withLoadStateHeaderAndFooter(
            header = LoaderStateAdapter(),
            footer = LoaderStateAdapter()
        )
        mAdapter.addLoadStateListener { loadState ->
            binding.rvMovies.isVisible = loadState.refresh is LoadState.NotLoading
            binding.mainProgressbar.isVisible = loadState.refresh is LoadState.Loading
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
                ?:loadState.refresh as? LoadState.Error
            errorState?.let {
                if (it.error.message != getString(R.string.no_data_found)) {
                    Toast.makeText(
                        this,
                        " Error:  ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    binding.tvNoMovies.show()
                }
            }
        }
        binding.rvMovies.layoutManager =
            GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
    }
}

