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

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    private val _errorChannel = Channel<String>()
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
                    val msg = result.message ?: "An unknown error occurred"
                    _errorChannel.send(msg)

                    // Show cached data if available
                    if (result.data != null) {
                        _movie.value = result.data
                    }
                }
            }
        }
    }

    fun toggleBookmark() {
        val currentMovie = _movie.value ?: return

        viewModelScope.launch {
            val newStatus = !currentMovie.isBookmarked
            repository.toggleBookmark(currentMovie.id, newStatus)
            _movie.value = currentMovie.copy(isBookmarked = newStatus)
        }
    }
}