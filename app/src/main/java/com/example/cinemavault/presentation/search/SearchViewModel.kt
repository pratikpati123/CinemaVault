package com.example.cinemavault.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemavault.domain.model.Movie
import com.example.cinemavault.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Search screen.
 *
 * This ViewModel handles the logic for searching movies. It uses a debounce operator
 * to wait for the user to stop typing before performing a search.
 *
 * @param repository The repository for accessing movie data.
 */
@OptIn(FlowPreview::class) // Required for debounce
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    /**
     * A [StateFlow] that emits the current search query entered by the user.
     */
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<Movie>>(emptyList())
    /**
     * A [StateFlow] that emits the results of the movie search.
     */
    val results: StateFlow<List<Movie>> = _results

    /**
     * Initializes the ViewModel by setting up a flow that listens for changes to the search query,
     * debounces the input, and then triggers a search.
     */
    init {
        viewModelScope.launch {
            _query
                .debounce(500L) // BONUS: Wait 500ms after user stops typing
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collect { searchQuery ->
                    repository.searchMovies(searchQuery).collect { movies ->
                        _results.value = movies
                    }
                }
        }
    }

    /**
     * Called when the user changes the search query in the UI.
     *
     * @param newQuery The new search query.
     */
    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}
