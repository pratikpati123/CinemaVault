package com.example.cinemavault.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemavault.core.common.Resource
import com.example.cinemavault.domain.model.Movie
import com.example.cinemavault.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Detail screen.
 *
 * This ViewModel is responsible for fetching the details of a specific movie,
 * handling bookmarking, and providing the movie data to the UI.
 *
 * @param repository The repository for accessing movie data.
 * @param savedStateHandle A handle to the saved state of the ViewModel, used to retrieve the movie ID.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    /**
     * A [StateFlow] that emits the current movie being displayed.
     */
    val movie: StateFlow<Movie?> = _movie

    private val _errorChannel = Channel<String>()
    /**
     * A flow that emits one-time error events to be displayed in the UI.
     */
    val errorEvents = _errorChannel.receiveAsFlow()

    init {
        val movieId = savedStateHandle.get<String>("movieId")?.toIntOrNull()
        if (movieId != null) {
            getMovie(movieId)
        }
    }

    private fun getMovie(id: Int) {
        viewModelScope.launch {
            when(val result = repository.getMovieById(id)) {
                is Resource.Success -> {
                    _movie.value = result.data
                }
                is Resource.Error -> {
                    // 2. Handle the error: Send message to UI
                    val msg = result.message ?: "An unknown error occurred"
                    _errorChannel.send(msg)

                    if (result.data != null) {
                        _movie.value = result.data
                    }
                }
                else -> {}
            }
        }
    }

    /**
     * Toggles the bookmark status of the current movie.
     */
    fun toggleBookmark() {
        _movie.value?.let { currentMovie ->
            viewModelScope.launch {
                val newStatus = !currentMovie.isBookmarked
                repository.toggleBookmark(currentMovie.id, newStatus)
                _movie.value = currentMovie.copy(isBookmarked = newStatus)
            }
        }
    }
}
