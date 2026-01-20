package com.example.cinemavault.data.mapper

import com.example.cinemavault.core.common.Constants
import com.example.cinemavault.data.local.entity.MovieEntity
import com.example.cinemavault.data.remote.dto.MovieDTO
import com.example.cinemavault.domain.model.Movie

/**
 * Converts a [MovieDTO] to a [MovieEntity].
 *
 * @param isTrending Whether the movie is trending.
 * @param isNowPlaying Whether the movie is now playing.
 * @param isBookmarked Whether the movie is bookmarked.
 * @return The corresponding [MovieEntity].
 */
fun MovieDTO.toEntity(
    isTrending: Boolean = false,
    isNowPlaying: Boolean = false,
    isBookmarked: Boolean = false
): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage ?: 0.0,
        isTrending = isTrending,
        isNowPlaying = isNowPlaying,
        isBookmarked = isBookmarked
    )
}

/**
 * Converts a [MovieEntity] to a [Movie] domain model.
 *
 * @return The corresponding [Movie] domain model.
 */
fun MovieEntity.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.let { "${Constants.IMAGE_BASE_URL}$it" },
        backdropUrl = backdropPath?.let { "${Constants.IMAGE_BASE_URL}$it" },
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        isBookmarked = isBookmarked
    )
}
