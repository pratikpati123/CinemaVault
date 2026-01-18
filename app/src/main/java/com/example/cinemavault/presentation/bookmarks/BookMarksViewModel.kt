package com.example.cinemavault.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemavault.domain.model.Movie
import com.example.cinemavault.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Bookmarks screen.
 *
 * This ViewModel is responsible for fetching and managing the list of bookmarked movies.
 *
 * @param repository The repository for accessing movie data.
 */
@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<Movie>>(emptyList())
    /**
     * A [StateFlow] that emits the current list of bookmarked movies.
     */
    val bookmarks: StateFlow<List<Movie>> = _bookmarks

    /**
     * Initializes the ViewModel by launching a coroutine to collect the bookmarked movies
     * from the repository and update the UI state.
     */
    init {
        viewModelScope.launch {
            repository.getBookmarkedMovies().collect { movies ->
                _bookmarks.value = movies
            }
        }
    }
}
