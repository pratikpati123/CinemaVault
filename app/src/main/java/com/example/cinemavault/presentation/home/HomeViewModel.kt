package com.example.cinemavault.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemavault.core.common.Resource
import com.example.cinemavault.domain.model.Movie
import com.example.cinemavault.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val trendingMovies: List<Movie> = emptyList(),
    val nowPlayingMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val state: StateFlow<HomeState> = combine(
        repository.getTrendingMovies(),
        repository.getNowPlayingMovies(),
        _isLoading,
        _error
    ) { trending, nowPlaying, loading, error ->
        HomeState(
            trendingMovies = trending,
            nowPlayingMovies = nowPlaying,
            isLoading = loading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState(isLoading = true)
    )

    init {
        loadHomeData()
    }

    /**
     * Triggers the network refresh.
     * This is a "Fire and Forget" command. The results will arrive via the 'state' flow above
     * because the Repository updates the Database, and 'state' observes the Database.
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // [FIX] Run fetches in PARALLEL using async
            // We use 'await()' to wait for both to finish before hiding the loading spinner
            val trendingJob = async { repository.refreshTrendingMovies() }
            val nowPlayingJob = async { repository.refreshNowPlayingMovies() }

            val trendingResult = trendingJob.await()
            val nowPlayingResult = nowPlayingJob.await()

            // Simple error handling: if either fails, show the error message.
            if (trendingResult is Resource.Error) {
                _error.value = trendingResult.message
            } else if (nowPlayingResult is Resource.Error) {
                _error.value = nowPlayingResult.message
            }

            _isLoading.value = false
        }
    }
}