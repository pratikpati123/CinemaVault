package com.example.cinemavault.domain.repository

import com.example.cinemavault.core.common.Resource
import com.example.cinemavault.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getTrendingMovies(): Flow<List<Movie>>

    fun getNowPlayingMovies(): Flow<List<Movie>>

    fun getBookmarkedMovies(): Flow<List<Movie>>

    suspend fun refreshTrendingMovies(): Resource<Unit>

    suspend fun refreshNowPlayingMovies(): Resource<Unit>

    // --- INDIVIDUAL OPERATIONS ---

    suspend fun getMovieById(id: Int): Resource<Movie>

    suspend fun toggleBookmark(id: Int, isBookmarked: Boolean)

    suspend fun searchMovies(query: String): Flow<List<Movie>>
}