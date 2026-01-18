package com.example.cinemavault.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemavault.core.common.Resource
import com.example.cinemavault.domain.model.Movie
import com.example.cinemavault.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the state of the home screen.
 *
 * @property trendingMovies The list of currently trending movies.
 * @property nowPlayingMovies The list of movies currently playing in theaters.
 * @property isLoading A boolean indicating if the screen is currently loading data.
 * @property error An optional error message to be displayed.
 */
data class HomeState(
    val trendingMovies: List<Movie> = emptyList(),
    val nowPlayingMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the Home screen.
 *
 * This ViewModel is responsible for fetching and managing the data for the home screen,
 * including trending movies and movies currently playing in theaters.
 *
 * @param repository The repository for accessing movie data.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    /**
     * A [StateFlow] that emits the current state of the home screen.
     */
    val state: StateFlow<HomeState> = _state

    /**
     * Initializes the ViewModel by loading the initial home screen data.
     */
    init {
        loadHomeData()
    }

    /**
     * Fetches the trending and now playing movies from the repository and updates the UI state.
     * It collects data from both flows and updates the state accordingly.
     */
    private fun loadHomeData() {
        viewModelScope.launch {
            // Collect Trending
            repository.getTrendingMovies(forceFetch = true).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            trendingMovies = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message,
                            // Even on error, show cached data if available
                            trendingMovies = result.data ?: emptyList()
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }
                }
            }

            // Collect Now Playing (Launch in parallel or sequentially as needed)
            repository.getNowPlayingMovies(forceFetch = true).collect { result ->
                if (result is Resource.Success) {
                    _state.value = _state.value.copy(nowPlayingMovies = result.data ?: emptyList())
                }
            }
        }
    }
}
