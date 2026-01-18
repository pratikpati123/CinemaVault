package com.example.cinemavault.domain.repository

import com.example.cinemavault.core.common.Resource
import com.example.cinemavault.domain.model.Movie
import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing movie data.
 * This repository provides methods for fetching movies from different sources (remote API, local database).
 */
interface MovieRepository {
    /**
     * Fetches the list of trending movies.
     *
     * @param forceFetch If true, forces a fetch from the remote API, otherwise fetches only if local data is empty.
     * @return A [Flow] of [Resource] containing the list of trending [Movie]s.
     */
    fun getTrendingMovies(forceFetch: Boolean): Flow<Resource<List<Movie>>>

    /**
     * Fetches the list of movies that are currently playing in theaters.
     *
     * @param forceFetch If true, forces a fetch from the remote API, otherwise fetches only if local data is empty.
     * @return A [Flow] of [Resource] containing the list of now playing [Movie]s.
     */
    fun getNowPlayingMovies(forceFetch: Boolean): Flow<Resource<List<Movie>>>

    /**
     * Fetches the list of bookmarked movies from the local database.
     *
     * @return A [Flow] of a list of bookmarked [Movie]s.
     */
    fun getBookmarkedMovies(): Flow<List<Movie>>

    /**
     * Fetches a movie by its ID.
     *
     * @param id The ID of the movie to fetch.
     * @return A [Resource] containing the [Movie].
     */
    suspend fun getMovieById(id: Int): Resource<Movie>

    /**
     * Toggles the bookmark status of a movie.
     *
     * @param id The ID of the movie to update.
     * @param isBookmarked The new bookmark status.
     */
    suspend fun toggleBookmark(id: Int, isBookmarked: Boolean)

    /**
     * Searches for movies based on a query string.
     *
     * @param query The search query.
     * @return A [Flow] of a list of [Movie]s matching the query.
     */
    suspend fun searchMovies(query: String): Flow<List<Movie>>
}
