package com.example.cinemavault.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemavault.domain.model.Movie
import com.example.cinemavault.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val results: StateFlow<List<Movie>> = _query
        .debounce(500L)            // 1. Wait for typing to stop
        .distinctUntilChanged()    // 2. Ignore same query
        .flatMapLatest { searchQuery ->
            // 3. CANCEL previous request if new one arrives
            if (searchQuery.isBlank()) {
                flowOf(emptyList())
            } else {
                repository.searchMovies(searchQuery)
            }
        }
        .stateIn(                  // 4. Cache the latest value for the UI
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}