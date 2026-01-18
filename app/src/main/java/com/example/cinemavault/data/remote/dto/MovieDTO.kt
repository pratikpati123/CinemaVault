package com.example.cinemavault.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object for a movie from the remote API.
 *
 * @property id The unique identifier of the movie.
 * @property title The title of the movie.
 * @property overview A brief summary of the movie.
 * @property posterPath The path to the movie's poster image.
 * @property backdropPath The path to the movie's backdrop image.
 * @property releaseDate The release date of the movie.
 * @property voteAverage The average vote score of the movie.
 */
@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,

    @SerialName("poster_path")
    val posterPath: String?,

    @SerialName("backdrop_path")
    val backdropPath: String?,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("vote_average")
    val voteAverage: Double? = 0.0
)

/**
 * Data transfer object for a response containing a list of movies from the remote API.
 *
 * @property page The current page number of the results.
 * @property results The list of movies for the current page.
 */
@Serializable
data class MovieResponseDto(
    val page: Int,
    val results: List<MovieDto>
)
