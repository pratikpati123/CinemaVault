package com.example.cinemavault.domain.model

/**
 * Represents a movie in the domain layer.
 * This is the model that the UI will interact with.
 *
 * @property id The unique identifier of the movie.
 * @property title The title of the movie.
 * @property overview A brief summary of the movie.
 * @property posterUrl The URL of the movie's poster image.
 * @property backdropUrl The URL of the movie's backdrop image.
 * @property releaseDate The release date of the movie.
 * @property voteAverage The average vote score of the movie.
 * @property isBookmarked Whether the movie is bookmarked by the user.
 */
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val isBookmarked: Boolean
)
