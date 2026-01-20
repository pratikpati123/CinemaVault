package com.example.cinemavault.data.remote.dto

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for The Movie Database (TMDb).
 */
interface TmdbApi {

    /**
     * Fetches the list of movies currently playing in theaters.
     *
     * @param page The page number of the results to fetch.
     * @param language The language of the results.
     * @return A [MovieResponseDto] containing the list of now playing movies.
     */
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1, @Query("language") language: String = "en-US"): MovieResponseDto

    /**
     * Fetches the list of trending movies for the day.
     *
     * @param language The language of the results.
     * @return A [MovieResponseDto] containing the list of trending movies.
     */
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(@Query("language") language: String = "en-US"): MovieResponseDto

    /**
     * Searches for movies based on a query string.
     *
     * @param query The search query.
     * @param page The page number of the results to fetch.
     * @param includeAdult Whether to include adult content in the results.
     * @return A [MovieResponseDto] containing the list of movies matching the query.
     */
    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String, @Query("page") page: Int = 1, @Query("include_adult") includeAdult: Boolean = false): MovieResponseDto

    /**
     * Fetches the details of a specific movie.
     *
     * @param movieId The ID of the movie to fetch.
     * @param language The language of the results.
     * @return A [MovieDTO] containing the details of the movie.
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int, @Query("language") language: String = "en-US"): MovieDTO
}